package hotelmania.group2.platform;

import java.util.HashMap;

import hotelmania.group2.dao.BookingOffer;
import hotelmania.group2.dao.Client;
import hotelmania.group2.dao.RatingInput;
import hotelmania.ontology.BookRoom;
import hotelmania.ontology.ConsultHotelStaff;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.HotelInformation;
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
import jade.lang.acl.MessageTemplate;

public class AgClient extends AbstractAgent {

	private static final long serialVersionUID = 6748170421157254696L;
	private enum Step {START, RECEIVING_RESPONSES, DONE};
	private Client client;
	private SequentialBehaviour stepsForBooking;
	
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
		} else {
			Logger.logError(myName()+": Invalid parameters to create this agent client");
		}

		//Add behaviors
		stepsForBooking = new SequentialBehaviour(this) {
			private static final long serialVersionUID = 7599237954637997373L;

			@Override
			public int onEnd() {
				//Die
				doDelete();
				Logger.logDebug(myName()+": DIED");
				return super.onEnd();
			}
		};

		stepsForBooking.addSubBehaviour(new GetHotelsFromHotelmaniaBehavior(this));
		stepsForBooking.addSubBehaviour(new CallForOffersBehavior(this));
		stepsForBooking.addSubBehaviour(new RequestBookingInHotelBehavior(this));
		
		addBehaviour(stepsForBooking);
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
		super.doOnNewDay();
		
		// Check checkin date deadline 
		if ( client.getStay().getCheckIn() >= getDay() && this.client.getBookingDone()==null ) {
			//Die
			doDelete();
		}
		
		// Starts data collection while staying in the hotel
		else if (client.getBookingDone()!=null && client.getCheckInDate() >= getDay()  &&  getDay() <= client.getCheckOutDate() ) {
			addBehaviour(new ConsultHotelNumberOfClientsBehavior(this));
			addBehaviour(new ConsultHotelStaffBehavior(this));
		}

		// After the staying
		else if (getDay() > client.getCheckOutDate() && client.isDataForRatingComplete()) {
			addBehaviour(new RateHotelBehavior(this));
			addBehaviour(new MakeDepositBehavior(this));
		}
			
	}

	// --------------------------------------------------------
	// Behaviors
	// --------------------------------------------------------

	private final class GetHotelsFromHotelmaniaBehavior extends MetaSimpleBehaviour {
		private static final long serialVersionUID = 1L;
		private AID hotelmania;
		private MessageTemplate messageTemplate;  
		private Step step = Step.START;

		public GetHotelsFromHotelmaniaBehavior(Agent a) {
			super(a);
			
			messageTemplate = MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchProtocol(Constants.CONSULTHOTELSINFO_PROTOCOL));
		}

		@Override
		public void action() {
			switch (step) {
			
			case START:
				
				if (hotelmania == null) {
					hotelmania = locateAgent(Constants.CONSULTHOTELSINFO_ACTION, myAgent);
				} else {
					sendRequestEmpty(hotelmania, Constants.CONSULTHOTELSINFO_PROTOCOL, ACLMessage.QUERY_REF);
					step = Step.RECEIVING_RESPONSES;
				}				
				break;
				
			case RECEIVING_RESPONSES:

				ACLMessage msg = receive(messageTemplate);
				if (msg == null) {
					// No message arrives
					block();					
				}
				//A New Message
				else {
					log.logReceivedMsg(msg);
					
					switch (msg.getPerformative()) {
					
					case ACLMessage.INFORM:
						boolean resultOK = handleConsultHotelsInfoInform(msg);
						if (resultOK) {
							step = Step.DONE;
						}
						break;

					case ACLMessage.REFUSE:
						// keep trying (does not change the state)
						break;
					case ACLMessage.NOT_UNDERSTOOD:
						// keep trying (does not change the state)
						break;
					default:
						// keep trying (does not change the state)
						break;
					}

				}
				break;
			
			case DONE:
			default:
				setDone(true);
				break;
			}
		}
		
		private boolean handleConsultHotelsInfoInform(ACLMessage message) {
			try {
				ContentElement content = getContentManager()
						.extractContent(message);
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

					return true;
				} else {
					Logger.logDebug(myName() + ": Null number of hotels");
				}
			} catch (CodecException | OntologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Logger.logDebug("Message: " + message.getContent());
			}
			
			return false;
		}

		private void processListOfHotels(ContentElementList list) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) instanceof HotelInformation) {
					HotelInformation hotelInformation = (HotelInformation) list.get(i);
					BookingOffer booking = new BookingOffer(new hotelmania.group2.dao.HotelInformation(hotelInformation));
					client.getOffers().add(booking);
				}

			}
		}
	}

	class CallForOffersBehavior extends MetaSimpleBehaviour {

		private static final long serialVersionUID = -2493514102408084980L;
		private MessageTemplate messageTemplate;
		private Step step = Step.START;

		public CallForOffersBehavior(Agent a) {
			super(a);
			messageTemplate = MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchProtocol(Constants.CONSULTROOMPRICES_PROTOCOL));
		}
		
		@Override
		public void action() {
			switch (step) {
			
			case START:
				//Call for booking proposals to all hotels
				
				//Create StayQueryRef predicate
				Stay stay = new Stay();
				stay.setCheckIn(client.getCheckInDate());
				stay.setCheckOut(client.getCheckOutDate());
				StayQueryRef query = new StayQueryRef();
				query.setStay(stay);

				//Send
				for (BookingOffer offer : client.getOffers()) {
					AID hotel = offer.getHotelInformation().getHotel().getAgent();
					sendRequestPredicate(hotel, query, Constants.CONSULTROOMPRICES_PROTOCOL, ACLMessage.QUERY_REF);
				}
				
				setTimeout();
				
				step = Step.RECEIVING_RESPONSES;
				break;
				
			case RECEIVING_RESPONSES:

				ACLMessage msg = receive(messageTemplate);
				if (msg == null) {
					// No message arrives
					block();					
				}
				//A New Message
				else {
					log.logReceivedMsg(msg);
					
					switch (msg.getPerformative()) {
					
					case ACLMessage.INFORM:
						saveOffer(msg);
						step = Step.DONE;
						break;

					case ACLMessage.REFUSE:
						// ignore
						break;
					case ACLMessage.NOT_UNDERSTOOD:
						// ignore
						break;
					default:
						// ignore
						break;
					}

				}
				break;
			
			case DONE:
			default:
				setDone(true);
				break;
			}
		}

		private void setTimeout() {
			//Set timeout
			long timeout = (long) (Constants.DAY_IN_MILLISECONDS * 0.25);
			myAgent.addBehaviour(new WakerBehaviour(myAgent, timeout) {
				private static final long serialVersionUID = 1L;
				@Override
				protected void handleElapsedTimeout() {
					step = Step.DONE;
				}
			});
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
				// TODO Auto-generated catch block
				e.printStackTrace();
				Logger.logDebug(myName() + ": Message: " + message.getContent());
			}

			return false;
		}
	}
	
	class ChooseBestOffer extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;

		public ChooseBestOffer(Agent a) {
			super(a);
		}
		
		@Override
		public void action() {
			BookingOffer bestOffer = client.computeBestRoomPrice();
			boolean accepted = false;
			if (bestOffer == null) {
				Logger.logDebug(getLocalName()+ ": No minimum offers achieved. Total: " + client.getOffers().size());
				accepted = false;
			} else if(client.acceptOffer(bestOffer)){
				Logger.logDebug(getLocalName() + ": Best offer ACCEPTED :" + bestOffer.getHotelInformation().getHotel().getName() + " Price: " + bestOffer.getPrice());
				accepted = true;
			} else if (getDay() == client.getCheckInDate()) { //TODO == or == -1 day
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
		}
	}
	
	class RequestBookingInHotelBehavior extends MetaSimpleBehaviour {
		
		private static final long serialVersionUID = 1790087472549757374L;
		private MessageTemplate messageTemplate;
		private Step step = Step.START;
		private BookingOffer offerChosen;

		private RequestBookingInHotelBehavior(Agent a) {
			super(a);
			messageTemplate = MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchProtocol(Constants.BOOKROOM_PROTOCOL));
		}

		public void action() {
			switch (step) {
			case START:
				offerChosen = client.computeBestRoomPrice();
				bookRoom(offerChosen);
				step = Step.RECEIVING_RESPONSES;
				break;

			case RECEIVING_RESPONSES:
				
				ACLMessage msg = receive(messageTemplate);
				if (msg == null) {
					// No message arrives
					block();					
				}
				//A New Message
				else {
					log.logReceivedMsg(msg);
					
					switch (msg.getPerformative()) {
					case ACLMessage.INFORM:
						hotelmania.group2.dao.Price price = new hotelmania.group2.dao.Price();
						price.setPrice(offerChosen.getPrice());
						hotelmania.group2.dao.BookRoom booking = new hotelmania.group2.dao.BookRoom(client.getStay(), price);
						
						client.setBookingDone(booking);
						client.setHotelOfBookingDone(offerChosen.getHotelInformation());
						break;
					case ACLMessage.FAILURE:
					case ACLMessage.REFUSE:
					case ACLMessage.NOT_UNDERSTOOD:
					default:
						// Restart the whole process
						stepsForBooking.reset();
						break;
					}
				}
				step = Step.DONE;
				break;

			
			case DONE:
			default:
				this.setDone(true);
				break;
			}
			
		}

		private void bookRoom(BookingOffer bookingOffer) {
			AID agHotel = bookingOffer.getHotelInformation().getHotel().getAgent();
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
			sendRequest(agHotel, action_booking, Constants.BOOKROOM_PROTOCOL, ACLMessage.REQUEST);
		}

	}

	class ConsultHotelNumberOfClientsBehavior extends MetaSimpleBehaviour {

		private static final long serialVersionUID = 1L;
	
		private MessageTemplate messageTemplate;  
		private Step step = Step.START;
		private AID hotel = client.getHotelOfBookingDone().getHotel().getAgent();
		private int day;

		private ConsultHotelNumberOfClientsBehavior(Agent a) {
			super(a);
			messageTemplate = MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchProtocol(Constants.CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL));
		}
	
		@Override
		public void action() {
			
			switch (step) {
			
			case START:
				NumberOfClientsQueryRef request = new NumberOfClientsQueryRef();
				sendRequestPredicate(hotel, request, Constants.CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL, ACLMessage.QUERY_REF);
				day = getDay();
				step = Step.RECEIVING_RESPONSES;
				break;
				
			case RECEIVING_RESPONSES:

				ACLMessage msg = receive(messageTemplate);
				if (msg == null) {
					// No message arrives
					block();					
				}
				//A New Message
				else {
					log.logReceivedMsg(msg);
					
					switch (msg.getPerformative()) {
					
					case ACLMessage.INFORM:
						boolean resultOK = handleConsultNumberOfClientsInform(msg);
						if (resultOK) {
							step = Step.DONE;
						}
						break;

					case ACLMessage.REFUSE:
						// keep trying (does not change the state)
						break;
					case ACLMessage.NOT_UNDERSTOOD:
						// keep trying (does not change the state)
						break;
					default:
						// keep trying (does not change the state)
						break;
					}

				}
				break;
			
			case DONE:
			default:
				setDone(true);
				break;
			}
		}

		private boolean handleConsultNumberOfClientsInform(ACLMessage message) {
			try {
				NumberOfClients content = (NumberOfClients) getContentManager().extractContent(message);
				if (content != null) {
					Logger.logDebug(myName() + ": Number of clients: " + content.getNum_clients());
					client.addOccupancyForRating(day, content.getNum_clients());
					return true;
				} else {
					Logger.logDebug(myName() + ": Number of clients: Not found (null)");
				}
			} catch (CodecException | OntologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Logger.logDebug(myName() + ": Message: " + message.getContent());
			}
			return false;
		}
	}

	class ConsultHotelStaffBehavior extends	MetaSimpleBehaviour {
		private static final long serialVersionUID = -6581059230472887190L;
		private MessageTemplate messageTemplate;
		private Step step = Step.START;
		private Hotel hotel;
		
		public ConsultHotelStaffBehavior(Agent a) {
			super(a);
			messageTemplate = MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchProtocol(Constants.RATEHOTEL_PROTOCOL));

			hotel = client.getHotelOfBookingDone().getHotel().getConcept();
		}
		
		@Override
		public void action() {
			switch (step) {
			
			case START:
				//FIXME: add predicate!!!!
				ConsultHotelStaff request = new ConsultHotelStaff();
				request.setHotel(hotel);
				sendRequest(hotel.getHotelAgent(), request, Constants.CONSULTHOTELSSTAFF_PROTOCOL, ACLMessage.QUERY_REF);

				step = Step.RECEIVING_RESPONSES;
				break;
				
			case RECEIVING_RESPONSES:

				ACLMessage msg = receive(messageTemplate);
				if (msg == null) {
					// No message arrives
					block();					
				}
				//A New Message
				else {
					log.logReceivedMsg(msg);
					
					switch (msg.getPerformative()) {
					
					case ACLMessage.INFORM:
						//OK
						break;

					case ACLMessage.REFUSE:
						// ignore
						break;
					case ACLMessage.NOT_UNDERSTOOD:
						// ignore
						break;
					default:
						// ignore
						break;
					}
					step = Step.DONE;
				}
				break;
			
			case DONE:
			default:
				setDone(true);
				break;
			}
		}
	}

	
	class RateHotelBehavior extends MetaSimpleBehaviour {
		
		private static final long serialVersionUID = -7663792485820846478L;
		private MessageTemplate messageTemplate;
		private Step step = Step.START;
		private AID agHotelMania;

		public RateHotelBehavior(Agent a) {
			super(a);
			messageTemplate = MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchProtocol(Constants.RATEHOTEL_PROTOCOL));
		}
		
		@Override
		public void action() {
			switch (step) {
			
			case START:
				if (agHotelMania == null) {
					agHotelMania = locateAgent(Constants.RATEHOTEL_ACTION, myAgent);
				} else {
					rateHotel(agHotelMania);
					step = Step.RECEIVING_RESPONSES;
				}
				break;
				
			case RECEIVING_RESPONSES:

				ACLMessage msg = receive(messageTemplate);
				if (msg == null) {
					// No message arrives
					block();					
				}
				//A New Message
				else {
					log.logReceivedMsg(msg);
					
					switch (msg.getPerformative()) {
					
					case ACLMessage.INFORM:
						// ok
						break;

					case ACLMessage.REFUSE:
						// ignore
						break;
					case ACLMessage.NOT_UNDERSTOOD:
						// ignore
						break;
					default:
						// ignore
						break;
					}
					step = Step.DONE;
					}
				break;
			
			case DONE:
			default:
				setDone(true);
				break;
			}
		}

		
		// TODO This part must be dynamic
		private void rateHotel(AID hotelMania) {

			//Calculate average ratings
			
			float cleanliness = 0;
			float kitchen = 0;
			float staff = 0;
			float price = 0;
			HashMap<Integer, RatingInput> data = client.getRatingData();
			for (RatingInput day : data.values()) {
				Logger.logDebug("computing rating for one day ...."+day);
//				cleanliness = day.getStaff()*0;
//				kitchen = day.getStaff()*0;
//				staff = day.getStaff()*0;
//				price = day.getStaff()*0;
			}
			
			//Build message 
			
			RateHotel action_rating = new RateHotel();
			Rating rating = new Rating();
			rating.setChef_rating(kitchen);
			rating.setPrice_rating(price);
			rating.setRoom_staff_rating(staff);
			rating.setCleanliness_rating(cleanliness);

			action_rating.setRatings(rating);
			Hotel hotel = client.getHotelOfBookingDone().getHotel().getConcept();
			action_rating.setHotel(hotel);

			sendRequest(hotelMania, action_rating, Constants.RATEHOTEL_PROTOCOL, ACLMessage.REQUEST);
		}

	}

	class MakeDepositBehavior extends MetaSimpleBehaviour {

		private static final long serialVersionUID = -270503232133476854L;
		private MessageTemplate messageTemplate;
		private Step step = Step.START;
		private AID agBank = null;
		
		public MakeDepositBehavior(Agent a) {
			super(a);
			messageTemplate = MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchProtocol(Constants.MAKEDEPOSIT_PROTOCOL));
		}
		
		@Override
		public void action() {
			
			switch (step) {
			
			case START:
				if (agBank == null) {
					agBank = locateAgent(Constants.MAKEDEPOSIT_ACTION, myAgent);

				} else {
					makeDeposit(agBank);
					step = Step.RECEIVING_RESPONSES;
				}
				break;
				
			case RECEIVING_RESPONSES:

				ACLMessage msg = receive(messageTemplate);
				if (msg == null) {
					// No message arrives
					block();					
				}
				//A New Message
				else {
					log.logReceivedMsg(msg);
					
					switch (msg.getPerformative()) {
					
					case ACLMessage.INFORM:
						// OK
						break;

					case ACLMessage.REFUSE:
						// ignore
						break;
					case ACLMessage.NOT_UNDERSTOOD:
						// ignore
						break;
					default:
						// ignore
						break;
					}
					step = Step.DONE;
				}
				break;
			
			case DONE:
			default:
				this.setDone(true);
				break;
			}
		}

		//TODO finish
		private void makeDeposit(AID bank) {
			MakeDeposit action_deposit = new MakeDeposit();
			action_deposit.setHotel(client.getHotelOfBookingDone().getHotel().getConcept());
			action_deposit.setMoney(client.getBookingDone().getRoomPrice().getPrice());

			sendRequest(bank, action_deposit, Constants.MAKEDEPOSIT_PROTOCOL, ACLMessage.REQUEST);
		}	
	}
	
}

