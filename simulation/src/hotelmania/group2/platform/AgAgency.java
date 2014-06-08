package hotelmania.group2.platform;

import hotelmania.group2.behaviours.GenericServerResponseBehaviour;
import hotelmania.group2.dao.Contract;
import hotelmania.group2.dao.ContractDAO;
import hotelmania.ontology.HotelStaffQueryRef;
import hotelmania.ontology.SignContract;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.Predicate;
import jade.content.abs.AbsContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.lang.acl.ACLMessage;

public class AgAgency extends AbstractAgent {
	private static final long serialVersionUID = 2893904717857535232L;
	private ContractDAO contractDAO = new ContractDAO();
	

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

	private final class SignStaffContractWithHotelBehavior extends GenericServerResponseBehaviour 
	{
		private static final long serialVersionUID = 7390814510706022198L;
		

		public SignStaffContractWithHotelBehavior(AbstractAgent agAgency) {
			super(agAgency,Constants.SIGNCONTRACT_PROTOCOL, ACLMessage.REQUEST);
		}

		@Override
		protected ACLMessage doSendResponse(ACLMessage msg) {
			myAgent.getLog().logReceivedMsg(msg);
			
			// The ContentManager transforms the message content (string) in java objects
			try {
				ContentElement ce = getContentManager().extractContent(msg);
				
				// We expect an action inside the message
				if (ce instanceof Action){
					Action agAction = (Action) ce;
					Concept conc = agAction.getAction();
					
					// If the action is Registration Request...
					if (conc instanceof SignContract){
						//execute request
						ACLMessage reply = answerContractRequest(msg, (SignContract) conc);
						myAgent.send(reply);
						myAgent.getLog().logSendReply(reply);

						if (reply.getPerformative()==ACLMessage.AGREE) {
							reply.setPerformative(ACLMessage.INFORM);
						}else if (reply.getPerformative()==ACLMessage.REFUSE) {
							reply.setPerformative(ACLMessage.FAILURE);
						}
						return reply;
					}
					
				}
				
			} catch (CodecException | OntologyException e) {
				Logger.logError(myName() + " Not receive contract");
				e.printStackTrace();
			}
			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			reply.setContent("No sending the right Ontology");
			return reply;
			
			
		}

		/**
		 * @param msg
		 * @param action
		 * @return
		 */
		private ACLMessage answerContractRequest(ACLMessage msg, SignContract action)
		{
			ACLMessage reply = msg.createReply();
					
			if (action != null && action.getHotel() != null ) {
				if(signContractWithHotel(action))
				{
					reply.setPerformative(ACLMessage.AGREE);
				} else {
					reply.setPerformative(ACLMessage.REFUSE);
				}
			} else {
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

	private final class ProvideHotelStaffInfoToClientBehavior extends GenericServerResponseBehaviour 
	{
		private static final long serialVersionUID = -4414753731149819352L;

		public ProvideHotelStaffInfoToClientBehavior(AbstractAgent agAgency) {
			super(agAgency, Constants.CONSULTHOTELSSTAFF_PROTOCOL, ACLMessage.QUERY_REF);
		}

		@Override
		protected ACLMessage doSendResponse(ACLMessage message) {
			
			Predicate predicate = getPredicateFromMessage(message);
			
			if (predicate instanceof HotelStaffQueryRef) {
				return answerHotelStaff(message,(HotelStaffQueryRef) predicate);
			}else {
				ACLMessage reply = message.createReply();
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				reply.setContent("No sending the right Ontology");
				return reply;
				
			}
		}

		/**
		 * @param message
		 * @param predicate
		 * @return
		 */
		private ACLMessage answerHotelStaff(ACLMessage message,	HotelStaffQueryRef hotelQueryStaff) {
			ACLMessage reply = message.createReply();

			//missing parameters?
			if (hotelQueryStaff == null) {
				
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				reply.setContent("There are missing parameters: NumberOfClientsQueryRef action or hotel name");

			} else if (hotelQueryStaff.getHotel()==null) {
				//invalid day in request?
				reply.setPerformative(ACLMessage.REFUSE);
				reply.setContent("There is no hotel");
				

			} else {
				//request is valid
				
				Contract contractDao =contractDAO.getCurrentContractsByHotel(hotelQueryStaff.getHotel().getHotel_name(), hotelQueryStaff.getDay());
				hotelmania.ontology.Contract contract = contractDao.getConcept();
				reply.setPerformative(ACLMessage.INFORM);
//				getContentManager().fillContent(reply, (AbsContentElement) contract);
				//sendRequest(reply.getSender(), contract, Constants.CONSULTHOTELSSTAFF_PROTOCOL, ACLMessage.REQUEST);
			}
			return reply;
		}

		

		}

}