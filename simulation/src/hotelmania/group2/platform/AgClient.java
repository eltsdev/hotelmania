package hotelmania.group2.platform;

import hotelmania.ontology.BookRoom;
import hotelmania.ontology.BookingOffer;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.HotelInformation;
import hotelmania.ontology.MakeDeposit;
import hotelmania.ontology.NumberOfClients;
import hotelmania.ontology.NumberOfClientsQueryRef;
import hotelmania.ontology.Price;
import hotelmania.ontology.QueryHotelmaniaHotel;
import hotelmania.ontology.RateHotel;
import hotelmania.ontology.Rating;
import hotelmania.ontology.Stay;
import jade.content.ContentElement;
import jade.content.ContentElementList;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class AgClient extends MetaAgent {
	
	private static final long serialVersionUID = 6748170421157254696L;

	private boolean bookingDone;
	//private String actualHotel;
	private AID myHotel;
	// -------------------------------------------------
	// Setup
	// -------------------------------------------------
	
	@Override
	protected void setup() {
		super.setup();
		
		// Behaviors

		addBehaviour(new RequestBookingInHotelBehavior(this));
		addBehaviour(new ConsultHotelInfoBehavior(this));
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
			
			// TODO implement:			
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
			 		
		}

		private void bookRoom(AID hotel) {

			BookRoom action_booking = new BookRoom();
			Price price= new Price();
			price.setPrice(300);
			Stay  stay = new Stay();
			stay.setCheckIn(3);
			stay.setCheckOut(7);

			BookingOffer bookOffer = new BookingOffer();
			bookOffer.setRoomPrice(price);
			action_booking.setBookingOffer(bookOffer);
			action_booking.setStay(stay);

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
			hotel.setHotelAgent(agHotel);

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
			hotel.setHotel_name(myName());
			hotel.setHotelAgent(agHotel);
			
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
			QueryHotelmaniaHotel request = new QueryHotelmaniaHotel();
			sendRequest(hotelmania, request, Constants.CONSULTHOTELSINFO_PROTOCOL,ACLMessage.QUERY_REF);
		}

	}
	
	private final class ConsultHotelNumberOfClientsBehavior extends MetaSimpleBehaviour {

		private static final long serialVersionUID = 1L;
		private AID hotel = myHotel;
		
		private ConsultHotelNumberOfClientsBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			if (hotel == null) {
				hotel = locateAgent(Constants.CONSULTHOTELNUMBEROFCLIENTS_ACTION, myAgent);//TODO quitarlo una vez que el booking cambie el valor de la variable myHotel
			} else {
				this.consultHotelInfo();
				this.setDone(true);
			}
		}

		private void consultHotelInfo() {
			NumberOfClientsQueryRef request = new NumberOfClientsQueryRef();
			//request.setHotel_name("hotelII");
			if (myHotel == null) {
				request.setHotel_name("hotelII");
			} else {
				request.setHotel_name(myName());
			}
			
			sendRequest(hotel, request, Constants.CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL,ACLMessage.QUERY_REF);
		}

	}


	@Override
	public void receivedAcceptance(ACLMessage message) {
		//TODO switch by message.getProtocol()
		if (message.getProtocol().equals(Constants.CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL)) {
			System.out.println();
		}
	}

	@Override
	public void receivedReject(ACLMessage message) {
		// TODO Auto-generated method stub
		if (message.getProtocol().equals(Constants.BOOKROOM_PROTOCOL)) {
			
		} else if (message.getProtocol().equals(Constants.CONSULTHOTELSINFO_PROTOCOL)) {
			
		}
	}

	@Override
	public void receivedNotUnderstood(ACLMessage message) {
		// TODO Auto-generated method stub
		if (message.getProtocol().equals(Constants.BOOKROOM_PROTOCOL)) {
			
		} else if (message.getProtocol().equals(Constants.CONSULTHOTELSINFO_PROTOCOL)) {

		}	
	}

	/* (non-Javadoc)
	 * @see hotelmania.group2.platform.MetaAgent#receiveInform()
	 */
	@Override
	public void receivedInform(ACLMessage message) {
		if (message.getProtocol().equals(Constants.CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL)) {
			handleConsultNumberOfClientsInform(message);
		}else if (message.getProtocol().equals(Constants.CONSULTHOTELSINFO_PROTOCOL)) {
			handleConsultHotelsInfoInform(message);
		}
	}

	private void handleConsultNumberOfClientsInform(ACLMessage message) {
		try {
			NumberOfClients content = (NumberOfClients) getContentManager().extractContent(message);
			if (content != null) {
				System.out.println(myName() + ": Number of clients: "+content.getNum_clients());					
			}else {
				System.out.println(myName() + ": Number of clients: Not found (null)");
			}
		} catch (CodecException | OntologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Message: " + message.getContent());
		}
	}

	private void handleConsultHotelsInfoInform(ACLMessage message) {
		try {
			ContentElement content = getContentManager().extractContent(message);
			if (content != null) {
				//TODO complete handling
				if (content instanceof ContentElementList) {
					ContentElementList list = (ContentElementList) content;
					System.out.println(myName() + ": Number of hotels: "+list.size());			
				}
				else if (content instanceof HotelInformation) {
					HotelInformation record = (HotelInformation) content;
					System.out.println(myName() + ": Number of hotels: 1 = "+record.getHotel().getHotel_name());
				}
			}else {
				System.out.println(myName() + ": Null number of hotels");
			}
		} catch (CodecException | OntologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Message: " + message.getContent());
		}
	}

}
