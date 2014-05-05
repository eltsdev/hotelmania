package hotelmania.group2.platform;

import hotelmania.group2.dao.Contract;
import hotelmania.ontology.SignContract;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
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
		
		//TODO charge hotel account

		addBehaviour(new ProvideHotelStaffInfoToClientBehavior(this));

	}

	/**
	 * This means: I AM interested on this event.
	 */
	@Override
	protected boolean setRegisterForDayEvents() {
		return true;
	}
	
	@Override
	protected void doOnNewDay() {}

	
	// --------------------------------------------------------
	// BEHAVIOURS
	// --------------------------------------------------------

	private final class SignStaffContractWithHotelBehavior extends CyclicBehaviour 
	{
		private static final long serialVersionUID = 7390814510706022198L;
		
		//Types of response
		private static final int VALID_REQ = 0;
		private static final int REJECT_REQ = -1;
		private static final int NOT_UNDERSTOOD_REQ = 1;

		public SignStaffContractWithHotelBehavior(AgAgency agent) {
			super(agent);
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
						int answer = answerContractRequest(msg, (SignContract) conc);
						
						//send reply
						ACLMessage reply = msg.createReply();
						String log = "";
						switch (answer) 
						{
						case VALID_REQ:
							reply.setPerformative(ACLMessage.AGREE);
							log = "AGREE";
							break;

						case REJECT_REQ:
							reply.setPerformative(ACLMessage.REFUSE);
							log = "REFUSE";
							break;

						case NOT_UNDERSTOOD_REQ:
						default:
							reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
							log = "NOT_UNDERSTOOD";
							break;
						}
						
						myAgent.send(reply);
						
						System.out.println(myAgent.getLocalName() + ": answer sent -> " + log );
					}
				}
				
			} catch (CodecException | OntologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}

		private int answerContractRequest(ACLMessage msg, SignContract action)
		{
			System.out.println(myAgent.getLocalName()+": received "+action.getClass().getSimpleName()+" from "+(msg.getSender()).getLocalName());

			if (action != null && action.getHotel() != null ) {
				if(signContractWithHotel(action))
				{
					return 	VALID_REQ;
				}else {
					return REJECT_REQ;
				}
			}else {
				return NOT_UNDERSTOOD_REQ;					
			}
		}

		private boolean signContractWithHotel(SignContract intent) 
		{
			Contract newContract = new Contract(
					intent.getHotel().getHotel_name(), 
					today+1, 
					intent.getContract().getChef_1stars(),
					intent.getContract().getChef_2stars(),
					intent.getContract().getChef_2stars(),
					intent.getContract().getRecepcionist_experienced(),
					intent.getContract().getRecepcionist_novice(),
					intent.getContract().getRoom_service_staff());
			contractDAO.createContract(newContract );
			
			return true;
		}

	}

	private final class ProvideHotelStaffInfoToClientBehavior extends CyclicBehaviour 
	{
		private static final long serialVersionUID = -4414753731149819352L;

		public ProvideHotelStaffInfoToClientBehavior(AgAgency me) {
			super(me);
		}

		@Override
		public void action() {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void receivedAcceptance(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receivedReject(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receivedNotUnderstood(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}
}