package hotelmania.group2.hotel;
import hotelmania.group2.dao.BookingDAO;
import hotelmania.ontology.BookRoom;
import hotelmania.ontology.Booking;
import hotelmania.ontology.Contract;
import hotelmania.ontology.CreateAccount;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.RegistrationRequest;
import hotelmania.ontology.SharedAgentsOntology;
import hotelmania.ontology.SignContract;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AgHotel2 extends Agent {
	private static final long serialVersionUID = 2893904717857535232L;

	static final String REGISTRATION_REQUEST = "Registration";
	
	static final String SIGNCONTRACT_REQUEST = "SIGNCONTRACT_REQUEST";

	static final String CREATEACCOUNT = "CREATEACCOUNT";
	static final String BOOKROOM = "BOOKROOM";

	private BookingDAO bookDao;

	/*
	 * Codec for the SL language used
	 */
	private Codec codec = new SLCodec();

	/*
	 * External communication protocol's ontology
	 */
	private Ontology ontology = SharedAgentsOntology.getInstance();

	/*
	 * Agent Attributes
	 */

	String name;
	
	/**
	 * hotelmania reference
	 */
	AID agHotelmania;
	AID agBank;
	AID agClient;
	boolean registered;

	/**
	 * agency reference
	 */
	private AID agAgency;
	private boolean agencyFound;

	public boolean newDay;



	@Override
	protected void setup() {
		System.out.println(getLocalName() + ": HAS ENTERED");

		bookDao = new BookingDAO();

		/*
		 * Register codec and ontology in ContentManager
		 */
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);

		// TODO Subscribe to day event

		addBehaviour(new RegisterInHotelmaniaBehavior(this));
		addBehaviour(new ReceiveAcceptanceMsgBehavior(this));
		addBehaviour(new ReceiveRejectionMsgBehavior(this));
		addBehaviour(new ReceiveNotUnderstoodMsgBehavior(this));

		addBehaviour(new HireDailyStaffBehavior(this));

		addBehaviour(new MakeRoomBookingBehavior(this));

		addBehaviour(new ProvideRoomInfoBehavior(this));

		addBehaviour(new CreateBankAccountBehavior(this));

		// TODO Consult account status
	}

	// --------------------------------------------------------
	// BEHAVIOURS
	// --------------------------------------------------------

	private final class RegisterInHotelmaniaBehavior extends SimpleBehaviour {
		private static final long serialVersionUID = 1256090117313507535L;
		private boolean hotelmaniaFound = false;

		private RegisterInHotelmaniaBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			// Generate hotel name
			name = getName();

			// Search hotelmania agent
			agHotelmania = locateHotelmaniaAgent();

			// Register hotel
			if (hotelmaniaFound) {
				registerHotel();
			}
		}

		private void registerHotel() {
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(agHotelmania);
			msg.setLanguage(codec.getName());
			msg.setOntology(ontology.getName());

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
			} catch (CodecException ce) {
				ce.printStackTrace();
			} catch (OntologyException oe) {
				oe.printStackTrace();
			}
		}

		/**
		 * Search hotelmania agent with the Directory Facilitator
		 * 
		 * @return
		 */
		private AID locateHotelmaniaAgent() {
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(REGISTRATION_REQUEST);
			dfd.addServices(sd);

			try {
				// It finds agents of the required type
				DFAgentDescription[] agents = DFService.search(myAgent, dfd);

				if (agents != null && agents.length > 0) {

					for (DFAgentDescription description : agents) {
						hotelmaniaFound = true;
						return (AID) description.getName(); // only expects 1
															// agent...
					}
				}
			} catch (Exception e) {
				hotelmaniaFound = false;
			}
			return null;
		}

		/*
		CREATE SUBSCRIPTION MESSAGE
		 DFAgentDescription template = // fill the template
 Behaviour b = new SubscriptionInitiator(
 this, 
 DFService.createSubscriptionMessage(this, getDefaultDF(), template, null)) 
 {
 protected void handleInform(ACLMessage inform) {
 try {
 DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
 // do something
  }
  catch (FIPAException fe) {
  fe.printStackTrace();
  }
  }
  };
  addBehaviour(b);
		*/
		@Override
		public boolean done() {
			return hotelmaniaFound;
		}

		private String getName() {
			return "Hotel2";
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
					.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));

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
					.MatchPerformative(ACLMessage.REJECT_PROPOSAL));

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

	private final class HireDailyStaffBehavior extends CyclicBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8769912917130729651L;

		public HireDailyStaffBehavior(AgHotel2 agHotel) {
			super(agHotel);
		}

		@Override
		public void action() {
			// Search agency agent
			if (!agencyFound) {
				agAgency = locateAgencyAgent();
			}
		}

		/**
		 * Locate the Agency agent with the Directory Facilitator
		 * 
		 * @return agency AID
		 */
		private AID locateAgencyAgent() {
			DFAgentDescription agentDescription = new DFAgentDescription();
			ServiceDescription service = new ServiceDescription();
			service.setType(SIGNCONTRACT_REQUEST);
			agentDescription.addServices(service);

			try {
				// It finds agents of the required type
				DFAgentDescription[] agents = DFService.search(myAgent, agentDescription);

				if (agents != null && agents.length > 0) {

					for (DFAgentDescription description : agents) {
						agencyFound = true;
						return (AID) description.getName(); // only expects 1 agent
					}
				}
			} catch (Exception e) {
				agencyFound = false;
			}
			return null;
		}

		/**
		 * This is invoked on a NewDay event.
		 * 
		 * @param currentDate "today" date given by simulator.
		 */
		private void hireDailyStaff(Date currentDate) 
		{
//			// Ensure I can contact agency
//			if (!agencyFound) {
//				return false;
//			}
//			
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(agAgency);
			msg.setLanguage(codec.getName());
			msg.setOntology(ontology.getName());

			SignContract request = new SignContract();
			
			Hotel hotel = new Hotel();
			hotel.setHotel_name(name);
			request.setHotel(hotel);
			
			request.setContract(getInitialContract());
			
			
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

		private Contract getInitialContract() {
			Contract c = new Contract();
			c.setCooker_1stars(5);
			c.setCooker_2stars(5);
			c.setCooker_3stars(5);
			c.setRecepcionist_experienced(2);
			c.setRecepcionist_novice(2);
			c.setRoom_service_staff(30);
			return c ;
		}

	}

	private final class MakeRoomBookingBehavior extends CyclicBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -390060690778340930L;
		private static final int VALID_REQ = 0;
		private static final int REJECT_REQ = -1;
		private static final int NOT_UNDERSTOOD_REQ = 1;

		// private boolean clientFound = false;

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

		/**
		 * @return
		 */
		// private AID locateClientAgent() {
		// DFAgentDescription dfd = new DFAgentDescription();
		// ServiceDescription sd = new ServiceDescription();
		// sd.setType(BOOKROOM);
		// dfd.addServices(sd);
		//
		// try {
		// // It finds agents of the required type
		// DFAgentDescription[] agents = DFService.search(myAgent, dfd);
		//
		// if (agents != null && agents.length > 0) {
		//
		// for (DFAgentDescription description : agents) {
		// clientFound = true;
		// return (AID) description.getName(); // only expects 1
		// // agent...
		// }
		// }
		// } catch (Exception e) {
		// clientFound = false;
		// }
		// return null;
		// }

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
			// TODO Auto-generated method stub

		}

	}

	/**
	 * @author user
	 *
	 */
	private final class CreateBankAccountBehavior extends CyclicBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1955222376582492939L;
		private boolean bankFound = false;

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
			// Generate hotel name
			name = getName();

			// Search bank agent
			agBank = locateBankAgent();

			// Create hotel account
			if (bankFound) {
				createHotelAccount();
			}

		}

		/**
		 * 
		 */
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
			} catch (CodecException ce) {
				ce.printStackTrace();
			} catch (OntologyException oe) {
				oe.printStackTrace();
			}
		}

		/**
		 * @return
		 */
		private AID locateBankAgent() {
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(CREATEACCOUNT);
			dfd.addServices(sd);

			try {
				// It finds agents of the required type
				DFAgentDescription[] agents = DFService.search(myAgent, dfd);

				if (agents != null && agents.length > 0) {

					for (DFAgentDescription description : agents) {
						bankFound = true;
						return (AID) description.getName(); // only expects 1
															// agent...
					}
				}
			} catch (Exception e) {
				bankFound = false;
			}
			return null;
		}
	}

}