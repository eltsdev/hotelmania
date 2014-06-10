package hotelmania.group2.platform;

import hotelmania.group2.behaviours.ClientStep;
import hotelmania.group2.behaviours.GenericSendReceiveBehaviour;
import hotelmania.group2.behaviours.SendReceiveBehaviour;
import hotelmania.group2.dao.BookingOffer;
import hotelmania.group2.dao.Client;
import hotelmania.group2.dao.ClientsServedStats;
import hotelmania.group2.dao.RatingInput;
import hotelmania.ontology.BookRoom;
import hotelmania.ontology.Contract;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.HotelInformation;
import hotelmania.ontology.HotelStaffInfo;
import hotelmania.ontology.HotelStaffQueryRef;
import hotelmania.ontology.MakeDeposit;
import hotelmania.ontology.NumberOfClients;
import hotelmania.ontology.NumberOfClientsQueryRef;
import hotelmania.ontology.Price;
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
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;

public class AgClient extends AbstractAgent {

	private static final long serialVersionUID = 6748170421157254696L;
	//Internal state
	private Client client;
	
	private SequentialBehaviour stepsForBooking;
	private SequentialBehaviour stepsForRating;
	private boolean stepsForRatingStarted;
	private boolean paymentStarted;
	
	
	//Flags to check end of life
	private boolean ratingDone;
	private boolean paymentDone;
	
	// -------------------------------------------------
	// Setup
	// -------------------------------------------------

	@Override
	protected void setup() {
		super.setup();

		// Get parameters
		initClient();
		
		// Behaviors for booking
		stepsForBooking = new SequentialBehaviour(this){
			private static final long serialVersionUID = 4696060378279974678L;

			@Override
			public void reset() {
				Logger.logDebug(myName()+": Steps for booking restarted!");
				super.reset();
			}
		};
		stepsForBooking.addSubBehaviour(new GetHotelsFromHotelmaniaBehavior(this));
		stepsForBooking.addSubBehaviour(new CallForOffersBehavior(this));
		stepsForBooking.addSubBehaviour(new ChooseBestOfferBehavior(this));
		addBehaviour(stepsForBooking);

		// Behaviors after booking - not started yet
		stepsForRating = new SequentialBehaviour();
		stepsForRating.addSubBehaviour(new ConsultHotelStaffBehavior(this));
		stepsForRating.addSubBehaviour(new ConsultHotelNumberOfClientsBehavior(this));
		stepsForRating.addSubBehaviour(new RateHotelInHotelmaniaBehavior(this));
	}

	private void initClient() {
		Object[] args = getArguments();
		if (args != null && args.length == 1 && args[0] instanceof Client) {
			this.client = (Client) args[0];
		} else {
			Logger.logError(myName()+": Invalid parameters to create this agent client");
		}
	}


	/**
	 * This means: I AM interested on this event.
	 */
	@Override
	protected boolean setRegisterForDayEvents() {
		return true;
	}
	
	// --------------------------------------------------------
	// Behaviors
	// --------------------------------------------------------

	@Override
	protected void doOnNewDay() {
		super.doOnNewDay();
		
		// Before the stay: check checkin deadline 
		if ( getDay() >= client.getStay().getCheckIn() && this.client.getBookingDone()==null ) {
			//Die
			doDelete();
			return;
		}
		
//		// During the stay: starts data collection
//		if (client.getBookingDone()!=null && client.getCheckInDate() >= getDay()  &&  getDay() < client.getCheckOutDate() ) {
//			startStancy();
//		}

		// After the staying
		if (getDay() >= client.getCheckOutDate()) {
			
			if (!this.paymentStarted) {
				addBehaviour(new MakeDepositBehavior(this));
				this.paymentStarted = true;
			}
			
			if (!this.stepsForRatingStarted) {
				addBehaviour(stepsForRating);
				this.stepsForRatingStarted = true;
			}
		}
	}

