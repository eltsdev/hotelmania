package hotelmania.group2.platform;

import hotelmania.group2.dao.Contract;
import hotelmania.group2.dao.ContractDAO;
import hotelmania.ontology.SignContract;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgAgency extends MetaAgent 
{
	private static final long serialVersionUID = 2893904717857535232L;
	
	//------------------------------------------------- 
	// Agent Attributes
	//-------------------------------------------------
	
	AID agHotel;
	AID agReporter;

	@Override
	protected void setup() {
		super.setup();
		
		// Creates its own description
		registerServices(Constants.SIGNCONTRACT_ACTION);
				
		// Behaviors

		addBehaviour(new SignStaffContractWithHotelBehavior(this));
		addBehaviour(new ProvideHotelStaffInfoToClientBehavior(this));
		//TODO charge hotel account
	}

	/**
	 * This means: I AM NOT interested on this event.
	 */
	@Override
	protected boolean setRegisterForDayEvents() {
		return false;
	}
	
	// --------------------------------------------------------
	// BEHAVIOURS
	// --------------------------------------------------------

	private final class SignStaffContractWithHotelBehavior extends MetaCyclicBehaviour 
	{
		private static final long serialVersionUID = 7390814510706022198L;
		private ContractDAO contractDAO = new ContractDAO();

		public SignStaffContractWithHotelBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			// Look for messages
			ACLMessage msg = receive(
					MessageTemplate.and(
							MessageTemplate.and(
									MessageTemplate.and(
									MessageTemplate.MatchLanguage(codec.getName()), 
									MessageTemplate.MatchOntology(ontology.getName())),
									MessageTemplate.MatchProtocol(Constants.SIGNCONTRACT_PROTOCOL)),
									MessageTemplate.MatchPerformative(ACLMessage.REQUEST))
					);
	
			// If no message arrives
			if (msg == null) 
			{
				block();
				return;
			}
	
			// The ContentManager transforms the message content (string) in java objects
			try {
				ContentElement ce = getContentManager().extractContent(msg);
				
				// We expect an action inside the message
				if (ce instanceof Action)
				{
					Action agAction = (Action) ce;
					Concept conc = agAction.getAction();
					
					// If the action is Registration Request...
					if (conc instanceof SignContract)
					{
						//execute request
						ACLMessage reply = answerContractRequest(msg, (SignContract) conc);
						myAgent.send(reply);
						
						System.out.println(myName() + ": answer sent -> " + log );
					}
				}
				
			} catch (CodecException | OntologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}

		private ACLMessage answerContractRequest(ACLMessage msg, SignContract action)
		{
			System.out.println(myName()+": received "+action.getClass().getSimpleName()+" from "+msg.getSender().getLocalName());
			
			ACLMessage reply = msg.createReply();
					
			if (action != null && action.getHotel() != null ) {
				if(signContractWithHotel(action))
				{
					this.log = Constants.AGREE;
					reply.setPerformative(ACLMessage.AGREE);
				} else {
					this.log = Constants.REFUSE;
					reply.setPerformative(ACLMessage.REFUSE);
				}
			} else {
				this.log = Constants.NOT_UNDERSTOOD;
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			}
			return reply;
		}

		private boolean signContractWithHotel(SignContract intent) 
		{
			Contract newContract = new Contract(
					intent.getHotel().getHotel_name(), 
					intent.getContract().getDay(), 
					intent.getContract().getChef_1stars(),
					intent.getContract().getChef_2stars(),
					intent.getContract().getChef_2stars(),
					intent.getContract().getRecepcionist_experienced(),
					intent.getContract().getRecepcionist_novice(),
					intent.getContract().getRoom_service_staff());
			contractDAO .createContract(newContract );
			
			return true;
		}

	}

	private final class ProvideHotelStaffInfoToClientBehavior extends MetaCyclicBehaviour 
	{
		private static final long serialVersionUID = -4414753731149819352L;

		public ProvideHotelStaffInfoToClientBehavior(AgAgency me) {
			super(me);
		}

		@Override
		public void action() {
			// TODO Blank
			block();

		}
	}

	
	@Override
	public void receivedAcceptance(ACLMessage message) {
		//TODO switch by message.getProtocol()
	}

	@Override
	public void receivedReject(ACLMessage message) {
		// TODO Auto-generated method stub
		if (message.getProtocol().equals(Constants.SIGNCONTRACT_PROTOCOL)) {
		} else if (message.getProtocol().equals(Constants.CONSULTHOTELSSTAFF_PROTOCOL)) {
		}
	}

	@Override
	public void receivedNotUnderstood(ACLMessage message) {
		// TODO Auto-generated method stub
		if (message.getProtocol().equals(Constants.SIGNCONTRACT_PROTOCOL)) {
		} else if (message.getProtocol().equals(Constants.CONSULTHOTELSSTAFF_PROTOCOL)) {
		}	
	}

	/* (non-Javadoc)
	 * @see hotelmania.group2.platform.MetaAgent#receiveInform()
	 */
	@Override
	public void receivedInform(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}


}