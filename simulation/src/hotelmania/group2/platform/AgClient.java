package hotelmania.group2.platform;

import hotelmania.group2.dao.Client;
import hotelmania.group2.dao.Booking;
import hotelmania.group2.dao.BookingOffer;
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
import jade.content.ContentElement;
import jade.content.ContentElementList;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CompositeBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Collection;

import java.util.ArrayList;

public class AgClient extends MetaAgent {

	private static final long serialVersionUID = 6748170421157254696L;

	private boolean bookingDone;
	private AID actual_hotel;
	private ArrayList<BookingOffer> bookingOffers = new ArrayList<BookingOffer>();


	private Client client;

	public AgClient(){}

	// -------------------------------------------------
	// Setup
	// -------------------------------------------------

	@Override
	protected void setup() {
		super.setup();

		// Get parameters
		Object[] args = getArguments();
		if (args != null && args.length == 1 && args[0] instanceof Client) {
			this.client = (Client)args[0];
			System.out.println("Client arguments: "+this.client.getBudget()+" - "+this.client.getStay().getCheckIn()+" - "+this.client.getStay().getCheckIn());
		}else {
			//throw new Exception("Invalid parameters to create this agent");
			System.err.println("Invalid parameters to create this agent client");
		}
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
		// TODO to implement...
	}

	// --------------------------------------------------------
	// Behaviors
	// --------------------------------------------------------
	private final class RequestBookingInHotelBehavior extends
	MetaSimpleBehaviour {

		private static final long serialVersionUID = -1417563883440156372L;
		private AID agHotelMania;
		private AID agBank;
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
			if(agHotel!=null){
				bookRoom(agHotel);
				this.setDone(true);

			}

			// TODO implement:
			//			if (AgClient.this.bookingDone) {
			//				myAgent.doDelete(); // TODO Test if this works.
			//			}


		}

		// TODO implement:
		//			if (AgClient.this.bookingDone) {
		//				myAgent.doDelete(); // TODO Test if this works.
		//			}

		//			// LocateHotel
		//			if (agHotel == null) {
		//				agHotel = locateAgent(Constants.BOOKROOM_ACTION, myAgent);
		//			} else {
		//				bookRoom(agHotel);
		//				this.setDone(true);
		//			}
		//
		//			if (agHotelMania == null) {
		//				agHotelMania = locateAgent(Constants.RATEHOTEL_ACTION, myAgent);
		//			} else {
		//
		//			}
		//
		//			// TODO request hotel info
		//
		//			if (agBank == null) {
		//				agBank = locateAgent(Constants.MAKEDEPOSIT_ACTION, myAgent);
		//			} else {
		//				makeDeposit(agBank);
		//			}
	}

	private void bookRoom(AID hotel) {

		BookRoom action_booking = new BookRoom();
		Price price = new Price();
		price.setPrice(300);
		Stay stay = new Stay();
		stay.setCheckIn(3);
		stay.setCheckOut(7);

		hotelmania.ontology.BookingOffer bookOffer = new hotelmania.ontology.BookingOffer();
		bookOffer.setRoomPrice(price);
		action_booking.setBookingOffer(bookOffer);
		action_booking.setStay(stay);

		sendRequest(hotel, action_booking, Constants.BOOKROOM_PROTOCOL,	ACLMessage.REQUEST);
	}

	//		private void makeDeposit(AID bank) {
	//			// Hotel
	//			Hotel hotel = new Hotel();
	//			hotel.setHotel_name(myName());
	//			hotel.setHotelAgent(agHotel);
	//
	//			// TODO This part must be dynamic
	//			MakeDeposit action_deposit = new MakeDeposit();
	//			action_deposit.setHotel(hotel);
	//			action_deposit.setMoney(2000);
	//
	//			sendRequest(bank, action_deposit, Constants.MAKEDEPOSIT_PROTOCOL,
	//					ACLMessage.REQUEST);
	//		}


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
		// TODO do dynamically
		Stay request_stay = new Stay();
		request_stay.setCheckIn(3);
		request_stay.setCheckOut(8);

		for (int i = 0; i < bookingOffers.size(); i++) {
			hotel = bookingOffers.get(i).getHotelInformation().getHotel().getConcept();
			sendRequestPredicate(hotel.getHotelAgent(), request_stay,Constants.CONSULTROOMPRICES_PROTOCOL, ACLMessage.QUERY_REF);

		}
		try {
			this.wait((long)(Constants.DAY_IN_SECONDS*0.5));
			computeBestRoomPrice(bookingOffers);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setDone(true);
	}

	//		private void makeDeposit(AID bank) {
	//			// Hotel
	//			Hotel hotel = new Hotel();
	//			hotel.setHotel_name(myName());
	//			hotel.setHotelAgent(agHotel);
	//
	//			// TODO This part must be dynamic
	//			MakeDeposit action_deposit = new MakeDeposit();
	//			action_deposit.setHotel(hotel);
	//			action_deposit.setMoney(2000);
	//
	//			sendRequest(bank, action_deposit, Constants.MAKEDEPOSIT_PROTOCOL,
	//					ACLMessage.REQUEST);
	//		}

	
	/**
	 * @param bookingOffers
	 */
	private void computeBestRoomPrice(ArrayList<BookingOffer> bookingOffers) {
		float minimunPrice =0;
		BookingOffer lowest_price_booking = null;
		for (BookingOffer bookingOffer : bookingOffers) {
			float actual_price= bookingOffer.getPrice();
			if (actual_price!=-1) {
				if (minimunPrice< actual_price){
					minimunPrice = actual_price;
					lowest_price_booking = bookingOffer;
				}
			}
		}
		if(lowest_price_booking!=null){
			addBehaviour(new RequestBookingInHotelBehavior(myAgent,lowest_price_booking));	
		}


	}
}



