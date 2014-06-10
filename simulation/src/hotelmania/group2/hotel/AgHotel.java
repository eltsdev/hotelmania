/**
 * 
 */
package hotelmania.group2.hotel;

import hotelmania.group2.behaviours.ClientStep;
import hotelmania.group2.behaviours.GenericServerResponseBehaviour;
import hotelmania.group2.behaviours.SendReceiveBehaviour;
import hotelmania.group2.dao.BookingDAO;
import hotelmania.group2.dao.Price;
import hotelmania.group2.dao.Stay;
import hotelmania.group2.platform.AbstractAgent;
import hotelmania.group2.platform.AgentState.State;
import hotelmania.group2.platform.Constants;
import hotelmania.group2.platform.Logger;
import hotelmania.ontology.AccountStatus;
import hotelmania.ontology.AccountStatusQueryRef;
import hotelmania.ontology.BookRoom;
import hotelmania.ontology.BookingOffer;
import hotelmania.ontology.Contract;
import hotelmania.ontology.CreateAccountRequest;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.HotelInformation;
import hotelmania.ontology.NumberOfClients;
import hotelmania.ontology.NumberOfClientsQueryRef;
import hotelmania.ontology.RegistrationRequest;
import hotelmania.ontology.SignContract;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.ContentElementList;
import jade.content.Predicate;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * @author user
 *
 */
public class AgHotel extends AbstractAgent {

	private static final long serialVersionUID = 6197364680986347122L;
	
	private SequentialBehaviour stepsForCreationHotel;
	private BookingDAO bookDAO = new BookingDAO();
	private final Hotel hotelIdentity = new Hotel();
	private hotelmania.group2.dao.Contract currentContract;
	private float rating=5;
	private float currentPrice = 0;
	private Integer idAccount;
	private float actualBalance;

	@Override
	public String myName() {
		return Constants.HOTEL_NAME;
	}

	
	@Override
	protected void doOnNewDay() {
		super.doOnNewDay();
		
		//Behaviors for price and staff strategy
		SequentialBehaviour stepsForPriceAndStaffStrategy = new SequentialBehaviour();
		ParallelBehaviour refreshDataBehaviour = new ParallelBehaviour();
		refreshDataBehaviour.addSubBehaviour(new ConsultAccountStatusBehavior(AgHotel.this));
		refreshDataBehaviour.addSubBehaviour(new ConsultMyRatingBehavior(AgHotel.this));
		
		stepsForPriceAndStaffStrategy.addSubBehaviour(refreshDataBehaviour);
		stepsForPriceAndStaffStrategy.addSubBehaviour(new HireDailyStaffBehavior(this));
	}
	
