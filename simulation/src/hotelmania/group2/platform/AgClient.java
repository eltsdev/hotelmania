package hotelmania.group2.platform;

import hotelmania.group2.dao.BookingOffer;
import hotelmania.group2.dao.Client;
import hotelmania.ontology.BookRoom;
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
import hotelmania.ontology.StayQueryRef;
import jade.content.ContentElement;
import jade.content.ContentElementList;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

public class AgClient extends MetaAgent {

	private static final long serialVersionUID = 6748170421157254696L;

	// private boolean bookingDone;
	private AID actual_hotel;
	private ArrayList<BookingOffer> bookingOffers = new ArrayList<BookingOffer>();

	private Client client;

	public AgClient() {
	}

	// -------------------------------------------------
	// Setup
	// -------------------------------------------------

	@Override
	protected void setup() {
		super.setup();

		// Get parameters
		Object[] args = getArguments();
		if (args != null && args.length == 1 && args[0] instanceof Client) {
			this.client = (Client) args[0];
			System.out.println("Client arguments: " + this.client.getBudget()+ " - " + this.client.getStay().getCheckIn() + " - " + this.client.getStay().getCheckOut());
		} else {
			// throw new Exception("Invalid parameters to create this agent");
			System.err
					.println("Invalid parameters to create this agent client");
		}
		// Behaviors

		//addBehaviour(new RequestBookingInHotelBehavior(this));
		addBehaviour(new ConsultHotelInfoBehavior(this));
		//addBehaviour(new ConsultHotelNumberOfClientsBehavior(this));
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
		// TODO to implement...
	}

	// --------------------------------------------------------
	// Behaviors
	// --------------------------------------------------------
	private final class RequestBookingInHotelBehavior extends
			MetaSimpleBehaviour {

		private static final long serialVersionUID = -1417563883440156372L;

		private BookingOffer actual_booking;

		private RequestBookingInHotelBehavior(Agent a) {
			super(a);
		}

		public RequestBookingInHotelBehavior(Agent a, BookingOffer booking) {
			super(a);
			this.actual_booking = booking;
		}

		public void action() {
			AID agHotel = this.actual_booking.getHotelInformation().getHotel().getAgent();
			if (agHotel != null) {
				bookRoom(this.actual_booking);
				this.setDone(true);
				Hotel hotel= actual_booking.getHotelInformation().getHotel().getConcept();
//				bookingDeadline();
//				addBehaviour(new RateHotelBehavior(myAgent, hotel));

			}

			// TODO implement:
			// if (AgClient.this.bookingDone) {
			// myAgent.doDelete(); // TODO Test if this works.
			// }

		}

	}

	private void bookRoom(BookingOffer bookingOffer) {

		AID agHotel = bookingOffer.getHotelInformation().getHotel().getAgent();
		int checkin = client.getStay().getCheckIn();
		int checkout = client.getStay().getCheckOut();

		BookRoom action_booking = new BookRoom();
		Price price = new Price();
		price.setPrice(bookingOffer.getPrice());
		Stay stay = new Stay();
		stay.setCheckIn(checkin);
		stay.setCheckOut(checkout);

		hotelmania.ontology.BookingOffer bookOffer = new hotelmania.ontology.BookingOffer();
		bookOffer.setRoomPrice(price);
		action_booking.setBookingOffer(bookOffer);
		action_booking.setStay(stay);

		sendRequest(agHotel, action_booking, Constants.BOOKROOM_PROTOCOL,
				ACLMessage.REQUEST);
	}

	private final class ConsultRoomPriceBehavior extends MetaSimpleBehaviour {

		private static final long serialVersionUID = -7430481224502139961L;

