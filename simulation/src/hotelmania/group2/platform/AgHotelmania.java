package hotelmania.group2.platform;

import hotelmania.group2.dao.HotelDAO;
import hotelmania.group2.dao.Rating;
import hotelmania.group2.dao.RatingDAO;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.RateHotel;
import hotelmania.ontology.RegistrationRequest;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.ContentElementList;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;

public class AgHotelmania extends MetaAgent 
{
	static final long serialVersionUID = -7762674314086577059L;
	private HotelDAO hotelDAO = new HotelDAO();
	private RatingDAO ratingDAO = new RatingDAO();
	
	@Override
	protected void setup() {
		super.setup();

		//Register the services
		this.registerServices(Constants.REGISTRATION_ACTION, Constants.RATEHOTEL_ACTION, Constants.CONSULTHOTELSINFO_ACTION);

		//add the behaviours
		addBehaviour(new ReceiveRegisterRequestBehavior(this));
		addBehaviour(new ProvideHotelInfoBehavior(this));
		addBehaviour(new UpdateHotelRatingBehavior(this));
	}
	
	/**
	 * This means: I am not interested on this event.
	 */
	@Override
	protected boolean setRegisterForDayEvents() {
		return false;
	}

	// --------------------------------------------------------
	// BEHAVIOURS CLASSES
	// --------------------------------------------------------

	/**
	 * Adds a behavior to answer REGISTER requests Waits for a request and, when
	 * it arrives, answers with the ACCEPT/REJECT response and waits again.
	 * 
	 * @author elts
	 */
	private final class ReceiveRegisterRequestBehavior extends MetaCyclicBehaviour {
		private static final long serialVersionUID = 8713963422079295068L;

		public ReceiveRegisterRequestBehavior(Agent a) {
			super(a);
		}

		public void action() {
			/*
			 * Look for messages
			 */
			ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchProtocol(Constants.REGISTRATION_PROTOCOL)),
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