	@Override
	protected void setup() {
		super.setup();

		this.hotelIdentity.setHotel_name(myName());
		this.hotelIdentity.setHotelAgent(getAID());

		// Behaviors for configuration Hotel
		this.stepsForCreationHotel = new SequentialBehaviour(this) {
			private static final long serialVersionUID = 7546466232205586064L;
			@Override
			public int onEnd() {
				//Behaviors that are responsible for responding to requests
				myAgent.addBehaviour(new ProvideRoomPriceBehavior(AgHotel.this));
				myAgent.addBehaviour(new MakeRoomBookingBehavior(AgHotel.this));
				myAgent.addBehaviour(new ProvideOccupancyOfRoomsBehavior(AgHotel.this));
				return super.onEnd();
			}
		};
		this.stepsForCreationHotel.addSubBehaviour(new RegisterInHotelmaniaBehavior(this));
		this.stepsForCreationHotel.addSubBehaviour(new CreateBankAccountBehavior(this));
		addBehaviour(stepsForCreationHotel);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hotelmania.group2.platform.AbstractAgent#setRegisterForDayEvents()
	 */
	@Override
	protected boolean setRegisterForDayEvents() {
		return true;
	}

	// --------------------------------------------------------
	// Behaviors
	// --------------------------------------------------------

	/*
	 * Registration Hotel in Hotelmania
	 */
	private final class RegisterInHotelmaniaBehavior extends SendReceiveBehaviour {
		private static final long serialVersionUID = 1256090117313507535L;

		private RegisterInHotelmaniaBehavior(AbstractAgent AgHotel) {
			super(AgHotel, Constants.REGISTRATION_PROTOCOL,	Constants.REGISTRATION_ACTION, ACLMessage.REQUEST);
		}

		@Override
		protected void doSend() {
			registerHotel();
		}

		private void registerHotel() {
			RegistrationRequest action_register = new RegistrationRequest();
			action_register.setHotel(hotelIdentity);

			sendRequest(this.server, action_register,this.protocol, this.sendPerformative);
		}
		
		@Override
		protected void receiveAgree(ACLMessage msg) {
			state.check(State.REGISTERED_HOTELMANIA);
		}
		
		// ignore responses.
		
		@Override
		protected ClientStep finishOrResend(int performativeReceived) {
			if (performativeReceived==ACLMessage.AGREE) {
				return ClientStep.DONE;
			}else {
				return ClientStep.RESEND;
			}
		}
	}

	
	/**
	 * @author user
	 *Create Hotel Account
	 */
	private final class CreateBankAccountBehavior extends SendReceiveBehaviour {

		private static final long serialVersionUID = 1955222376582492939L;

		/**
		 * @param agHotel
		 */
		public CreateBankAccountBehavior(AbstractAgent agHotel) {
			super(agHotel, Constants.CREATEACCOUNT_PROTOCOL,Constants.CREATEACCOUNT_ACTION, ACLMessage.REQUEST);
		}

		@Override
		protected void doSend() {
			createHotelAccount();
		}

		private void createHotelAccount() {
			CreateAccountRequest action_account = new CreateAccountRequest();
			action_account.setHotel(hotelIdentity);
			sendRequest(this.server, action_account,this.protocol, this.sendPerformative);

		}

		@Override
		protected void receiveInform(ACLMessage msg) {
			handleAccount(msg);
		}
		
		protected boolean handleAccount(ACLMessage message) {
			try {
				AccountStatus accountStatus = (AccountStatus) getContentManager().extractContent(message);
				idAccount = accountStatus.getAccount().getId_account();
				
				state.check(State.ACCOUNT_CREATED);
				Logger.logDebug(myName() + ": My bank account is: " + idAccount);
			} catch (CodecException | OntologyException e) {
				Logger.logError(myName()+": " + message.getContent());
				e.printStackTrace();
			}
		
			return true;
		}

		@Override
		protected ClientStep finishOrResend(int performativeReceived) {
			if (performativeReceived==ACLMessage.AGREE) {
				return ClientStep.DONE;
			}else {
				return ClientStep.RESEND;
			}
		}
	}

	private final class MakeRoomBookingBehavior extends GenericServerResponseBehaviour {

		private static final long serialVersionUID = -390060690778340930L;

		public MakeRoomBookingBehavior(AbstractAgent agHotel) {
			super(agHotel,Constants.BOOKROOM_PROTOCOL, ACLMessage.REQUEST);
		}
		
		@Override
		protected ACLMessage doSendResponse(ACLMessage msg) {
			
			// The ContentManager transforms the message content (string) in objects
			try {
				ContentElement ce = getContentManager().extractContent(msg);

				// We expect an action inside the message
				if (ce instanceof Action) {
					Action agAction = (Action) ce;
					Concept conc = agAction.getAction();

					// If the action is BookRoom...
					if (conc instanceof BookRoom) {
						// execute request
						ACLMessage reply = answerBookingRequest(msg,(BookRoom) conc);
						myAgent.send(reply);
						myAgent.getLog().logSendReply(reply);
						
						
						//Send next message
						if (reply.getPerformative()==ACLMessage.AGREE) {
							reply.setPerformative(ACLMessage.INFORM);
						}else if (reply.getPerformative()==ACLMessage.REFUSE) {
							reply.setPerformative(ACLMessage.FAILURE);
						}
						return reply;
					}
				}
			
			} catch (CodecException | OntologyException e) {
				Logger.logError(myName()+": " + msg.getContent());
				e.printStackTrace();
			}

			//Send response to unexpected message
			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			reply.setContent("No sending the right Ontology");
			return reply;
		
		}

		private ACLMessage answerBookingRequest(ACLMessage msg, BookRoom bookData) {
			// send reply
			ACLMessage reply = msg.createReply();
		
			if(bookData!=null){
				if( bookRoom(bookData)){
					reply.setPerformative(ACLMessage.AGREE);
				} else {
					reply.setPerformative(ACLMessage.REFUSE);
				}
			} else {
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			}
			return reply;
		}
		/**
		 * @param book
		 * Save the booking in the DAO
		 * @return
		 */
		private boolean bookRoom(BookRoom book) {
			Stay stay = new Stay();
			stay.setCheckIn(book.getStay().getCheckIn());
			stay.setCheckOut(book.getStay().getCheckOut());
			Price price = new Price();
			price.setPrice(book.getPrice().getAmount());
			hotelmania.group2.dao.BookRoom booking= new hotelmania.group2.dao.BookRoom(stay, price);
			return bookDAO.book(booking);
			
		}

	}
	
	
	private final class HireDailyStaffBehavior extends SendReceiveBehaviour {

		private static final long serialVersionUID = -1320828325347509541L;
		
		/**
		 * @param myAgent
		 * @param protocol
		 * @param serviceToLookUp
		 * @param sendPerformative
		 */
		public HireDailyStaffBehavior(AbstractAgent agHotel) {
			super(agHotel, Constants.SIGNCONTRACT_PROTOCOL, Constants.SIGNCONTRACT_ACTION, ACLMessage.REQUEST);
		}
		@Override
		protected void doSend() {
			
			SignContract requestContract = new SignContract();
			requestContract.setHotel(hotelIdentity);
			requestContract.setContract(hireDailyStaff(getDay()+1));
			sendRequest(this.server, requestContract, this.protocol, this.sendPerformative);
		};
		
		/**
		 * This is invoked on a NewDay event.
		 * 
		 * @param date of the contract
		 */
		Contract hireDailyStaff(int day) {
			Contract contract;
			if (day == Constants.FIRST_DAY) {
				contract = getInitialContract(day);
			} else {
				contract = buildNewContract(day);
			}
			return contract;
		}

		Contract buildNewContract(int day) {
			if (currentContract == null) {
				buildInitialContract(day);
			}
			if (rating > 5) {
				if (rating > 7.5) {//If our rating is very good, we increase prices a lot
					float increment = (float) (currentPrice*0.5);
					currentPrice += increment;//increase price 50%
					currentContract.decreaseQuality((float) (increment*0.5));
				} else {//If our rating is good but not that good, we increase prices but also increase quality of contract
					float increment = (float) (currentPrice*0.25);
					currentPrice += increment;//increase price 25%
					currentContract.decreaseQuality((float) (increment*0.5));//Increase contract quality with half of price raise
				}
			} else {
				if (!bookDAO.isThereAnyBooking()) {
					float decrease = (float) (currentPrice*0.5);
					currentPrice -= decrease;
				} else {
					if (rating > 2.5) {//If the rating is not that bad, we just decrease the quality contract
						float budget = (float) ((currentPrice - currentContract.getCost())*0.5);
						currentContract.increaseQuality(budget);
					} else {//If the rating is too bad, we decrease quality contract and price
						float budget = (float) ((currentPrice - currentContract.getCost())*0.5);
						currentPrice -= budget;
						currentContract.increaseQuality((float) (budget*0.5));
					}
				}

			}
			currentContract.setDate(day);
			//Logger.logDebug(myName() + ": new price: " + this.currentPrice);
			return currentContract.getConcept();
		}

		/**
		 * Default values for staff hiring
		 * @param day 
		 * 
		 * @return
		 */
		Contract getInitialContract(int day) {
			buildInitialContract(day);
			return currentContract.getConcept();
		}
		
		public void buildInitialContract(int day) {
			currentContract = new hotelmania.group2.dao.Contract();
			currentContract.setchef3stars(1);
			currentContract.setRecepcionistExperienced(3);
			currentContract.setRoomService(3);
			currentContract.setDate(day);
			currentPrice = currentContract.getCost();
		}
		@Override
		protected ClientStep finishOrResend(int performativeReceived) {
			if (performativeReceived==ACLMessage.AGREE) {
				return ClientStep.DONE;
			}else {
				return ClientStep.RESEND;
			}
		}
		
	}
	
	
	private final class ProvideOccupancyOfRoomsBehavior extends GenericServerResponseBehaviour {

		private static final long serialVersionUID = -4414753731149819352L;

		public ProvideOccupancyOfRoomsBehavior(AbstractAgent agHotel) {
			super(agHotel,Constants.CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL, ACLMessage.QUERY_REF);
		}
		
		@Override
		protected ACLMessage doSendResponse(ACLMessage message) {
			Predicate predicate = getPredicateFromMessage(message);
			
			if (predicate instanceof NumberOfClientsQueryRef) {
				return answerGetNumberOfClients(message,(NumberOfClientsQueryRef) predicate);
			}else {
				ACLMessage reply = message.createReply();
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				reply.setContent("No sending the right Ontology");
				return reply;
				
			}
		}

		/**
		 * INFORM: If the request is valid
		 * REFUSE: if day is in the future or before day 1.
		 * NOT UNDERSTOOD: if there are missing parameters
		 * @param message
		 * @param numberOfClientsQueryRef
		 * @return
		 */
		private ACLMessage answerGetNumberOfClients(ACLMessage message,	NumberOfClientsQueryRef numberOfClientsQueryRef) {
			ACLMessage reply = message.createReply();

			//missing parameters?
			if (numberOfClientsQueryRef == null) {
				
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				reply.setContent("There are missing parameters: NumberOfClientsQueryRef action or hotel name");

			} else if (!(numberOfClientsQueryRef.getDay() >= Constants.FIRST_DAY && numberOfClientsQueryRef.getDay() <= getDay() )) {
				//invalid day in request?
				reply.setPerformative(ACLMessage.REFUSE);
				reply.setContent("Day is in the future or before day 1.");
				

			} else {
				//request is valid
				hotelmania.ontology.NumberOfClients numberOfClients = getNumberOfClients(numberOfClientsQueryRef.getDay());
				try {
					reply.setPerformative(ACLMessage.INFORM);
					myAgent.getContentManager().fillContent(reply, numberOfClients);
				} catch (CodecException | OntologyException e) {
					Logger.logError(myName()+": " + message.getContent());
					e.printStackTrace();
				}
			}
			return reply;
		}

		private hotelmania.ontology.NumberOfClients getNumberOfClients(int day) { 
			hotelmania.ontology.NumberOfClients numberOfClients = new NumberOfClients(); 
			int clients = bookDAO.getClientsAtDay(day);
			numberOfClients.setNum_clients(clients);
			return numberOfClients;
		}
	}
		
	private final class ProvideRoomPriceBehavior extends GenericServerResponseBehaviour {

		private static final long serialVersionUID = 1955222376582492939L;

		public ProvideRoomPriceBehavior(AbstractAgent agHotel) {
			super(agHotel, Constants.CONSULTROOMPRICES_PROTOCOL, ACLMessage.QUERY_REF);
		}

		
		@Override
		protected ACLMessage doSendResponse(ACLMessage msg) {
			Predicate offer = getPredicateFromMessage(msg);
			// If the action is Registration Request...
			if (offer instanceof hotelmania.ontology.StayQueryRef) {
				// execute request
				return answerRoomPriceOffer(msg,(hotelmania.ontology.StayQueryRef) offer);
			}else {
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				reply.setContent("No sending the right Ontology");
				return reply;
			}
		}
		
				

		/**
		 * @param msg
		 * @param conc
		 * @return
		 */
		private ACLMessage answerRoomPriceOffer(ACLMessage msg,	hotelmania.ontology.StayQueryRef conc) {
			int totalDays;
			ACLMessage reply = msg.createReply();
		
			//missing parameters?
			if (conc == null) {
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				reply.setContent("There are missing parameters: BookinhgOffer protocol or Stay predicate is null");
			}else {
				hotelmania.ontology.Stay stay = conc.getStay();
				//Total numbers of day to stay
				totalDays = stay.getCheckOut()-stay.getCheckIn();

				if (!bookDAO.isThereRoomAvailableAtDays(stay.getCheckIn(),stay.getCheckOut())) {
					//not rooms
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("No rooms available");

				} else {
					//request is valid
					float price = 	calculatePrice(totalDays);
					BookingOffer offer_inform = new BookingOffer();
					hotelmania.ontology.Price priceOffer = new hotelmania.ontology.Price();
					priceOffer.setAmount(price);
					offer_inform.setRoomPrice(priceOffer);
					try {
						reply.setPerformative(ACLMessage.INFORM);
						myAgent.getContentManager().fillContent(reply, offer_inform);
					} catch (CodecException | OntologyException e) {
						Logger.logError(myName()+": " + msg.getContent());
						e.printStackTrace();
					}
				}
			}
			return reply;
		}

		/**
		 * @param stay
		 */
		private float calculatePrice(int totalDays) {
					
			return currentPrice*totalDays;
		}



		

	}
	
	private final class ConsultAccountStatusBehavior extends SendReceiveBehaviour {

		private static final long serialVersionUID = 1955222376582492939L;

		public ConsultAccountStatusBehavior(AbstractAgent agHotel) {
			super(agHotel, Constants.CONSULTACCOUNTSTATUS_PROTOCOL, Constants.CONSULTACCOUNTSTATUS_ACTION, ACLMessage.QUERY_REF);
		}
		
		@Override
		protected void doSend() {
			if(idAccount!=null){
				consultHotelAccountInfo();
			}
		}

		private void consultHotelAccountInfo() {
			AccountStatusQueryRef request = new AccountStatusQueryRef();
			request.setId_account(idAccount);
			sendRequest(this.server, request, this.protocol, this.sendPerformative);
		}
		
		@Override
		protected void receiveInform(ACLMessage message) {
			handleConsultAccount(message);
		}
		
		public void handleConsultAccount(ACLMessage message){
			AccountStatus accountStatus;
			try {
				accountStatus = (AccountStatus) getContentManager().extractContent(message);
				actualBalance = accountStatus.getAccount().getBalance();
	
				Logger.logDebug("Account Balance:" + actualBalance);
			} catch (CodecException  | OntologyException e) {
				Logger.logError(myName()+": " + message.getContent());
				e.printStackTrace();
			}
			
		}
	}
	
	private final class ConsultMyRatingBehavior extends SendReceiveBehaviour {

		private static final long serialVersionUID = 1L;

		public ConsultMyRatingBehavior(AbstractAgent agHotel) {
			super(agHotel, Constants.CONSULTHOTELSINFO_PROTOCOL, Constants.CONSULTHOTELSINFO_ACTION, ACLMessage.QUERY_REF);
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
						hotelmania.group2.dao.BookingOffer bookingOffer = new hotelmania.group2.dao.BookingOffer(new hotelmania.group2.dao.HotelInformation(hotelInformation));
						rating=bookingOffer.getHotelInformation().getRating();
						Logger.logDebug(myName() + ": Number of hotels: 1 = " + hotelInformation.getHotel().getHotel_name());
					}
				} else {
					Logger.logDebug(myName() + ": Null number of hotels");
				}
			} catch (CodecException | OntologyException e) {
				Logger.logError(myName()+": " + message.getContent());
				e.printStackTrace();
			}
		}

		private void processListOfHotels(ContentElementList list) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) instanceof HotelInformation) {
					HotelInformation hotelInformation = (HotelInformation) list.get(i);
					hotelmania.group2.dao.BookingOffer offer = new hotelmania.group2.dao.BookingOffer(new hotelmania.group2.dao.HotelInformation(hotelInformation));
					if (myAgent.getAID()==offer.getHotelInformation().getHotel().getAgent()){
						rating=offer.getHotelInformation().getRating();
					}
				}

			}
		}
	}		

}
