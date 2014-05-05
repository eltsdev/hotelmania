package hotelmania.group2.platform;

import hotelmania.ontology.BookRoom;
import hotelmania.ontology.Booking;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.HotelsInfoRequest;
import hotelmania.ontology.MakeDeposit;
import hotelmania.ontology.RateHotel;
import hotelmania.ontology.Rating;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class AgClient extends MetaAgent {
	
	private static final long serialVersionUID = 6748170421157254696L;


	//------------------------------------------------- 
	// Agent Attributes
	//-------------------------------------------------
	protected static final boolean registerForDayEvents = true;

	private AID agHotelMania;
	private AID agHotel;
	private AID agBank;
	private boolean bookingDone;
	private String actualHotel;

	// -------------------------------------------------
	// Setup
	// -------------------------------------------------
	
	@Override
	protected void setup() {
		super.setup();
		
		
		// LocateHotelMania
		agHotelMania = locateAgent(Constants.RATEHOTEL_ACTION, this);
		// Locate the Bank
		agBank = locateAgent(Constants.MAKEDEPOSIT_ACTION, this);

		
		// Behaviors

		// TODO subscribe day event

		addBehaviour(new RequestBookingInHotelBehavior(this));
		addBehaviour(new ConsultHotelInfoBehavior(this));
		
		// TODO refuse offer
	}


	public void receivedAcceptance(ACLMessage message) {
		System.out.println("hola");
	}

	public void receivedReject(ACLMessage message) {

	}

	public void receivedNotUnderstood(ACLMessage message) {

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
	// Behaviors
	// --------------------------------------------------------

	private final class RequestBookingInHotelBehavior extends CyclicBehaviour {

		private static final long serialVersionUID = -1417563883440156372L;
		private int messageType;

		private RequestBookingInHotelBehavior(Agent a) {
			super(a);
		}

		public void action() {
			if (AgClient.this.bookingDone) {
				myAgent.doDelete(); // TODO TEST
			}
			// LocateHotel
			agHotel = locateAgent(Constants.BOOKROOM_ACTION, myAgent);
			if (agHotel != null) {// hotel found
				// TODO verify is working or not
				bookRoom(agHotel);
			}

			if (agHotelMania != null && agHotel != null ) {// hotel found
				// TODO verify is working or not
				rateHotel(agHotelMania);
			}
			// TODO request hotel info

			if (agBank != null) {// hotel found
				// TODO verify is working or not
				makeDeposit(agBank);
			}

			//TODO: FIX
			// If no message arrives
//			block();
		}

		private void bookRoom(AID hotel) {

			BookRoom action_booking = new BookRoom();

			// This part must be dynamic
			Booking booking = new Booking();
			booking.setDays(10);
			booking.setStartDay("10/04/2014");

			// TODO AGREGAR PROTOCOLO
			sendRequest(this.getAgent(), hotel, action_booking,
					codec, ontology, Constants.BOOKROOM_PROTOCOL, ACLMessage.REQUEST);
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
			sendRequest(this.getAgent(), hotelMania,
					action_rating, codec, ontology, Constants.RATEHOTEL_PROTOCOL,ACLMessage.REQUEST);
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
			sendRequest(this.getAgent(), bank, action_deposit,
					codec, ontology,Constants.MAKEDEPOSIT_PROTOCOL,messageType);
			System.out.println(getLocalName()
					+ ": REQUESTS PAY SERVICES TO HOTEL ACCOUNT " + messageType);

		}
	}

	private final class ConsultHotelInfoBehavior extends SimpleBehaviour {
		private static final long serialVersionUID = 1L;
		private boolean couldFindHotelMania = false;
		private ConsultHotelInfoBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			AID hotelmania = locateAgent(Constants.CONSULTHOTELSINFO_ACTION, myAgent);
			if (hotelmania != null) {// hotel found
				this.consultHotelInfo(hotelmania);
				this.couldFindHotelMania = true;
			} else {
				System.out.println(getLocalName() + " couldn't locate hotelmania in behaviour ConsultHotelInfoBehavior");
			}
		}

		private void consultHotelInfo(AID hotelmania) {
			System.out.println(getLocalName() + ": REQUESTING INFORMATION ABOUT HOTELS TO HOTELMANIA");
			HotelsInfoRequest request = new HotelsInfoRequest();

			sendRequest(this.getAgent(), hotelmania, request, codec, ontology,Constants.CONSULTHOTELSINFO_PROTOCOL,ACLMessage.QUERY_REF);
			System.out.println(getLocalName() + ": REQUESTS INFORMATION ABOUT HOTELS TO HOTELMANIA");
		}

		@Override
		public boolean done() {
			return this.couldFindHotelMania;
		}

	}

}