					// If the action is Registration Request...
					if (conc instanceof RegistrationRequest) {
						// execute request

						// send reply
						ACLMessage reply = answerRegistrationRequest(msg, (RegistrationRequest) conc);

						myAgent.send(reply);
						log.logSendReply(reply);
					}
				}

			} catch (CodecException | OntologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**
		 * Creates the response to registration request
		 * 
		 * @param msg
		 * @param registrationRequestData
		 * @return Answer type
		 */
		private ACLMessage answerRegistrationRequest(ACLMessage msg, RegistrationRequest registrationRequestData) {
			ACLMessage reply = msg.createReply();
			
			if (registrationRequestData != null
					&& registrationRequestData.getHotel() != null) {
				
				boolean registrationSuccessful = registerNewHotel(registrationRequestData.getHotel(), msg.getSender());
				
				if (registrationSuccessful) {
					reply.setPerformative(ACLMessage.AGREE);
				} else {
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("Registration unsuccesful. The hotel name perhaps already existed in hotelmania.");
				}
			} else {
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				reply.setContent("Registration request is null or the hotel property is null.");
			}
			return reply;
		}

		private boolean registerNewHotel(Hotel hotel, AID sender) {
			
			return hotelDAO .registerNewHotel(hotel.getHotel_name(), sender );
		}
	}

	private final class ProvideHotelInfoBehavior extends MetaCyclicBehaviour {
		private static final long serialVersionUID = 2449653047078980935L;

		public ProvideHotelInfoBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchProtocol(Constants.CONSULTHOTELSINFO_PROTOCOL)),
					MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF)));

			// If no message arrives
			if (msg == null) {
				block();
				return;
			}
			
			log.logReceivedMsg(msg);
			
			// execute request
			ContentElementList hotels = getAllHotels();
			
			// Send reply
			sendResponseOfHotelRecords(msg, hotels);
		}

		private void sendResponseOfHotelRecords(ACLMessage msg, ContentElementList hotels) {
			ACLMessage reply = msg.createReply();
			
			if (hotels != null && !hotels.isEmpty()) {
				reply.setPerformative(ACLMessage.INFORM);
				// The ContentManager transforms the java objects into strings
				try {
					myAgent.getContentManager().fillContent(reply, hotels);
				} catch (CodecException | OntologyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
				
			} else {
				reply.setPerformative(ACLMessage.REFUSE);
				reply.setContent("No hotels registered yet.");
			}
			//..there is no option of NOT UNDERSTOOD
			
			//Send
			myAgent.send(reply);
			log.logSendReply(reply);
		}

		private ContentElementList getAllHotels() {
			ArrayList<hotelmania.group2.dao.HotelInformation> list = hotelDAO.getHotelsRegistered();
			return toContentElementList(list); 
		}
	}

	private final class UpdateHotelRatingBehavior extends MetaCyclicBehaviour {

		private static final long serialVersionUID = 7586132058023771627L;

		
		public UpdateHotelRatingBehavior(Agent a) {
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
					MessageTemplate.MatchProtocol(Constants.RATEHOTEL_PROTOCOL)),
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
			 * The ContentManager transforms the message content (string) in a java object
			 */
			try {
				ContentElement ce = getContentManager().extractContent(msg);

				// We expect an action inside the message
				if (ce instanceof Action) {
					Action agAction = (Action) ce;
					Concept conc = agAction.getAction();

					// If the action is Rate Hotel...
					if (conc instanceof RateHotel) {
						// execute request
						ACLMessage reply = rateHotel(msg, (RateHotel) conc);
						// send reply
						myAgent.send(reply);
						log.logSendReply(reply);
					}
				}

			} catch (CodecException | OntologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		private ACLMessage rateHotel(ACLMessage msg, RateHotel ratingData) {
			ACLMessage reply = msg.createReply();

			if (ratingData != null && ratingData.getHotel() != null) {
				if (registerNewRating(ratingData)) {
					reply.setPerformative(ACLMessage.AGREE);
				} else {
					reply.setPerformative(ACLMessage.REFUSE);
					//TODO reply.setContent("");
				}
			} else {
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				//TODO 	reply.setContent("");
			}
			return reply;
		}
	
	}
	

	@Override
	public void receivedAcceptance(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}

	public ContentElementList toContentElementList(
			ArrayList<hotelmania.group2.dao.HotelInformation> list) {
		
		ContentElementList contentList = new ContentElementList();
		
		for (hotelmania.group2.dao.HotelInformation record : list) {
			contentList.add( (ContentElement) record.getConcept());
		}

		return contentList;
	}
	

	/**
	 * Register a new rating and update the ratings for each hotel.
	 * @param ratingData
	 * @return
	 */
	private boolean registerNewRating(RateHotel ratingData) {
		String hotelName = ratingData.getHotel().getHotel_name();
		
		ratingDAO.addRating(hotelName, 
				ratingData.getRatings().getCleanliness_rating(), 
				ratingData.getRatings().getChef_rating(), 
				ratingData.getRatings().getPrice_rating(), 
				ratingData.getRatings().getRoom_staff_rating()); 
		
		ArrayList<Rating> historicalHotelRatings = ratingDAO.getRatingsOfHotel(hotelName);
		float finalRating = computeAverageRating(historicalHotelRatings);
		hotelDAO.updateRating(hotelName, finalRating);
		return true;
	}
	

	private float computeAverageRating(ArrayList<Rating> ratings) {
		float cleanliness = 0, chefs = 0, price = 0, roomStaff = 0;
		for (Rating rating : ratings) {
			cleanliness += rating.getCleanliness_rating();
			chefs += rating.getChef_rating();
			price+= rating.getPrice_rating();
			roomStaff += rating.getRoom_staff_rating();
		}
		
		cleanliness /= ratings.size();
		chefs /= ratings.size();
		price /= ratings.size();
		roomStaff /= ratings.size();
		
		float total = price*0.4f + cleanliness*0.2f + chefs*0.2f + roomStaff*0.2f;
		return total;
	}

	@Override
	public void receivedReject(ACLMessage message) {
		if (message.getProtocol().equals(Constants.REGISTRATION_PROTOCOL)) {

		} else if (message.getProtocol().equals(Constants.RATEHOTEL_PROTOCOL)) {
		
		} else if (message.getProtocol().equals(Constants.CONSULTHOTELSINFO_PROTOCOL)) {


		}
	}

	@Override
	public void receivedNotUnderstood(ACLMessage message) {
		if (message.getProtocol().equals(Constants.REGISTRATION_PROTOCOL)) {

		} else if (message.getProtocol().equals(Constants.RATEHOTEL_PROTOCOL)) {

		} else if (message.getProtocol().equals(Constants.CONSULTHOTELSINFO_PROTOCOL)) {

		}
	}

	@Override
	public void receivedInform(ACLMessage message) {
		
	}
	
	@Override
	public boolean doBeforeDie() {
		return false;
	}
}
