package hotelmania.group2.platform;

import hotelmania.ontology.BookRoom;
import hotelmania.ontology.Booking;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.MakeDeposit;
import hotelmania.ontology.RateHotel;
import hotelmania.ontology.Rating;
import hotelmania.ontology.SharedAgentsOntology;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AgClient extends Agent {
	private static final long serialVersionUID = 6748170421157254696L;
	static final String BOOKROOM = "BOOKROOM";
	static final String RATEHOTEL = "RATEHOTEL";
	static final String MAKEDEPOSIT = "MAKEDEPOSIT";

	// Codec for the SL language used
	private Codec codec = new SLCodec();

	// External communication protocol's ontology
	private Ontology ontology = SharedAgentsOntology.getInstance();

	// -------------------------------------------------
	// Agent Attributes
	// -------------------------------------------------

	String name;
	AID agHotelMania;
	AID agHotel;
	AID agBank;
	boolean bookingDone;
	String actualHotel;
	private boolean hotelFound;
	private boolean hotelManiaFound;
	private boolean bankFound;

	// -------------------------------------------------
	// Setup
	// -------------------------------------------------

	@Override
	protected void setup() {
		System.out.println(getLocalName() + ": HAS ENTERED");

		// Register codec and ontology in ContentManager
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);

		// Behaviors

		// TODO subscribe day event

		addBehaviour(new RequestBookingInHotelBehavior(this));

		// TODO refuse offer

	}

	// --------------------------------------------------------
	// Behaviors
	// --------------------------------------------------------

	private final class RequestBookingInHotelBehavior extends CyclicBehaviour {
		private static final long serialVersionUID = -1417563883440156372L;

		private RequestBookingInHotelBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// TODO Esto tendria que hacerse?
			// LocateHotel
			agHotel = locateHotel();

			if (hotelFound) {
				// TODO ask room price *
				bookRoom();
			}

			// LocateHotelMania
			agHotelMania = locateHotelMania();

			if (hotelManiaFound) {

				// TODO rate hotel
				rateHotel();
			}
			// TODO request hotel info

			// TODO pay bank account
			// Locate the Bank
			agBank = locateBank();

			if (bankFound) {
				// Deposit money in the hotel account
				makeDeposit();

			}
			// If no message arrives
			block();
		}

		/**
		 * 
		 */
		private AID locateBank() {
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(MAKEDEPOSIT);
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

		/**
		 * @return
		 * 
		 * 
		 */
		private AID locateHotel() {
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(BOOKROOM);
			dfd.addServices(sd);

			try {
				// It finds agents of the required type
				DFAgentDescription[] agents = DFService.search(myAgent, dfd);

				if (agents != null && agents.length > 0) {

					for (DFAgentDescription description : agents) {
						hotelFound = true;
						return (AID) description.getName(); // only expects 1
						// agent...
					}
				}
			} catch (Exception e) {
				hotelFound = false;
			}
			return null;

		}

		/**
		 * @return
		 */
		private AID locateHotelMania() {
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(RATEHOTEL);
			dfd.addServices(sd);

			try {
				// It finds agents of the required type
				DFAgentDescription[] agents = DFService.search(myAgent, dfd);

				if (agents != null && agents.length > 0) {

					for (DFAgentDescription description : agents) {
						hotelManiaFound = true;
						return (AID) description.getName(); // only expects 1
															// agent...
					}
				}
			} catch (Exception e) {
				hotelManiaFound = false;
			}
			return null;

		}

		/**
		 * 
		 */
		private void bookRoom() {

			// TODO book room (Review everything)
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(agHotel);
			msg.setLanguage(codec.getName());
			msg.setOntology(ontology.getName());

			BookRoom action_booking = new BookRoom();

			// This part must be dynamic
			Booking booking = new Booking();
			booking.setDays(10);
			booking.setStartDay("10/04/2014");

			actualHotel = "HOTEL-1"; // TODO getHotelName()

			// As it is an action and the encoding language the SL,
			// it must be wrapped into an Action
			Action agAction = new Action(agHotel, action_booking);
			try {
				// The ContentManager transforms the java objects into strings
				getContentManager().fillContent(msg, agAction);
				send(msg);
				System.out.println(getLocalName() + ": REQUESTS MAKE BOOKING");
			} catch (CodecException ce) {
				ce.printStackTrace();
			} catch (OntologyException oe) {
				oe.printStackTrace();
			}

		}

		/**
		 * 
		 */
		private void rateHotel() {
			// TODO Auto-generated method stub
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(agHotelMania);
			msg.setLanguage(codec.getName());
			msg.setOntology(ontology.getName());

			RateHotel action_rating = new RateHotel();

			// Hotel
			Hotel hotel = new Hotel();
			hotel.setHotel_name(actualHotel);

			// This part must be dynamic
			Rating rating = new Rating();
			rating.setCleanliness_rating(10);
			rating.setCookers_rating(10);
			rating.setPrice_rating(10);
			rating.setRoom_staff_rating(10);
			action_rating.setHotel(hotel);
			action_rating.setRatings(rating);

			// As it is an action and the encoding language the SL,
			// it must be wrapped into an Action
			Action agAction = new Action(agHotelMania, action_rating);
			try {
				// The ContentManager transforms the java objects into strings
				getContentManager().fillContent(msg, agAction);
				send(msg);
				System.out.println(getLocalName() + ": REQUESTS RATE HOTEL");
			} catch (CodecException ce) {
				ce.printStackTrace();
			} catch (OntologyException oe) {
				oe.printStackTrace();
			}

		}

		/**
		 * 
		 */
		private void makeDeposit() {
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(agBank);
			msg.setLanguage(codec.getName());
			msg.setOntology(ontology.getName());

			// Hotel
			Hotel hotel = new Hotel();
			hotel.setHotel_name(actualHotel);

			// This part must be dynamic
			MakeDeposit action_deposit = new MakeDeposit();
			action_deposit.setHotel(hotel);
			action_deposit.setMoney(2000);

			// As it is an action and the encoding language the SL,
			// it must be wrapped into an Action
			Action agAction = new Action(agBank, action_deposit);
			try {
				// The ContentManager transforms the java objects into strings
				getContentManager().fillContent(msg, agAction);
				send(msg);
				System.out.println(getLocalName()
						+ ": REQUESTS PAY SERVICES TO HOTEL ACCOUNT");
			} catch (CodecException ce) {
				ce.printStackTrace();
			} catch (OntologyException oe) {
				oe.printStackTrace();
			}

		}
	}
}
