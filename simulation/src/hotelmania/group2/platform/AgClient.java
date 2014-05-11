package hotelmania.group2.platform;

import hotelmania.ontology.BookRoom;
import hotelmania.ontology.Booking;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.HotelsInfoRequest;
import hotelmania.ontology.MakeDeposit;
import hotelmania.ontology.NumberOfClientsQueryRef;
import hotelmania.ontology.RateHotel;
import hotelmania.ontology.Rating;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class AgClient extends MetaAgent {
	
	private static final long serialVersionUID = 6748170421157254696L;

	private boolean bookingDone;
	private String actualHotel;

	// -------------------------------------------------
	// Setup
	// -------------------------------------------------
	
	@Override
	protected void setup() {
		super.setup();
		
		// Behaviors

		//addBehaviour(new RequestBookingInHotelBehavior(this));
		//addBehaviour(new ConsultHotelInfoBehavior(this));
		addBehaviour(new ConsultHotelNumberOfClientsBehavior(this));
		// TODO refuse offer
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
		//TODO to implement...
	}


	// --------------------------------------------------------
	// Behaviors
	// --------------------------------------------------------

	private final class RequestBookingInHotelBehavior extends MetaSimpleBehaviour {

		private static final long serialVersionUID = -1417563883440156372L;
		private AID agHotel;
		private AID agHotelMania;
		private AID agBank;
		

		private RequestBookingInHotelBehavior(Agent a) {
			super(a);
		}

		public void action() {
			this.setDone(true);
			
			/* TODO implement:			
			if (AgClient.this.bookingDone) {
				myAgent.doDelete(); // TODO Test if this works.
			}
			
			// LocateHotel
			if (agHotel == null) {
				agHotel = locateAgent(Constants.BOOKROOM_ACTION, myAgent);
			}else {
				bookRoom(agHotel);
				this.setDone(true);
			}

			if (agHotelMania == null) {
				agHotelMania = locateAgent(Constants.REGISTRATION_ACTION, myAgent);
			}else {
				rateHotel(agHotelMania);
			}

			// TODO request hotel info
			
			if (agBank == null) {
				agBank = locateAgent(Constants.REGISTRATION_ACTION, myAgent);
			}else {
				makeDeposit(agBank);
			}
			 */			
		}

		private void bookRoom(AID hotel) {

			BookRoom action_booking = new BookRoom();

			// This part must be dynamic
			Booking booking = new Booking();
			booking.setDays(10);
			booking.setStartDay("10/04/2014");

			sendRequest(hotel, action_booking, Constants.BOOKROOM_PROTOCOL, ACLMessage.REQUEST);
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

			sendRequest(hotelMania, action_rating, Constants.RATEHOTEL_PROTOCOL,ACLMessage.REQUEST);
		}

		private void makeDeposit(AID bank) {
			// Hotel
			Hotel hotel = new Hotel();
			hotel.setHotel_name(actualHotel);

			// TODO This part must be dynamic
			MakeDeposit action_deposit = new MakeDeposit();
			action_deposit.setHotel(hotel);
			action_deposit.setMoney(2000);

			sendRequest(bank, action_deposit,Constants.MAKEDEPOSIT_PROTOCOL,ACLMessage.REQUEST);
		}
	}

	private final class ConsultHotelInfoBehavior extends MetaSimpleBehaviour {

		private static final long serialVersionUID = 1L;
		private AID hotelmania;
		
		private ConsultHotelInfoBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			if (hotelmania == null) {
				hotelmania = locateAgent(Constants.CONSULTHOTELSINFO_ACTION, myAgent);
			} else {
				this.consultHotelInfo(hotelmania);
				this.setDone(true);
			}
		}

		private void consultHotelInfo(AID hotelmania) {
			HotelsInfoRequest request = new HotelsInfoRequest();
			sendRequest(hotelmania, request, Constants.CONSULTHOTELSINFO_PROTOCOL,ACLMessage.QUERY_REF);
		}

	}
	
	private final class ConsultHotelNumberOfClientsBehavior extends MetaSimpleBehaviour {

		private static final long serialVersionUID = 1L;
		private AID hotel;
		
		private ConsultHotelNumberOfClientsBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			if (hotel == null) {
				hotel = locateAgent(Constants.CONSULTHOTELNUMBEROFCLIENTS_ACTION, myAgent);
			} else {
				this.consultHotelInfo();
				this.setDone(true);
			}
		}

		private void consultHotelInfo() {
			NumberOfClientsQueryRef request = new NumberOfClientsQueryRef();
			request.setHotel_name("hotelII");
			sendRequest(hotel, request, Constants.CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL,ACLMessage.QUERY_REF);
		}

	}


	@Override
	public void receivedAcceptance(ACLMessage message) {
		if (message.getProtocol().equals(Constants.CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL)) {
			System.out.println("aaaaa recibido");
		}
		//TODO switch by message.getProtocol()
	}

	@Override
	public void receivedReject(ACLMessage message) {
		// TODO Auto-generated method stub
		if (message.getProtocol().equals(Constants.BOOKROOM_PROTOCOL)) {
			logRejectedMessage(Constants.BOOKROOM_PROTOCOL, message);
		} else if (message.getProtocol().equals(Constants.CONSULTHOTELSINFO_PROTOCOL)) {
			logRejectedMessage(Constants.CONSULTHOTELSINFO_PROTOCOL, message);
		}
	}

	@Override
	public void receivedNotUnderstood(ACLMessage message) {
		// TODO Auto-generated method stub
		if (message.getProtocol().equals(Constants.BOOKROOM_PROTOCOL)) {
			logNotUnderstoodMessage(Constants.BOOKROOM_PROTOCOL, message);
		} else if (message.getProtocol().equals(Constants.CONSULTHOTELSINFO_PROTOCOL)) {
			logNotUnderstoodMessage(Constants.CONSULTHOTELSINFO_PROTOCOL, message);
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
