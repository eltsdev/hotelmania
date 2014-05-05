package hotelmania.group2.hotel;
import hotelmania.group2.platform.Constants;
import hotelmania.group2.platform.MetaAgent;
import hotelmania.ontology.BookRoom;
import hotelmania.ontology.Booking;
import hotelmania.ontology.Contract;
import hotelmania.ontology.CreateAccount;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.RegistrationRequest;
import hotelmania.ontology.SignContract;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgHotel2 extends MetaAgent 
{
	private static final long serialVersionUID = 2893904717857535232L;

	//------------------------------------------------- 
	// Agent Attributes
	//-------------------------------------------------

	AID agHotelmania;
	AID agBank;
	AID agClient;
	AID agAgency;
	boolean registered;

	@Override
	protected void setup() {
		super.setup();
		
		// Generate hotel name
		name = 	"Hotel2";
		
		// Search agency agent
		agAgency = locateAgent(Constants.SIGNCONTRACT_ACTION, this);
		
		// Search hotelmania agent
		agHotelmania = locateAgent(Constants.REGISTRATION_ACTION, this);
		
		// Search bank agent
		agBank = locateAgent(Constants.CREATEACCOUNT_ACTION, this);

		addBehaviour(new RegisterInHotelmaniaBehavior(this));
//		addBehaviour(new ReceiveAcceptanceMsgBehavior(this));
//		addBehaviour(new ReceiveRejectionMsgBehavior(this));
//		addBehaviour(new ReceiveNotUnderstoodMsgBehavior(this));

//		NOT NEEDED: addBehaviour(new HireDailyStaffBehavior(this));

//		addBehaviour(new MakeRoomBookingBehavior(this));
//
//		addBehaviour(new ProvideRoomInfoBehavior(this));
//
//		addBehaviour(new CreateBankAccountBehavior(this));

		// TODO Consult account status
	}
	
	/**
	 * This means: I AM interested on this event.
	 */
	@Override
	protected boolean setRegisterForDayEvents() {
		return true;
	}

	@Override
	protected void doOnNewDay() {
		if (agAgency == null) {
			agAgency = locateAgent(Constants.SIGNCONTRACT_ACTION, this);
		}
		// Search agency agent
		
		hireDailyStaffBehaviorAction();
	}

	// --------------------------------------------------------
	// BEHAVIOURS
	// --------------------------------------------------------

	private final class RegisterInHotelmaniaBehavior extends SimpleBehaviour {
		private static final long serialVersionUID = 1256090117313507535L;
		private boolean registration = false;

		private RegisterInHotelmaniaBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			// Register hotel
			if (agHotelmania==null) {
				block();
				return;
			}
			
			registerHotel();
		}

		private void registerHotel() {
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(agHotelmania);
			msg.setLanguage(codec.getName());
			msg.setOntology(ontology.getName());
			msg.setProtocol(Constants.REGISTRATION_PROTOCOL);

			RegistrationRequest register = new RegistrationRequest();
			Hotel hotel = new Hotel();
			hotel.setHotel_name(name);
			register.setHotel(hotel);

			// As it is an action and the encoding language the SL,
			// it must be wrapped into an Action
			Action agAction = new Action(agHotelmania, register);
			try {
				// The ContentManager transforms the java objects into strings
				getContentManager().fillContent(msg, agAction);
				send(msg);
				System.out.println(getLocalName() + ": REQUESTS REGISTRATION");
				
				registration = true; //FIXME this must be done when the Acceptance is received.
				
				
			} catch (CodecException ce) {
				ce.printStackTrace();
			} catch (OntologyException oe) {
				oe.printStackTrace();
			}
		}

		@Override
		public boolean done() {
			return registration;
		}
	}

	private final class ReceiveAcceptanceMsgBehavior extends CyclicBehaviour {
		private static final long serialVersionUID = -4878774871721189228L;

		private ReceiveAcceptanceMsgBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// Waits for acceptance messages
			ACLMessage msg = receive(MessageTemplate
					.MatchPerformative(ACLMessage.AGREE));

			if (msg != null) {
				// If an acceptance arrives...
				registered = true;
				String request = "*Request*" ;
				System.out.println(myAgent.getLocalName()
						+ ": received "+request +" acceptance from "
						+ (msg.getSender()).getLocalName());
			} else {
				// If no message arrives
				block();
			}

		}
	}

	private final class ReceiveRejectionMsgBehavior extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		private ReceiveRejectionMsgBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// Waits for rejection message
			ACLMessage msg = receive(MessageTemplate
					.MatchPerformative(ACLMessage.REFUSE));

			if (msg != null) {
				// If a rejection arrives...
				System.out.println(myAgent.getLocalName()
						+ ": received work rejection from "
						+ (msg.getSender()).getLocalName());
			} else {
				// If no message arrives
				block();
			}

		}
	}

	private final class ReceiveNotUnderstoodMsgBehavior extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		private ReceiveNotUnderstoodMsgBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// Waits for estimations not understood
			ACLMessage msg = receive(MessageTemplate
					.MatchPerformative(ACLMessage.NOT_UNDERSTOOD));
			if (msg != null) {
				// If a not understood message arrives...
				System.out.println(myAgent.getLocalName()
						+ ": received NOT_UNDERSTOOD from "
						+ (msg.getSender()).getLocalName());
			} else {
				// If no message arrives
				block();
			}

		}
	}

	/**
	 * @author user
	 *
	 */
	private final class MakeRoomBookingBehavior extends CyclicBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -390060690778340930L;
		private static final int VALID_REQ = 0;
		private static final int REJECT_REQ = -1;
		private static final int NOT_UNDERSTOOD_REQ = 1;

		// private boolean clientFound = false;

		/**
		 * @param agHotel
		 */
		public MakeRoomBookingBehavior(AgHotel2 agHotel) {
			
			super(agHotel);
		}

		@Override
		public void action() {
			/*
			 * Look for messages
			 */
			ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));

			/*
			 * If no message arrives
			 */
			if (msg == null) {
				block();
				return;
			}

			/*
			 * The ContentManager transforms the message content (string) in
			 */
			try {
				ContentElement ce = getContentManager().extractContent(msg);

				// We expect an action inside the message
				if (ce instanceof Action) {
					Action agAction = (Action) ce;
					Concept conc = agAction.getAction();

					// If the action is BookRoom...
					if (conc instanceof BookRoom) {
						// execute request
						int answer = answerBookingRequest(msg, (BookRoom) conc);

						// send reply
						ACLMessage reply = msg.createReply();
						String log = "";
						switch (answer) {
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

						System.out.println(myAgent.getLocalName()
								+ ": answer sent -> " + log);
					}
				}

			} catch (CodecException | OntologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**
		 * @param msg
		 * @param conc
		 * @return
		 */
		private int answerBookingRequest(ACLMessage msg, BookRoom bookData) {
			System.out.println(myAgent.getLocalName()
					+ ": received Registration Request from "
					+ (msg.getSender()).getLocalName());

			if (bookData != null) {
				if (bookRoom(bookData.getBooking())) {
					return VALID_REQ;
				} else {
					return REJECT_REQ;
				}
			} else {
				return NOT_UNDERSTOOD_REQ;

			}
		}

		/**
		 * 
		 */
		private boolean bookRoom(Booking book) {

			bookDao.booking(book.getDays(), book.getStartDay());
			return true;

		}

	}

	private final class ProvideRoomInfoBehavior extends CyclicBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1955222376582492939L;

		public ProvideRoomInfoBehavior(AgHotel2 agHotelWithOntology) {
			// TODO Auto-generated constructor stub
			super(agHotelWithOntology);
		}

		@Override
		public void action() {
			//TODO
			block();
		}

	}

	/**
	 * @author user
	 *
	 */
	private final class CreateBankAccountBehavior extends SimpleBehaviour {
	
		private static final long serialVersionUID = 1955222376582492939L;
		private boolean done = false;
	
		/**
		 * @param agHotelWithOntology
		 */
		public CreateBankAccountBehavior(AgHotel2 agHotelWithOntology) {
			super(agHotelWithOntology);
		}
	
		/*
		 * (non-Javadoc)
		 * 
		 * @see jade.core.behaviours.Behaviour#action()
		 */
		@Override
		public void action() {
			// Create hotel account
			if (agBank !=null ) {
				createHotelAccount();
			}
		}
	
		private void createHotelAccount() {
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(agBank);
			msg.setLanguage(codec.getName());
			msg.setOntology(ontology.getName());
	
			CreateAccount action_account = new CreateAccount();
			Hotel hotel = new Hotel();
			hotel.setHotel_name(name);
			action_account.setHotel(hotel);
			action_account.setBalance(1000000);
	
			// As it is an action and the encoding language the SL,
			// it must be wrapped into an Action
			Action agAction = new Action(agBank, action_account);
			try {
				// The ContentManager transforms the java objects into strings
				getContentManager().fillContent(msg, agAction);
				send(msg);
				System.out.println(getLocalName()
						+ ": REQUESTS CREATION ACCOUNT");
				
				done  = true;
			} catch (CodecException ce) {
				ce.printStackTrace();
			} catch (OntologyException oe) {
				oe.printStackTrace();
			}
		}
	
		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return done;
		}
	}

	/*
	private final class HireDailyStaffBehavior extends CyclicBehaviour 
	{
		private static final long serialVersionUID = -8769912917130729651L;
	
		public HireDailyStaffBehavior(AgHotel2 agHotel) {
			super(agHotel);
		}
	
		@Override
		public void action() {
//			hireDailyStaffBehaviorAction();
		}
	
	}
	*/

	/**
	 * Behavior for hiring the staff 
	 */
	void hireDailyStaffBehaviorAction() {
		// Ensure I can contact agency
		if (agAgency == null) {
			return;
		}
		
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(agAgency);
		msg.setLanguage(codec.getName());
		msg.setOntology(ontology.getName());
		msg.setProtocol(Constants.SIGNCONTRACT_PROTOCOL);
	
		SignContract request = new SignContract();
		
		Hotel hotel = new Hotel();
		hotel.setHotel_name(name);
		request.setHotel(hotel);
		request.setContract(hireDailyStaff(today+1));
		
		// As it is an action and the encoding language the SL,
		// it must be wrapped into an Action
		Action agAction = new Action(agHotelmania, request);
		try {
			// The ContentManager transforms the java objects into strings
			getContentManager().fillContent(msg, agAction);
			send(msg);
			System.out.println(getLocalName() + ": REQUESTS "+ request.getClass().getSimpleName());
		} catch (CodecException ce) {
			ce.printStackTrace();
		} catch (OntologyException oe) {
			oe.printStackTrace();
		}
	}

	/**
	 * This is invoked on a NewDay event.
	 * @param date of the contract
	 */
	Contract hireDailyStaff(int day) 
	{
		Contract contract;
		if (day == Constants.FIRST_DAY) {
			contract = getInitialContract();
		} else {
			contract = buildNewContract();
		}
	
		return contract;
	}

	Contract buildNewContract() {
		//Contract old = dao.getContractsByHotel(name).get(0);
		Contract c = new Contract();
		c.setChef_1stars(5);
		c.setChef_2stars(5);
		c.setChef_3stars(5);
		c.setRecepcionist_experienced(2);
		c.setRecepcionist_novice(2);
		c.setRoom_service_staff(20);
		return c ;
	}

	/**
	 * Default values for staff hiring
	 * @return
	 */
	Contract getInitialContract() {
		Contract c = new Contract();
		c.setChef_1stars(5);
		c.setChef_2stars(5);
		c.setChef_3stars(5);
		c.setRecepcionist_experienced(2);
		c.setRecepcionist_novice(2);
		c.setRoom_service_staff(20);
		return c ;
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