	private void startStancy() {
		Logger.logDebug(myName()+": Stay in hotel started. I enjoy this hotel!");
//		addBehaviour(new ConsultHotelNumberOfClientsBehavior(this));
//		addBehaviour(new ConsultHotelStaffBehavior(this));
	}
	private final class GetHotelsFromHotelmaniaBehavior extends SendReceiveBehaviour {
		private static final long serialVersionUID = 287171972374945182L;

		public GetHotelsFromHotelmaniaBehavior(AbstractAgent agClient) {
			super(agClient, Constants.CONSULTHOTELSINFO_PROTOCOL, Constants.CONSULTHOTELSINFO_ACTION, ACLMessage.QUERY_REF);
		}

		/**
		 * Save list of hotels received
		 */
		@Override
		protected void receiveInform(ACLMessage message) {
			try {
				ContentElement content = getContentManager().extractContent(message);
				if (content != null) {
					if (content instanceof ContentElementList) {
						ContentElementList list = (ContentElementList) content;
						this.processListOfHotels(list);
						Logger.logDebug(myName() + ": Number of hotels: " + list.size());

					} else if (content instanceof HotelInformation) {
						HotelInformation hotelInformation = (HotelInformation) content;
						BookingOffer bookingOffer = new BookingOffer(new hotelmania.group2.dao.HotelInformation(hotelInformation));
						client.getOffers().add(bookingOffer);
						Logger.logDebug(myName() + ": Number of hotels: 1 = " + hotelInformation.getHotel().getHotel_name());
					}
				} else {
					Logger.logDebug(myName() + ": Null number of hotels");
				}
			} catch (CodecException | OntologyException e) {
				Logger.logError(myName()+": Message: " + message.getContent());
				e.printStackTrace();
			}
		}