		/**
		 * @param a
		 */
		public ConsultRoomPriceBehavior(Agent a) {
			super(a);
			// TODO Auto-generated constructor stub
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see hotelmania.group2.platform.MetaSimpleBehaviour#action()
		 */
		@Override
		public void action() {
			Hotel hotel;
			int checkin = client.getStay().getCheckIn();
			int checkout = client.getStay().getCheckOut();

			// Config Days to stay in a Hotel
			Stay stay = new Stay();
			stay.setCheckIn(checkin);
			stay.setCheckOut(checkout);
			
			//Create StayQueryRef predicate
			StayQueryRef request_stay = new StayQueryRef();
			request_stay.setStay(stay);

			for (int i = 0; i < bookingOffers.size(); i++) {
				hotel = bookingOffers.get(i).getHotelInformation().getHotel().getConcept();
				sendRequestPredicate(hotel.getHotelAgent(), request_stay,Constants.CONSULTROOMPRICES_PROTOCOL, ACLMessage.QUERY_REF);

			}
			long time= (long) (Constants.DAY_IN_MILLISECONDS * 0.5);
			doWait(time);
			
			BookingOffer lowest_price_booking=computeBestRoomPrice(bookingOffers);
			this.setDone(true);
			if (lowest_price_booking != null) {
				addBehaviour(new RequestBookingInHotelBehavior(myAgent, lowest_price_booking));
			} else {
				System.out.println("no minimum hotel was found: " + bookingOffers.size());
				
			}

			
		}

		/**
		 * @param bookingOffers
		 */
		private BookingOffer computeBestRoomPrice(ArrayList<BookingOffer> bookingOffers) {
			float minimunPrice = 0;
			BookingOffer lowest_price_booking = null;
			for (BookingOffer bookingOffer : bookingOffers) {
				float actual_price = bookingOffer.getPrice();
				if (actual_price != -1) {
					if (minimunPrice < actual_price) {
						minimunPrice = actual_price;
						lowest_price_booking = bookingOffer;
					}
				}
			}
			return lowest_price_booking;
		}
	}

	private final class MakeDepositBehavior extends MetaSimpleBehaviour {

		private static final long serialVersionUID = -6125742370278108815L;
		private BookingOffer actual_booking;
		private AID agBank = null;

		/**
		 * @param a
		 */
		public MakeDepositBehavior(Agent a, BookingOffer bookingOffer) {
			super(a);
			this.actual_booking = bookingOffer;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see hotelmania.group2.platform.MetaSimpleBehaviour#action()
		 */
		@Override
		public void action() {

			// TODO Auto-generated method stub
			super.action();

			// TODO request hotel info

			if (agBank == null) {
				agBank = locateAgent(Constants.MAKEDEPOSIT_ACTION, myAgent);

			} else {

				makeDeposit(agBank);
				this.setDone(true);
			}
		}

		private void makeDeposit(AID bank) {
			// Hotel
			Hotel hotel = actual_booking.getHotelInformation().getHotel()
					.getConcept();

			// TODO This part must be dynamic
			MakeDeposit action_deposit = new MakeDeposit();
			action_deposit.setHotel(hotel);
			action_deposit.setMoney(actual_booking.getPrice());

			sendRequest(bank, action_deposit, Constants.MAKEDEPOSIT_PROTOCOL, ACLMessage.REQUEST);
		}

	}

	private final class RateHotelBehavior extends MetaSimpleBehaviour {

		private static final long serialVersionUID = -3146214323868840463L;
		private AID agHotelMania;
		private Hotel hotel;

		RateHotelBehavior(Agent a) {
			super(a);
		}
		/**
		 * 
		 */
		public RateHotelBehavior(Agent a, Hotel hotel) {
			super(a);
			this.hotel=hotel;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see hotelmania.group2.platform.MetaSimpleBehaviour#action()
		 */
		@Override
		public void action() {
			// TODO Auto-generated method stub
			super.action();

			// LocateHotel

			if (agHotelMania == null) {
				agHotelMania = locateAgent(Constants.RATEHOTEL_ACTION, myAgent);
			} else {
				rateHotel(agHotelMania);
				this.setDone(true);
			}

		}


		/**
  		 * @param agHotelMania
  		 */
  		private void rateHotel(AID hotelMania) {
  			RateHotel action_rating = new RateHotel();
  
  			// Hotel
 		// Hotel hotel = new Hotel();
 		// hotel.setHotel_name(actualHotel);
 		// hotel.setHotelAgent(agHotel);
 
 		// TODO This part must be dynamic
 		Rating rating = new Rating();
 		rating.setCookers_rating(10);
 		rating.setPrice_rating(10);
 		rating.setRoom_staff_rating(10);
 		 action_rating.setHotel(this.hotel);
 		 action_rating.setRatings(rating);
 
 		sendRequest(hotelMania, action_rating,
 				Constants.RATEHOTEL_PROTOCOL, ACLMessage.REQUEST);
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
			QueryHotelmaniaHotel consult_request = new QueryHotelmaniaHotel();
			sendRequest(hotelmania, consult_request,
					Constants.CONSULTHOTELSINFO_PROTOCOL, ACLMessage.QUERY_REF);
		}

	}

	private final class ConsultHotelNumberOfClientsBehavior extends	MetaSimpleBehaviour {

		private static final long serialVersionUID = 1L;
		private AID hotel = actual_hotel;

		private ConsultHotelNumberOfClientsBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			if (hotel == null) {
				hotel = locateAgent(
						Constants.CONSULTHOTELNUMBEROFCLIENTS_ACTION, myAgent);

				// TODO quitarlo una vez que el booking cambie el valor de la variable myHotel 
			} else {
				this.consultHotelInfo();
				this.setDone(true);
			}
		}

		private void consultHotelInfo() {
			NumberOfClientsQueryRef request = new NumberOfClientsQueryRef();
			// request.setHotel_name("hotelII");
			if (actual_hotel == null) {
				request.setHotel_name("Hotel2");
			} else {
				request.setHotel_name(myName());
			}
			sendRequest(hotel, request,
					Constants.CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL, ACLMessage.QUERY_REF);
		}

	}

	@Override
	public void receivedAcceptance(ACLMessage message) {
		// TODO switch by message.getProtocol()
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see hotelmania.group2.platform.MetaAgent#receiveInform()
	 */
	@Override
	public void receivedInform(ACLMessage message) {
		if (message.getProtocol().equals(Constants.CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL)) {
			handleConsultNumberOfClientsInform(message);
		} else if (message.getProtocol().equals(Constants.CONSULTHOTELSINFO_PROTOCOL)) {
			handleConsultHotelsInfoInform(message);
		} else if (message.getProtocol().equals(Constants.CONSULTROOMPRICES_PROTOCOL)) {
			handleConsultHotelsPriceInform(message);
		}
	}

	/**
	 * @param message
	 */
	private void handleConsultHotelsPriceInform(ACLMessage message) {

		ContentElement content;
		try {
			content = getContentManager().extractContent(message);

			if (content != null) {
				// TODO complete handling
				if (content instanceof hotelmania.ontology.BookingOffer) {
					hotelmania.ontology.BookingOffer record = (hotelmania.ontology.BookingOffer) content;
					Float price = Float.valueOf(record.getRoomPrice().getPrice());
					for (BookingOffer bookingOffer : bookingOffers) {
						if (bookingOffer.getHotelInformation().getHotel().getAgent().equals(message.getSender())) {
							bookingOffer.setPrice(price);
						}
					}
					System.out.println(myName() + ": Price offeres: 1 = " + price);

				}
			} else {
				System.out.println(myName() + ": Content was null. Trying to receive room price from Hotel: " + message.getSender().getLocalName());
			}

		} catch (CodecException | OntologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(myName() + ": Message: " + message.getContent());
		}
	}

	/**
	 * @param message
	 */
	private void handleConsultNumberOfClientsInform(ACLMessage message) {
		try {
			NumberOfClients content = (NumberOfClients) getContentManager().extractContent(message);
			if (content != null) {
				System.out.println(myName() + ": Number of clients: " + content.getNum_clients());
			} else {
				System.out.println(myName() + ": Number of clients: Not found (null)");
			}
		} catch (CodecException | OntologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(myName() + ": Message: " + message.getContent());
		}
	}

	/**
	 * @param message
	 */
	private void handleConsultHotelsInfoInform(ACLMessage message) {
		try {
			ContentElement content = getContentManager()
					.extractContent(message);
			if (content != null) {
				// TODO complete handling
				if (content instanceof ContentElementList) {
					ContentElementList list = (ContentElementList) content;
					System.out.println(myName() + ": Number of hotels: " + list.size());
					this.convertContenElementListToHotelInformationList(list);
				} else if (content instanceof HotelInformation) {
					HotelInformation hotelInformation = (HotelInformation) content;
					BookingOffer bookingOffer = new BookingOffer(new hotelmania.group2.dao.HotelInformation(hotelInformation));
					this.bookingOffers.add(bookingOffer);
					System.out.println(myName() + ": Number of hotels: 1 = " + hotelInformation.getHotel().getHotel_name());
				}
				
				addBehaviour(new ConsultRoomPriceBehavior(this));
			} else {
				System.out.println(myName() + ": Null number of hotels");
			}
		} catch (CodecException | OntologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Message: " + message.getContent());
		}
	}

	/**
	 * @param content
	 */
	private void convertContenElementListToHotelInformationList(
			ContentElementList list) {
		// Hotel
		for (int i = 0; i < list.size(); i++) {
			HotelInformation hotelInformation = (HotelInformation) list.get(i);
			if (hotelInformation instanceof HotelInformation) {
				BookingOffer booking = new BookingOffer(new hotelmania.group2.dao.HotelInformation(hotelInformation));
				this.bookingOffers.add(booking);
			}

		}
	}

}
