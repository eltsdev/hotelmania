/**
 * 
 */
package hotelmania.group2.hotel;

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
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * @author user
 *
 */
public class AgHotel extends AbstractAgent {

	private SequentialBehaviour stepsForCreationHotel;
	private SequentialBehaviour stepsForRoomPrice;
	private static final long serialVersionUID = 6197364680986347122L;
	private final Hotel hotelIdentity = new Hotel();
	private BookingDAO bookDAO = new BookingDAO();
	private float actualBalance;
	private float myRating;
	private Integer idAccount;

	@Override
	public String myName() {
		return Constants.HOTEL_NAME;
	}

	
	@Override
	protected void doOnNewDay() {
		super.doOnNewDay();
		Logger.logDebug("HOTEL: DAY IS "+ getDay() + " =========================================================================");
		addBehaviour(new SignContractBehavior(this));
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
				myAgent.addBehaviour(new ProvideRoomInfoBehavior(AgHotel.this));
				myAgent.addBehaviour(new MakeRoomBookingBehavior(AgHotel.this));
				myAgent.addBehaviour(new ProvideHotelNumberOfClientsBehavior(AgHotel.this));
				
				//Behaviors to calculate room prices and provide it to Client
				stepsForRoomPrice = new SequentialBehaviour(AgHotel.this);
				stepsForRoomPrice.addSubBehaviour(new ConsultBankAccountInfoBehavior(AgHotel.this));
				stepsForRoomPrice.addSubBehaviour(new ConsultMyRatingBehavior(AgHotel.this));
				//TODO decide when to start this behaviors addBehaviour(stepsForRoomPrice);

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
		protected boolean finishOrResend(int performativeReceived) {
			if (performativeReceived==ACLMessage.AGREE) {
				return true;
			}else {
				return false;
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
		protected boolean finishOrResend(int performativeReceived) {
			if (performativeReceived==ACLMessage.INFORM) {
				return true;
			}else {
				return false;
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
	
	
	private final class SignContractBehavior extends SendReceiveBehaviour {

		private static final long serialVersionUID = -1320828325347509541L;
		
		/**
		 * @param myAgent
		 * @param protocol
		 * @param serviceToLookUp
		 * @param sendPerformative
		 */
		public SignContractBehavior(AbstractAgent agHotel) {
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
				contract = getInitialContract();
			} else {
				contract = buildNewContract(day);
			}
			System.out.println(myName() +": Day staffff========================================> "+ day);
			return contract;
		}

		Contract buildNewContract(int day) {
			//TODO dynamic
			Contract c = new Contract();
			c.setDay(day);
			c.setChef_1stars(1);
			c.setChef_2stars(0);
			c.setChef_3stars(0);
			c.setRecepcionist_experienced(2);
			c.setRecepcionist_novice(2);
			c.setRoom_service_staff(20);
			return c;
		}

		/**
		 * Default values for staff hiring
		 * 
		 * @return
		 */
		Contract getInitialContract() {
			Contract c = new Contract();
			c.setDay(1);
			c.setChef_1stars(1);
			c.setChef_2stars(0);
			c.setChef_3stars(0);
			c.setRecepcionist_experienced(2);
			c.setRecepcionist_novice(2);
			c.setRoom_service_staff(20);
			return c;
		}
		
		@Override
		protected boolean finishOrResend(int performativeReceived) {
			if (performativeReceived==ACLMessage.INFORM) {
				return true;
			}else {
				return false;
			}
		}
		
	}
	
	
	private final class ProvideHotelNumberOfClientsBehavior extends GenericServerResponseBehaviour {

		private static final long serialVersionUID = -4414753731149819352L;

		public ProvideHotelNumberOfClientsBehavior(AbstractAgent agHotel) {
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
		
	private final class ProvideRoomInfoBehavior extends GenericServerResponseBehaviour {

		private static final long serialVersionUID = 1955222376582492939L;

		public ProvideRoomInfoBehavior(AbstractAgent agHotel) {
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
			float price = 50;
			//TODO Calculate price Properly
			//price = bookDAO.getActualPrice()*totalDays;
			
			return price;
		}



		

	}
	
	private final class ConsultBankAccountInfoBehavior extends SendReceiveBehaviour {

		private static final long serialVersionUID = 1955222376582492939L;

		public ConsultBankAccountInfoBehavior(AbstractAgent agHotel) {
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
	
				Logger.logDebug("Account Balance:" + accountStatus.getAccount().getBalance());
			} catch (CodecException  | OntologyException e) {
				Logger.logError(myName()+": " + message.getContent());
				e.printStackTrace();
			}
			
		}
		
		@Override
		protected boolean finishOrResend(int performativeReceived) {
			return true;
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
						myRating=bookingOffer.getHotelInformation().getRating();
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
						myRating=offer.getHotelInformation().getRating();
					}
				}

			}
		}
		
		@Override
		protected boolean finishOrResend(int performativeReceived) {
			return true;
		}
	}		

}