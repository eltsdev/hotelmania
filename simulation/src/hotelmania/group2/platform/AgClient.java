package hotelmania.group2.platform;

import hotelmania.ontology.BookRoom;
import hotelmania.ontology.Booking;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.MakeDeposit;
import hotelmania.ontology.RateHotel;
import hotelmania.ontology.Rating;
import hotelmania.ontology.SharedAgentsOntology;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
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
	private AgentsFinder agentsFinder = AgentsFinder.getInstance();

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
		addBehaviour(new ConsultHotelInfoBehavior(this));
		
		// TODO refuse offer

	}

	// --------------------------------------------------------
	// Behaviors
	// --------------------------------------------------------

	private final class RequestBookingInHotelBehavior extends CyclicBehaviour {
		private static final long serialVersionUID = -1417563883440156372L;
		private int messageType;
		private RequestBookingInHotelBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// LocateHotel
			agHotel = agentsFinder.locateAgent(SharedAgentsOntology.BOOKROOM,
					myAgent);
			if (agHotel != null) {// hotel found
				// TODO verify is working or not
				bookRoom(agHotel);
			}

			// LocateHotelMania
			agHotelMania = agentsFinder.locateAgent(
					SharedAgentsOntology.RATEHOTEL, myAgent);

			if (agHotelMania != null) {// hotel found
				// TODO verify is working or not
				rateHotel(agHotelMania);
			}
			// TODO request hotel info

			// Locate the Bank
			agBank = agentsFinder.locateAgent(SharedAgentsOntology.MAKEDEPOSIT,
					myAgent);

			if (agBank != null) {// hotel found
				// TODO verify is working or not
				makeDeposit(agBank);
			}

			// If no message arrives
			block();
		}

		private void bookRoom(AID hotel) {

			BookRoom action_booking = new BookRoom();

			// This part must be dynamic
			Booking booking = new Booking();
			booking.setDays(10);
			booking.setStartDay("10/04/2014");

			// TODO AGREGAR PROTOCOLO
			agentsFinder.sendRequest(this.getAgent(), hotel, action_booking,
					codec, ontology,"",ACLMessage.REQUEST);
			System.out.println(getLocalName() + ": REQUESTS MAKE BOOKING");
		}

		/**
		 * @param agHotelMania
		 */
		private void rateHotel(AID hotelMania) {
			RateHotel action_rating = new RateHotel();

			// Hotel
			Hotel hotel = new Hotel();
			// hotel.setHotel_name(actualHotel);
			hotel.setHotel_name(agHotel.getName());

			// TODO This part must be dynamic
			Rating rating = new Rating();
			rating.setCleanliness_rating(10);
			rating.setCookers_rating(10);
			rating.setPrice_rating(10);
			rating.setRoom_staff_rating(10);
			action_rating.setHotel(hotel);
			action_rating.setRatings(rating);

			// TODO AGREGAR PROTOCOLO
			agentsFinder.sendRequest(this.getAgent(), hotelMania,
					action_rating, codec, ontology,"",ACLMessage.REQUEST);
			System.out.println(getLocalName() + ": REQUESTS RATE HOTEL");

		}

		/**
		 * @param agBank
		 */
		private void makeDeposit(AID bank) {

			// Hotel
			Hotel hotel = new Hotel();
			hotel.setHotel_name(actualHotel);

			// TODO This part must be dynamic
			MakeDeposit action_deposit = new MakeDeposit();
			action_deposit.setHotel(hotel);
			action_deposit.setMoney(2000);
			messageType = ACLMessage.REQUEST;
			
			// TODO AGREGAR PROTOCOLO
			agentsFinder.sendRequest(this.getAgent(), bank, action_deposit,
					codec, ontology,"",messageType);
			System.out.println(getLocalName()
					+ ": REQUESTS PAY SERVICES TO HOTEL ACCOUNT " + messageType);

		}
	}

	private final class ConsultHotelInfoBehavior extends SimpleBehaviour {
		private static final long serialVersionUID = 1L;

		private ConsultHotelInfoBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			AID hotelmania = agentsFinder.locateAgent(
					SharedAgentsOntology.CONSULTHOTELSINFO, myAgent);
			if (hotelmania != null) {// hotel found
				this.consultHotelInfo(hotelmania);
			}
		}

		private void consultHotelInfo(AID hotelmania) {
			
			// TODO AGREGAR PROTOCOLO
			agentsFinder.sendRequest(this.getAgent(), hotelmania, null, codec,
					ontology,"",ACLMessage.QUERY_REF);
			System.out.println(getLocalName()
					+ ": REQUESTS INFORMATION ABOUT HOTELS TO HOTELMANIA");
		}

		@Override
		public boolean done() {
			return true;
		}

	}

}