private final class RateHotelBehavior extends MetaSimpleBehaviour {

	private static final long serialVersionUID = -3146214323868840463L;

	public RateHotelBehavior(Agent a) {
		super(a);
	}

	// rateHotel();

	/**
	 * @param bookingOffers
	 */
	private void computeBestRoomPrice(ArrayList<BookingOffer> bookingOffers) {
		float minimunPrice =0;
		BookingOffer lowest_price_booking = null;
		for (BookingOffer bookingOffer : bookingOffers) {
			float actual_price= bookingOffer.getPrice();
			if (actual_price!=-1) {
				if (minimunPrice< actual_price){
					minimunPrice = actual_price;
					lowest_price_booking = bookingOffer;
				}
			}
		}
		if(lowest_price_booking!=null){
			addBehaviour(new RequestBookingInHotelBehavior(myAgent,lowest_price_booking));	
		}


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
			hotelmania = locateAgent(Constants.CONSULTHOTELSINFO_ACTION,
					myAgent);
		} else {
			this.consultHotelInfo(hotelmania);
			this.setDone(true);
		}
	}

	private void consultHotelInfo(AID hotelmania) {
		QueryHotelmaniaHotel request = new QueryHotelmaniaHotel();
		sendRequest(hotelmania, request,
				Constants.CONSULTHOTELSINFO_PROTOCOL, ACLMessage.QUERY_REF);
	}

}

private final class ConsultHotelNumberOfClientsBehavior extends
MetaSimpleBehaviour {

	private static final long serialVersionUID = 1L;
	private AID hotel = actual_hotel;

	private ConsultHotelNumberOfClientsBehavior(Agent a) {
		super(a);
	}

	@Override
	public void action() {
		if (hotel == null) {
			hotel = locateAgent(
					Constants.CONSULTHOTELNUMBEROFCLIENTS_ACTION, myAgent);// TODO
			// quitarlo
			// una
			// vez
			// que
			// el
			// booking
			// cambie
			// el
			// valor
			// de
			// la
			// variable
			// myHotel
		} else {
			this.consultHotelInfo();
			this.setDone(true);
		}
	}

	private void consultHotelInfo() {
		NumberOfClientsQueryRef request = new NumberOfClientsQueryRef();
		//request.setHotel_name("hotelII");
		if (actual_hotel == null) {
			request.setHotel_name("Hotel2");
		} else {
			request.setHotel_name(myName());
		}
		sendRequest(hotel, request,
				Constants.CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL,
				ACLMessage.QUERY_REF);
	}

}

@Override
public void receivedAcceptance(ACLMessage message) {
	// TODO switch by message.getProtocol()
	if (message.getProtocol().equals(
			Constants.CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL)) {
		System.out.println();
	}
}

@Override
public void receivedReject(ACLMessage message) {
	// TODO Auto-generated method stub
	if (message.getProtocol().equals(Constants.BOOKROOM_PROTOCOL)) {

	} else if (message.getProtocol().equals(
			Constants.CONSULTHOTELSINFO_PROTOCOL)) {

	}
}

@Override
public void receivedNotUnderstood(ACLMessage message) {
	// TODO Auto-generated method stub
	if (message.getProtocol().equals(Constants.BOOKROOM_PROTOCOL)) {

	} else if (message.getProtocol().equals(
			Constants.CONSULTHOTELSINFO_PROTOCOL)) {

	}
}

/*
 * (non-Javadoc)
 * 
 * @see hotelmania.group2.platform.MetaAgent#receiveInform()
 */
@Override
public void receivedInform(ACLMessage message) {
	if (message.getProtocol().equals(
			Constants.CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL)) {
		handleConsultNumberOfClientsInform(message);
	} else if (message.getProtocol().equals(
			Constants.CONSULTHOTELSINFO_PROTOCOL)) {
		handleConsultHotelsInfoInform(message);
	} else if (message.getProtocol().equals(
			Constants.CONSULTROOMPRICES_PROTOCOL)) {
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
				for (BookingOffer  bookingOffer : bookingOffers) {
					if(bookingOffer.getHotelInformation().getHotel().getAgent().equals(message.getSender())){
						bookingOffer.setPrice(price);
					}
				}
				System.out.println(myName() + ": Price offeres: 1 = " + price);

			}
		} else {
			System.out.println(myName() + ": Content was null. Trying to receive room price from Hotel: "+ message.getSender().getLocalName());
		}

	} catch (CodecException | OntologyException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		System.out.println("Message: " + message.getContent());
	}
}

/**
 * @param message
 */
private void handleConsultNumberOfClientsInform(ACLMessage message) {
	try {
		NumberOfClients content = (NumberOfClients) getContentManager()
				.extractContent(message);
		if (content != null) {
			System.out.println(myName() + ": Number of clients: "
					+ content.getNum_clients());
		} else {
			System.out.println(myName()
					+ ": Number of clients: Not found (null)");
		}
	} catch (CodecException | OntologyException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		System.out.println("Message: " + message.getContent());
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
private void convertContenElementListToHotelInformationList(ContentElementList list) {
	//Hotel
	for (int i = 0; i < list.size(); i++) {
		HotelInformation hotelInformation = (HotelInformation) list.get(i);
		if(hotelInformation instanceof HotelInformation){
			BookingOffer booking = new BookingOffer(new hotelmania.group2.dao.HotelInformation(hotelInformation));
			this.bookingOffers.add(booking);
		}

	}
}

}