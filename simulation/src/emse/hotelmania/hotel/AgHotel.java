package emse.hotelmania.hotel;

import hotelmania.onto.Hotel;
import hotelmania.onto.RegistrationRequest;
import hotelmania.onto.SharedAgentsOntology;
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

public class AgHotel extends Agent 
{
	private static final long serialVersionUID = 2893904717857535232L;

	static final String REGISTRATION_REQUEST = "REGISTRATION_REQUEST";

	// Codec for the SL language used
	private Codec codec = new SLCodec();

	// External communication protocol's ontology
	private Ontology ontology = SharedAgentsOntology.getInstance();

	// Agent Attributes

	String name;
	AID agHotelmania;
	boolean registered;

	@Override
	protected void setup() {
		System.out.println(getLocalName() + ": HAS ENTERED");

		// Register codec and ontology in ContentManager
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		

		//TODO Subscribe to day event
		
		addBehaviour(new RegisterInHotelmaniaBehavior(this));
		addBehaviour(new ReceiveAcceptanceMsgBehavior(this));
		addBehaviour(new ReceiveRejectionMsgBehavior(this));
		addBehaviour(new ReceiveNotUnderstoodMsgBehavior(this));
		
		//TODO create bank account
		
		addBehaviour(new HireDailyStaffBehavior(this));
		
		addBehaviour(new MakeRoomBookingBehavior(this));
		
		addBehaviour(new ProvideRoomInfoBehavior(this));
		
		//TODO Consult account status
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

		@Override
		public boolean done() {
			return hotelmaniaFound;
		}

		private String getName() {
			return "myHOTEL-" + (int) Math.random() * 10;
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
				System.out.println(myAgent.getLocalName()
						+ ": received registration acceptance from "
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

		public HireDailyStaffBehavior(AgHotel agHotelWithOntology) {
			// TODO Auto-generated constructor stub
			super(agHotelWithOntology);
		}

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
		}

	}
	
	private final class MakeRoomBookingBehavior extends CyclicBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -390060690778340930L;

		public MakeRoomBookingBehavior(AgHotel agHotelWithOntology) {
			// TODO Auto-generated constructor stub
			super(agHotelWithOntology);
		}

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
		}

	}
	
	private final class ProvideRoomInfoBehavior extends CyclicBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1955222376582492939L;

		public ProvideRoomInfoBehavior(AgHotel agHotelWithOntology) {
			// TODO Auto-generated constructor stub
			super(agHotelWithOntology);
		}

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
		}

	
	}
	
}