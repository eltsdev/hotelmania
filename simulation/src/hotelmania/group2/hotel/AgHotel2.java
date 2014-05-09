package hotelmania.group2.hotel;
import hotelmania.group2.platform.Constants;
import hotelmania.group2.platform.MetaAgent;
import hotelmania.group2.platform.MetaCyclicBehaviour;
import hotelmania.group2.platform.MetaSimpleBehaviour;
import hotelmania.ontology.BookRoom;
import hotelmania.ontology.Booking;
import hotelmania.ontology.Contract;
import hotelmania.ontology.CreateAccount;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.HotelsInfoRequest;
import hotelmania.ontology.RegistrationRequest;
import hotelmania.ontology.SignContract;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgHotel2 extends MetaAgent {
	
	private static final long serialVersionUID = 2893904717857535232L;

	//------------------------------------------------- 
	// Agent Attributes
	//-------------------------------------------------

	boolean registered;

	@Override
	protected void setup() {
		super.setup();

		addBehaviour(new RegisterInHotelmaniaBehavior(this));

//		addBehaviour(new MakeRoomBookingBehavior(this));
//		addBehaviour(new ProvideRoomInfoBehavior(this));
//		addBehaviour(new CreateBankAccountBehavior(this));
//		addBehaviour(new ConsultHotelInfoBehavior(this));
//		 TODO Consult account status
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
		hireDailyStaffBehaviorAction();
	}

	// --------------------------------------------------------
	// BEHAVIOURS
	// --------------------------------------------------------

	private final class RegisterInHotelmaniaBehavior extends MetaSimpleBehaviour {
		private static final long serialVersionUID = 1256090117313507535L;
		private AID agHotelmania;

		private RegisterInHotelmaniaBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			// Register hotel
			if (agHotelmania==null) {
				// Search hotelmania agent
				agHotelmania = locateAgent(Constants.REGISTRATION_ACTION, myAgent);
				//block();
				//return;
			} else {
				registerHotel();
				this.setDone(true);
			}
			
		}

		private void registerHotel() {
			RegistrationRequest register = new RegistrationRequest();
			Hotel hotel = new Hotel();
			hotel.setHotel_name(name);
			register.setHotel(hotel);
			
			sendRequest(myAgent, agHotelmania, register, codec, ontology, Constants.REGISTRATION_PROTOCOL, ACLMessage.REQUEST);
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
			ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));

			/*
			 * If no message arrives
			 */
			if (msg == null) {
				block();
				return;
			}

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
						ACLMessage reply = answerBookingRequest(msg, (BookRoom) conc);
						myAgent.send(reply);

						System.out.println(myAgent.getLocalName() + ": answer sent -> " + log);
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
		private ACLMessage answerBookingRequest(ACLMessage msg,
				BookRoom bookData) {
			System.out.println(myAgent.getLocalName()
					+ ": received Registration Request from "
					+ (msg.getSender()).getLocalName());
			// send reply
			ACLMessage reply = msg.createReply();

			if (bookData != null) {
				if (bookRoom(bookData.getBooking())) {
					reply.setPerformative(ACLMessage.AGREE);
					this.log = Constants.AGREE;
				} else {
					reply.setPerformative(ACLMessage.REFUSE);
					this.log = Constants.REFUSE;
				}
			} else {
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				this.log = Constants.NOT_UNDERSTOOD;
			}

			return reply;
			}

		private boolean bookRoom(Booking book) {
			bookDao.booking(book.getDays(), book.getStartDay());
			return true;
		}

	}

	private final class ProvideRoomInfoBehavior extends CyclicBehaviour {

		private static final long serialVersionUID = 1955222376582492939L;

		public ProvideRoomInfoBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			//TODO
			block();
		}

	}

	private final class CreateBankAccountBehavior extends MetaSimpleBehaviour {
	
		private static final long serialVersionUID = 1955222376582492939L;
		
		private AID agBank;
	
		public CreateBankAccountBehavior(Agent a) {
			super(a);
		}
	
		@Override
		public void action() {
			// Create hotel account
			if (agBank ==null ) {
				locateAgent(Constants.CREATEACCOUNT_ACTION, myAgent);
			} else {
				createHotelAccount();
			}
		}
	
		private void createHotelAccount() {
			CreateAccount action_account = new CreateAccount();
			Hotel hotel = new Hotel();
			hotel.setHotel_name(name);
			action_account.setHotel(hotel);
			action_account.setBalance(1000000);

			sendRequest(myAgent, agBank, action_account, codec,
					ontology, Constants.CREATEACCOUNT_PROTOCOL,
					ACLMessage.REQUEST);

			this.setDone(true);
		}
	
	}
	
	/**
	 * Behavior for hiring the staff 
	 */
	private void hireDailyStaffBehaviorAction() {
		
		AID agAgency = locateAgent(Constants.SIGNCONTRACT_ACTION, this);
		
		SignContract request = new SignContract();
		
		Hotel hotel = new Hotel();
		hotel.setHotel_name(name);
		request.setHotel(hotel);
		request.setContract(hireDailyStaff(today+1));
		
		this.sendRequest(this, agAgency, request, codec, ontology, Constants.SIGNCONTRACT_PROTOCOL, ACLMessage.REQUEST);
		
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
			contract = buildNewContract();
		}
	
		return contract;
	}

	Contract buildNewContract() {
		//Contract old = dao.getContractsByHotel(name).get(0);
		Contract c = new Contract();
		c.setChef_1stars(5);
		c.setChef_2stars(5);
		c.setChef_3stars(5);
		c.setRecepcionist_experienced(2);
		c.setRecepcionist_novice(2);
		c.setRoom_service_staff(20);
		return c ;
	}

	/**
	 * Default values for staff hiring
	 * @return
	 */
	Contract getInitialContract() {
		Contract c = new Contract();
		c.setChef_1stars(5);
		c.setChef_2stars(5);
		c.setChef_3stars(5);
		c.setRecepcionist_experienced(2);
		c.setRecepcionist_novice(2);
		c.setRoom_service_staff(20);
		return c ;
	}
	
	
	private final class ConsultHotelInfoBehavior extends MetaSimpleBehaviour {
		
		private static final long serialVersionUID = 1L;
		private AID agHotelmania;
		
		private ConsultHotelInfoBehavior(Agent a) {
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
			HotelsInfoRequest request = new HotelsInfoRequest();
			sendRequest(this.getAgent(), hotelmania, request, codec, ontology,Constants.CONSULTHOTELSINFO_PROTOCOL,ACLMessage.QUERY_REF);
		}

	}

	
	@Override
	public void receivedAcceptance(ACLMessage message) {
		//TODO switch by message.getProtocol()
	}

	@Override
	public void receivedReject(ACLMessage message) {
		// TODO Auto-generated method stub
		if (message.getProtocol().equals(Constants.REGISTRATION_PROTOCOL)) {
			logRejectedMessage(Constants.REGISTRATION_PROTOCOL, message);
			//TODO: DEFINE: addBehaviour(new RegisterInHotelmaniaBehavior(this));
		} else if (message.getProtocol().equals(Constants.CREATEACCOUNT_PROTOCOL)) {
			logRejectedMessage(Constants.CREATEACCOUNT_PROTOCOL, message);
		} else if (message.getProtocol().equals(Constants.CONSULTHOTELSINFO_PROTOCOL)) {
			logRejectedMessage(Constants.CONSULTHOTELSINFO_PROTOCOL, message);
		}
	}

	@Override
	public void receivedNotUnderstood(ACLMessage message) {
		// TODO Auto-generated method stub
		if (message.getProtocol().equals(Constants.REGISTRATION_PROTOCOL)) {
			logNotUnderstoodMessage(Constants.REGISTRATION_ACTION, message);
		} else if (message.getProtocol().equals(Constants.CREATEACCOUNT_PROTOCOL)) {
			logNotUnderstoodMessage(Constants.CREATEACCOUNT_PROTOCOL, message);
		} else if (message.getProtocol().equals(Constants.CONSULTHOTELSINFO_PROTOCOL)) {
			logNotUnderstoodMessage(Constants.CONSULTHOTELSINFO_PROTOCOL, message);
		}	
	}
}