		private void processListOfHotels(ContentElementList list) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) instanceof HotelInformation) {
					HotelInformation hotelInformation = (HotelInformation) list.get(i);
					BookingOffer offer = new BookingOffer(new hotelmania.group2.dao.HotelInformation(hotelInformation));
					client.getOffers().add(offer);
				}

			}
		}
		
		@Override
		protected ClientStep finishOrResend(int performativeReceived) {
			if(performativeReceived==ACLMessage.INFORM || performativeReceived==ACLMessage.AGREE){
				return ClientStep.DONE;
			}
			return ClientStep.RESEND;
		}
	}

	class CallForOffersBehavior extends GenericSendReceiveBehaviour {
		private static final long serialVersionUID = -2493514102408084980L;
		private int informs = 0;
		private int numberOfMessages = 0;

		public CallForOffersBehavior(AbstractAgent myAgent) {
			super(myAgent, Constants.CONSULTROOMPRICES_PROTOCOL);
		}
		
		//Call for booking proposals to all hotels
		protected void doSend() {
			//Create StayQueryRef predicate
			Stay stay = new Stay();
			stay.setCheckIn(client.getCheckInDate());
			stay.setCheckOut(client.getCheckOutDate());
			StayQueryRef query = new StayQueryRef();
			query.setStay(stay);

			//Send
			for (BookingOffer offer : client.getOffers()) {
				AID hotel = offer.getHotelInformation().getHotel().getAgent();
				sendRequest(hotel, query, this.protocol, ACLMessage.QUERY_REF);
			}
			
			setTimeout();
			
			this.numberOfMessages = client.getOffers().size();
		}

		private void setTimeout() {
			//Set timeout
			long timeout = (long) (Constants.DAY_IN_MILLISECONDS / 4);
			myAgent.addBehaviour(new WakerBehaviour(myAgent, timeout) {
				private static final long serialVersionUID = 1L;
				@Override
				protected void handleElapsedTimeout() {
					setDone(true);
				}
			});
		}
		
		@Override
		protected void receiveInform(ACLMessage msg) {
			saveOffer(msg);
		}

		private boolean saveOffer(ACLMessage message) {
			try {
				ContentElement content = getContentManager().extractContent(message);
				if (content != null && content instanceof hotelmania.ontology.BookingOffer) {
					//Set the price to the BookOffer
					hotelmania.ontology.BookingOffer record = (hotelmania.ontology.BookingOffer) content;
					client.setOfferPrice(message.getSender(), record.getRoomPrice().getAmount());
					Logger.logDebug(myName() + ": Price offers: 1 = " + record.getRoomPrice().getAmount());
					return true;
				} else {
					Logger.logDebug(myName() + ": Content was null. Trying to receive room price from Hotel: " + message.getSender().getLocalName());
				}
			} catch (CodecException | OntologyException e) {
				e.printStackTrace();
				Logger.logError(myName() + ": Message: " + message.getContent());
			}

			return false;
		}

		@Override
		protected ClientStep finishOrResend(int performativeReceived) {
			if (performativeReceived==ACLMessage.INFORM || performativeReceived==ACLMessage.AGREE) {
				informs++;
			}
			
			if (informs == numberOfMessages ) {
				return ClientStep.DONE;
			}
			return ClientStep.RECEIVE_RESPONSES;
		}
	}
	
	class ChooseBestOfferBehavior extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;

		public ChooseBestOfferBehavior(Agent a) {
			super(a);
		}
		
		@Override
		public void action() {
			
			BookingOffer bestOffer = client.computeBestBookingOffer();
			boolean accepted = false;
			
			if (bestOffer == null) {
				Logger.logDebug(getLocalName()+ ": No minimum offers achieved. Total: " + client.getOffers().size());
				accepted = false;
				
			} else if(bestOffer.getClientValuation() > 0 ){
				Logger.logDebug(getLocalName() + ": Best offer ACCEPTED :" + bestOffer.getHotelInformation().getHotel().getName() + "; Price: " + bestOffer.getPrice() + "; Rating: " + bestOffer.getHotelInformation().getRating() + "; Valuation: "+bestOffer.getClientValuation());
				accepted = true;
				
			} else if (getDay() == client.getCheckInDate()) {
				Logger.logDebug(getLocalName() + ": Best offer ACCEPTED, although it is beyond the budget:" + bestOffer.getHotelInformation().getHotel().getName() + " Price: " + bestOffer.getPrice() + " because of the deadline!");
				accepted = true;
				
			} else {
				Logger.logDebug(getLocalName() + ": Best offer REJECTED because it is beyond the budget:" + bestOffer.getHotelInformation().getHotel().getName() + " Price: " + bestOffer.getPrice());
				accepted = false;
			}

			if (!accepted) {
				//Start the WHOLE process again! 
				stepsForBooking.reset();
			}
			else {
				stepsForBooking.addSubBehaviour(new RequestBookingInHotelBehavior(AgClient.this, bestOffer));
			}
		}
	}
	
	class RequestBookingInHotelBehavior extends GenericSendReceiveBehaviour {
		private static final long serialVersionUID = 1790087472549757374L;
		private BookingOffer offerChosen;
		private hotelmania.group2.dao.HotelInformation hotel;

		private RequestBookingInHotelBehavior(AbstractAgent myAgent, BookingOffer offerChosen) {
			super(myAgent, Constants.BOOKROOM_PROTOCOL);
			this.offerChosen = offerChosen;  
		}
		
		@Override
		protected boolean doPrepare() {
			if (offerChosen == null) {
				//No offer chosen to make the booking
				this.setDone(true);
				stepsForBooking.reset();
				return false;
			}
			return true;
		}
		
		@Override
		protected void doSend() {
			if(offerChosen!=null){
			bookRoom(offerChosen);
			}else{
				stepsForBooking.reset();
			}
			
		}

		private void bookRoom(BookingOffer bookingOffer) {
			hotel = bookingOffer.getHotelInformation();
			int checkin = client.getStay().getCheckIn();
			int checkout = client.getStay().getCheckOut();
			BookRoom action_booking = new BookRoom();
			Price price = new Price();
			price.setAmount(bookingOffer.getPrice());
			Stay stay = new Stay();
			stay.setCheckIn(checkin);
			stay.setCheckOut(checkout);

			action_booking.setPrice(price);
			action_booking.setStay(stay);
			
			Logger.logDebug(myName()+": Sending request for booking. Checkin and Checkout: "+checkin+"  -  "+checkout + " TODAY = " + getDay());
			
			sendRequest(hotel.getHotel().getAgent(), action_booking, Constants.BOOKROOM_PROTOCOL, ACLMessage.REQUEST);
		}

		@Override
		protected void receiveAgree(ACLMessage msg) {
			saveConfirmationOfBooking();
		}
		
		@Override
		protected void receiveInform(ACLMessage msg) {
			saveConfirmationOfBooking();
		}

		private void saveConfirmationOfBooking() {
			//Save
			hotelmania.group2.dao.Price price = new hotelmania.group2.dao.Price();
			price.setPrice(offerChosen.getPrice());
			hotelmania.group2.dao.BookRoom booking = new hotelmania.group2.dao.BookRoom(client.getStay(), price);
			
			offerChosen.getHotelInformation().getHotel();
			
			client.setBookingDone(booking);
			client.setHotelOfBookingDone(this.hotel);
			
			//Update counter of bookings
			ClientsServedStats.notifyNewService(offerChosen.getHotelInformation().getHotel().getName()); 
			
			//Start stancy
			startStancy();
		}
		
		// Restart the whole BOOKING process if there is no success
		
		@Override
		protected void receiveFailure(ACLMessage msg) {
			stepsForBooking.reset();
		}
		
		@Override
		protected void receiveRefuse(ACLMessage msg) {
			stepsForBooking.reset();
		}
		
		@Override
		protected void receiveNotUnderstood(ACLMessage msg) {
			stepsForBooking.reset();
		}

		@Override
		protected ClientStep finishOrResend(int performativeReceived) {
			return ClientStep.DONE;
		}
	}

	class ConsultHotelNumberOfClientsBehavior extends GenericSendReceiveBehaviour {

		private static final long serialVersionUID = 1L;
		private int dateOfStay;

		private ConsultHotelNumberOfClientsBehavior(AbstractAgent myAgent) {
			super(myAgent,Constants.CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL);
		}
		@Override
		protected void doSend() {
			AID  hotel = client.getHotelOfBookingDone().getHotel().getAgent();
			this.dateOfStay = client.getStay().getCheckIn();
			
			for (int queryDay = client.getStay().getCheckIn(); queryDay <= client.getCheckOutDate(); queryDay++) {
				NumberOfClientsQueryRef request = new NumberOfClientsQueryRef();
				request.setDay(queryDay);
				sendRequest(hotel, request, this.protocol, ACLMessage.QUERY_REF);
			}
		}

		@Override
		protected void receiveInform(ACLMessage msg) {
			handleConsultNumberOfClientsInform(msg);
		}
		
		private void handleConsultNumberOfClientsInform(ACLMessage message) {
			try {
				NumberOfClients content = (NumberOfClients) getContentManager().extractContent(message);
				if (content != null) {
					Logger.logDebug(myName() + ": Number of clients: " + content.getNum_clients());
					client.addOccupancyForRating(this.dateOfStay, content.getNum_clients());
				} else {
					Logger.logDebug(myName() + ": Number of clients: Not found (null)");
				}
			} catch (CodecException | OntologyException e) {
				Logger.logError(myName() + ": Message: " + message.getContent());
				e.printStackTrace();
			}
		}
		
		// ignores refuse, not understood, failure.
		
		@Override
		protected ClientStep finishOrResend(int performativeReceived) {
			if (this.dateOfStay == client.getStay().getCheckOut()) {
				return ClientStep.DONE;
			}
			this.dateOfStay++;
			return ClientStep.RECEIVE_RESPONSES;
		}
	}

	class ConsultHotelStaffBehavior extends	SendReceiveBehaviour {
		
		private static final long serialVersionUID = -6581059230472887190L;
		private int dateOfStay;

		public ConsultHotelStaffBehavior(AbstractAgent myAgent) {
			super(myAgent,Constants.CONSULTHOTELSSTAFF_PROTOCOL, Constants.CONSULTHOTELSSTAFF_ACTION,ACLMessage.QUERY_REF);
		}
		
		@Override
		protected void doSend() {
			Hotel hotel = client.getHotelOfBookingDone().getHotel().getConcept();
			this.dateOfStay = client.getStay().getCheckIn();
			
			for (int queryDay = client.getStay().getCheckIn(); queryDay <= client.getCheckOutDate(); queryDay++) {
				HotelStaffQueryRef request = new HotelStaffQueryRef();
				request.setDay(queryDay);
				request.setHotel(hotel);
				sendRequest(this.server, request, this.protocol, ACLMessage.QUERY_REF);
			}
		}
		
		@Override
		protected void receiveInform(ACLMessage msg) {
			handleConsultStaff(msg);
		}
		
		private boolean handleConsultStaff(ACLMessage message) {
			try {
				HotelStaffInfo hotelStaff = (HotelStaffInfo) getContentManager().extractContent(message);
				Contract content = hotelStaff.getContract();  
				if (content != null) {
					client.addStaffForRating(this.dateOfStay, content);
				} else {
					Logger.logDebug(myName() + ": Staff for day="+dateOfStay+" not found (null)");
				}
			} catch (CodecException | OntologyException e) {
				Logger.logError(myName() + ": Message: " + message.getContent());
				e.printStackTrace();
			}
			return false;
		}


		// ignores refuse, not understood, failure.
		
		@Override
		protected ClientStep finishOrResend(int performativeReceived) {
			if (this.dateOfStay == client.getStay().getCheckOut()) {
				return ClientStep.DONE;
			}
			this.dateOfStay++;
			return ClientStep.RECEIVE_RESPONSES;
		}
	}

	
	class RateHotelInHotelmaniaBehavior extends SendReceiveBehaviour {
		
		private static final long serialVersionUID = -7663792485820846478L;

		public RateHotelInHotelmaniaBehavior(AbstractAgent a) {
			super(a, Constants.RATEHOTEL_PROTOCOL, Constants.RATEHOTEL_ACTION, ACLMessage.REQUEST);
		}
		
		@Override
		protected void doSend() {
			Logger.logDebug(myAgent.myName()+": starting RateHotelInHotelmaniaBehavior");
			rateHotel(this.server);
		}
	
		private void rateHotel(AID hotelMania) {

			//Calculate average ratings
			float cleanliness = 0;
			float kitchen = 0;
			float staff = 0;
			float price = 0;
			int numberOfRatings = 0;
			HashMap<Integer, RatingInput> data = client.getRatingData();
			for (RatingInput ratingInput : data.values()) {
				Logger.logDebug("computing rating for one day ...."+ratingInput.toString());
				//skip if its not complete
				if (ratingInput.getStaff() == null || ratingInput.getOccupancy() == null || ratingInput.getOccupancy() == 0) {
					// if zero: it is inconsistent! at least the client that is asking was in the hotel.
					continue;
				}
				
				//compute
				hotelmania.group2.dao.Contract contract = ratingInput.getStaff();
				kitchen += this.getKitchenRating(contract);
				staff += this.getReceptionistRating(contract,ratingInput.getOccupancy());
				cleanliness += this.getStaffRating(contract,ratingInput.getOccupancy());
				numberOfRatings++;
			}
			
			if (numberOfRatings != 0) {
				cleanliness /= numberOfRatings;
				kitchen /= numberOfRatings;
				staff /= numberOfRatings;
				//After this point, cleanliness kitcfhen and staff are calculated
				price = getPriceRating();
				
				RateHotel action_rating = new RateHotel();
				Rating rating = new Rating();
				rating.setChef_rating(kitchen);
				rating.setPrice_rating(price);
				rating.setRoom_staff_rating(staff);
				rating.setCleanliness_rating(cleanliness);

				action_rating.setRatings(rating);
				Hotel hotel = client.getHotelOfBookingDone().getHotel().getConcept();
				action_rating.setHotel(hotel);

				sendRequest(this.server, action_rating, this.protocol, this.sendPerformative);
			}
			
		}
		
		private float getPriceRating() {
			double budget = client.getBudget();
			double hotelCost = client.getBookingDone().getRoomPrice().getPrice();
			double difference = budget - hotelCost;
			if (difference > 0) {
				double rating = difference/5;
				if (rating > 10) {
					rating = 10;
				}
				return (float) rating;
			} else {
				return 0;
			}
		}
		
		private double getKitchenRating (hotelmania.group2.dao.Contract contract){
			int numberOfCheffs1Star = contract.getchef1stars();
			int numberOfCheffs2Star = contract.getchef2stars();
			int numberOfCheffs3Star = contract.getchef3stars();
			double rating = numberOfCheffs1Star*3.33 + numberOfCheffs2Star*6.66 + numberOfCheffs3Star*10;
			if (rating > 10) {
				rating = 10;
			}
			return rating;
		}
		
		private double getReceptionistRating (hotelmania.group2.dao.Contract contract, int numberOfClients){
			int numberOfNoviceRecepcionist = contract.getRecepcionistNovice();
			int numberOfExpertRecepcionist = contract.getRecepcionistExperienced();
			double rating = ((numberOfExpertRecepcionist*3 + numberOfNoviceRecepcionist*2)/numberOfClients)*10;
			if (rating > 10) {
				rating = 10;
			}
			return rating;
		}
		
		private double getStaffRating (hotelmania.group2.dao.Contract contract, int numberOfClients){
			int numberOfWorkers = contract.getRoomService();

			double rating = (numberOfWorkers/numberOfClients)*10;
			if (rating > 10) {
				rating = 10;
			}
			return rating;
		}
		
		//ignore responses
		
		@Override
		protected ClientStep finishOrResend(int performativeReceived) {
			ratingDone=true;
			return ClientStep.DONE;
		}
	}

	class MakeDepositBehavior extends SendReceiveBehaviour {
		private static final long serialVersionUID = -270503232133476854L;
		
		public MakeDepositBehavior(AbstractAgent a) {
			super(a,Constants.MAKEDEPOSIT_PROTOCOL,Constants.MAKEDEPOSIT_ACTION, ACLMessage.REQUEST);
		}
		

		@Override
		protected void doSend() {
			makeDeposit();
		}	
		
		private void makeDeposit() {
			MakeDeposit action_deposit = new MakeDeposit();
			action_deposit.setHotel(client.getHotelOfBookingDone().getHotel().getConcept());
			action_deposit.setMoney(client.getBookingDone().getRoomPrice().getPrice());

			sendRequest(this.server, action_deposit, this.protocol, this.sendPerformative);
		}
		
		// ignore failure, and other responses.
		
		@Override
		protected ClientStep finishOrResend(int performativeReceived) {
			paymentDone = true;
			return ClientStep.DONE;
		}
	}

	public boolean checkEnd() {
		//If all is done
		if (this.paymentDone && this.ratingDone ) {
			return true;
		}else if (getDay() > client.getCheckOutDate() ) {
			//if there is chance to finish, pay and rate
			return false;
		} else {
			//otherwise die...
			return true;
		}
	}

	@Override
	public boolean doBeforeDie() {
		Logger.logDebug(myName() + ": STAY DAYS = " + client.getStay().getDays()+ " - TOTAL RATING DATA = " + this.client.getRatingData().size());
		client.printRatingData();
		return checkEnd();
	}
	
	
	@Override
	public void doDelete() {
		Logger.logDebug(myName() + ": STAY DAYS = " + client.getStay().getDays()+ " - TOTAL RATING DATA = " + this.client.getRatingData().size());
		client.printRatingData();
		//Die
		Logger.logDebug(myName()+": DIED");		
		super.doDelete();
	}

}
