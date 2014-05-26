package hotelmania.group2.hotel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import hotelmania.group2.dao.BookingDAO;
import hotelmania.group2.dao.Price;
import hotelmania.group2.dao.Stay;
import hotelmania.group2.platform.Constants;
import hotelmania.group2.platform.Logger;
import hotelmania.group2.platform.MetaAgent;
import hotelmania.group2.platform.MetaCyclicBehaviour;
import hotelmania.group2.platform.MetaSimpleBehaviour;
import hotelmania.ontology.AccountStatus;
import hotelmania.ontology.AccountStatusQueryRef;
import hotelmania.ontology.BookRoom;
import hotelmania.ontology.BookingOffer;
import hotelmania.ontology.Contract;
import hotelmania.ontology.CreateAccountRequest;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.NumberOfClients;
import hotelmania.ontology.NumberOfClientsQueryRef;
import hotelmania.ontology.RegistrationRequest;
import hotelmania.ontology.SignContract;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.Predicate;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgHotel2 extends MetaAgent {

	private static final long serialVersionUID = 2893904717857535232L;
	private BookingDAO bookDAO = new BookingDAO();
	
	// -------------------------------------------------
	// Agent Attributes
	// -------------------------------------------------

	// Used in Create Account (when receive the Inform) and consultHotelAccountInfo
	private Integer id_account;
	boolean registered;
	private final Hotel identity = new Hotel();
	private int day = 0;

	@Override
	protected void setup() {
		super.setup();
		loadProperties();
		this.registerServices(Constants.CONSULTHOTELNUMBEROFCLIENTS_ACTION, Constants.BOOKROOM_ACTION, Constants.CONSULTROOMPRICES_ACTION);
		
		identity.setHotel_name(myName());
		identity.setHotelAgent(getAID());
		
		addBehaviour(new RegisterInHotelmaniaBehavior(this));
		addBehaviour(new CreateBankAccountBehavior(this));
		addBehaviour(new ConsultBankAccountInfoBehavior(this));
		addBehaviour(new ProvideHotelNumberOfClientsBehavior(this));
		addBehaviour(new MakeRoomBookingBehavior(this));
		addBehaviour(new ProvideRoomInfoBehavior(this));
		// addBehaviour(new ConsultHotelInfoBehavior(this));

		
		// TODO Consult account status
	}
	
	private void loadProperties() {
		// create and load default properties
		Properties defaultProps = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream("resources/settings.properties");
			defaultProps.load(in);
			//Official settings
			Constants.DAY_IN_MILLISECONDS = Integer.parseInt(defaultProps.getProperty("day.length","15"))*1000;
			Constants.SIMULATION_DAYS = Integer.parseInt(defaultProps.getProperty("simulation.days","10"));
			Constants.CLIENTS_PER_DAY=Integer.parseInt(defaultProps.getProperty("simulation.clients_per_day","2"));
			Constants.CLIENTS_BUDGET=Integer.parseInt(defaultProps.getProperty("clients.budget","90"));
			Constants.CLIENTS_BUDGET_VARIANCE=Integer.parseInt(defaultProps.getProperty("clients.budget_variance","20"));
			Constants.SIMULATION_TIME_TO_START = Integer.parseInt(defaultProps.getProperty("simulation.time_to_start","1"));
			//Private settings
			Constants.REPORT_FILE=defaultProps.getProperty("","results.txt");
			Constants.LOG_DEBUG=Boolean.parseBoolean(defaultProps.getProperty("log.debug","true"));
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (Constants.SIMULATION_DAYS <= 1) {
			//throw new Exception("ERROR: The number of days in the settings does not allow the generation of valid booking dates for clients. Please fix it: "+Constants.SIMULATION_DAYS);
			Logger.logError("The number of days in the settings does not allow the generation of valid booking dates for clients. Please fix it to at least 2 days."+Constants.SIMULATION_DAYS);
		}
	}
	
	@Override
	public String myName() {
		return Constants.HOTEL_NAME;
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
		day ++;
		hireDailyStaffBehaviorAction();
	}

	// --------------------------------------------------------
	// BEHAVIOURS
	// --------------------------------------------------------

	private final class RegisterInHotelmaniaBehavior extends
	MetaSimpleBehaviour {
		private static final long serialVersionUID = 1256090117313507535L;
		private AID agHotelmania;

		private RegisterInHotelmaniaBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			// Register hotel
			if (agHotelmania == null) {
				// Search hotelmania agent
				agHotelmania = locateAgent(Constants.REGISTRATION_ACTION, myAgent);
				// block();
				// return;
			} else {
				registerHotel();
				this.setDone(true);
			}

		}

		private void registerHotel() {
			RegistrationRequest register = new RegistrationRequest();
			register.setHotel(identity);

			sendRequest(agHotelmania, register,	Constants.REGISTRATION_PROTOCOL, ACLMessage.REQUEST);
		}
	}

	private final class MakeRoomBookingBehavior extends MetaCyclicBehaviour {

		private static final long serialVersionUID = -390060690778340930L;

		public MakeRoomBookingBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			/*
			 * Look for messages
			 */
			ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchProtocol(Constants.BOOKROOM_PROTOCOL)),
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
			
		
			/*
			 * If no message arrives
			 */
			if (msg == null) {
				block();
				return;
			}
			
			log.logReceivedMsg(msg);

			/*
			 * The ContentManager transforms the message content (string) in
			 */
			try {
				ContentElement ce = getContentManager().extractContent(msg);

				// We expect an action inside the message
				if (ce instanceof Action) {
					Action agAction = (Action) ce;
					Concept conc = agAction.getAction();

					// If the action is BookRoom...
					if (conc instanceof BookRoom) {
						// execute request
						answerBookingRequest(msg,(BookRoom) conc);
					}
				}
				
				

			} catch (CodecException | OntologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**
		 * @param msg
		 * @param conc
		 * @return
		 */
		private void answerBookingRequest(ACLMessage msg, BookRoom bookData) {
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
			myAgent.send(reply);
			log.logSendReply(reply);
		}

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

	
	private final class ProvideRoomInfoBehavior extends MetaCyclicBehaviour {

		private static final long serialVersionUID = 1955222376582492939L;

		public ProvideRoomInfoBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			
			/*
			 * Look for messages
			 */
			ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchProtocol(Constants.CONSULTROOMPRICES_PROTOCOL)),
					MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF)));
			
			if (msg == null) {
				block();
				return;
			}
			
			log.logReceivedMsg(msg);
			
			Predicate conc = this.getPredicateFromMessage(msg);
			// If the action is Registration Request...
			if (conc instanceof hotelmania.ontology.StayQueryRef) {
				// execute request
				ACLMessage reply = answerRoomPriceOffer(msg,(hotelmania.ontology.StayQueryRef) conc);
				// send reply
				myAgent.send(reply);
				log.logSendReply(reply);
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

	/**
	 * @author user
	 *
	 */
	private final class CreateBankAccountBehavior extends MetaSimpleBehaviour {

		private static final long serialVersionUID = 1955222376582492939L;

		private AID agBank;

		public CreateBankAccountBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			// Create hotel account
			if (agBank == null) {
				agBank = locateAgent(Constants.CREATEACCOUNT_ACTION, myAgent);
			} else {
				createHotelAccount();
			}
		}

		private void createHotelAccount() {
			CreateAccountRequest action_account = new CreateAccountRequest();
			action_account.setHotel(identity);
			sendRequest(agBank, action_account, Constants.CREATEACCOUNT_PROTOCOL, ACLMessage.REQUEST);

			this.setDone(true);
		}

	}

	private final class ConsultBankAccountInfoBehavior extends
	MetaSimpleBehaviour {

		private static final long serialVersionUID = 1955222376582492939L;

		private AID agBank;

		public ConsultBankAccountInfoBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			// Consult hotel account
			if (agBank == null) {
				agBank = locateAgent(Constants.CONSULTACCOUNTSTATUS_ACTION, myAgent);
			} else {
				if(id_account!=null){
					consultHotelAccountInfo();
				}
				
			}
		}

		private void consultHotelAccountInfo() {
			AccountStatusQueryRef request = new AccountStatusQueryRef();
			request.setId_account(id_account);// TODO set real account id

			sendRequest(agBank, request, Constants.CONSULTACCOUNTSTATUS_PROTOCOL, ACLMessage.QUERY_REF);

			this.setDone(true);
		}

	}

	/**
	 * Behavior for hiring the staff
	 */
	private void hireDailyStaffBehaviorAction() {
		AID agAgency = locateAgent(Constants.SIGNCONTRACT_ACTION, this);

		SignContract request = new SignContract();

		request.setHotel(identity);
		
		try {
			//TODO Contract must be dynamic
			request.setContract(hireDailyStaff(getDay()+1));
			//System.out.println("[HOTEL] Wants to hire staff for day: "+(request.getContract().getDay())+ " Today is:"+day);
			this.sendRequest(agAgency, request, Constants.SIGNCONTRACT_PROTOCOL, ACLMessage.REQUEST);
		} catch (Exception e) {//this never happens
			e.printStackTrace();
		}
	}

	/**
	 * This is invoked on a NewDay event.
	 * 
	 * @param date
	 *            of the contract
	 */
	Contract hireDailyStaff(int day) {
		Contract contract;
		if (day == Constants.FIRST_DAY) {
			contract = getInitialContract();
		} else {
			contract = buildNewContract(day);
		}

		return contract;
	}

	Contract buildNewContract(int day) {
		Contract c = new Contract();
		c.setDay(day);
		c.setChef_1stars(5);
		c.setChef_2stars(5);
		c.setChef_3stars(5);
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
		c.setChef_1stars(5);
		c.setChef_2stars(5);
		c.setChef_3stars(5);
		c.setRecepcionist_experienced(2);
		c.setRecepcionist_novice(2);
		c.setRoom_service_staff(20);
		return c;
	}

	private final class ConsultMyRatingBehavior extends MetaSimpleBehaviour {

		private static final long serialVersionUID = 1L;
		private AID agHotelmania;

		private ConsultMyRatingBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			if (agHotelmania == null) {
				agHotelmania = locateAgent(Constants.CONSULTHOTELSINFO_ACTION, myAgent);
			} else {
				this.consultHotelInfo(agHotelmania);
				this.setDone(true);
			}
		}

		private void consultHotelInfo(AID hotelmania) {
			sendRequestEmpty(hotelmania, Constants.CONSULTHOTELSINFO_PROTOCOL, ACLMessage.QUERY_REF);
		}

	}

	private final class ProvideHotelNumberOfClientsBehavior extends
	MetaCyclicBehaviour {

		private static final long serialVersionUID = -4414753731149819352L;

		public ProvideHotelNumberOfClientsBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			/*
			 * Look for messages
			 */
			ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchProtocol(Constants.CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL)),
					MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF)));

			/*
			 * If no message arrives
			 */
			if (msg == null) {
				block();
				return;
			}
			
			log.logReceivedMsg(msg);
			
			Predicate predicate = this.getPredicateFromMessage(msg);
			// If the action is Registration Request...
			if (predicate instanceof NumberOfClientsQueryRef) {
				// execute request
				ACLMessage reply = answerGetNumberOfClients(msg,(NumberOfClientsQueryRef) predicate);
				// send reply
				myAgent.send(reply);
				log.logSendReply(reply);
			}
		}

		/**
		 * INFORM: If the request is valid
		 * REFUSE: if day is in the future or before day 1.
		 * NOT UNDERSTOOD: if there are missing parameters
		 * @param msg
		 * @param numberOfClientsQueryRef
		 * @return
		 */
		private ACLMessage answerGetNumberOfClients(ACLMessage msg,	NumberOfClientsQueryRef numberOfClientsQueryRef) {
			ACLMessage reply = msg.createReply();

			boolean failure = false;
			int theCurrentDay = Constants.FIRST_DAY;
			try {
				theCurrentDay = getDay();
			} catch (Exception e1) {
				failure=true;
			}
			
			//missing parameters?
			if (numberOfClientsQueryRef == null) {
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				reply.setContent("There are missing parameters: NumberOfClientsQueryRef action or hotel name");

			} else if (failure || !(numberOfClientsQueryRef.getDay() >= Constants.FIRST_DAY && numberOfClientsQueryRef.getDay() <= theCurrentDay )) {
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
					e.printStackTrace();
				}
			}
			
			log.logSendReply(reply);
			return reply;
		}

		private hotelmania.ontology.NumberOfClients getNumberOfClients(int day) { 
			hotelmania.ontology.NumberOfClients numberOfClients = new NumberOfClients(); 
			int clients = bookDAO.getClientsAtDay(day);
			numberOfClients.setNum_clients(clients);
			return numberOfClients;
		}
	}

	@Override
	public void receivedAcceptance(ACLMessage message) {
		// TODO switch by message.getProtocol()
	}

	@Override
	public void receivedReject(ACLMessage message) {
		if (message.getProtocol().equals(Constants.REGISTRATION_PROTOCOL)) {
			// TODO: DEFINE: addBehaviour(new
			// RegisterInHotelmaniaBehavior(this));
		} else if (message.getProtocol().equals(Constants.CREATEACCOUNT_PROTOCOL)) {
		} else if (message.getProtocol().equals(Constants.CONSULTHOTELSINFO_PROTOCOL)) {
		} else if (message.getProtocol().equals(Constants.BOOKROOM_PROTOCOL)){
			
		}
		/*
		 * TODO include cases for: MakeRoomBookingBehavior
		 * ProvideRoomInfoBehavior Consult account status
		 */
	}

	@Override
	public void receivedNotUnderstood(ACLMessage message) {
		if (message.getProtocol().equals(Constants.REGISTRATION_PROTOCOL)) {

		} else if (message.getProtocol().equals(Constants.CREATEACCOUNT_PROTOCOL)) {

		} else if (message.getProtocol().equals(Constants.CONSULTHOTELSINFO_PROTOCOL)) {

		}else if( message.getProtocol().equals(Constants.BOOKROOM_PROTOCOL)){
			
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hotelmania.group2.platform.MetaAgent#receiveInform()
	 */
	@Override
	public void receivedInform(ACLMessage message) {
		if (message.getProtocol().equals(Constants.CREATEACCOUNT_PROTOCOL)) {
			try {
				AccountStatus accountStatus = (AccountStatus) getContentManager().extractContent(message);
				this.id_account = accountStatus.getAccount().getId_account();
			} catch (CodecException | OntologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else if (message.getProtocol().equals(Constants.CONSULTACCOUNTSTATUS_ACTION)) {
			try {
				AccountStatus accountStatus = (AccountStatus) getContentManager().extractContent(message);
				Logger.logDebug("Account Balance:" + accountStatus.getAccount().getBalance());
			} catch (CodecException | OntologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public int getDay() {
		System.err.println("HOTEL: DAY IS = "+day);
		return day;
	}
	
}
