package hotelmania.group2.platform;

import hotelmania.group2.dao.HotelDAO;
import hotelmania.group2.dao.RatingDAO;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.HotelsInfoRequest;
import hotelmania.ontology.RateHotel;
import hotelmania.ontology.RegistrationRequest;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.abs.AbsContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgHotelmania extends MetaAgent 
{
	static final long serialVersionUID = -7762674314086577059L;
	private HotelDAO hotelDAO = new HotelDAO();
	private RatingDAO ratingDAO = new RatingDAO();
	
	@Override
	protected void setup() {
		super.setup();

		//Register the services
		this.registerServices(Constants.REGISTRATION_ACTION,
				Constants.RATEHOTEL_ACTION,
				Constants.CONSULTHOTELSINFO_ACTION);

		//add the behaviours
		addBehaviour(new ReceiveRegisterRequestBehavior(this));
		addBehaviour(new ProvideHotelInfoBehavior(this));
		//TODO addBehaviour(new ReceiveHotelRatingBehavior(this));
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

						System.out.println(myAgent.getLocalName()
								+ ": answer sent -> " + this.log);
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
			System.out.println(myAgent.getLocalName()
					+ ": received Registration Request from "
					+ (msg.getSender()).getLocalName());
			
			ACLMessage reply = msg.createReply();
			
			if (registrationRequestData != null
					&& registrationRequestData.getHotel() != null) {
				if (registerNewHotel(registrationRequestData.getHotel())) {
					this.log = Constants.AGREE;
					reply.setPerformative(ACLMessage.AGREE);
				} else {
					this.log = Constants.REFUSE;
					reply.setPerformative(ACLMessage.REFUSE);
				}
			} else {
				this.log = Constants.NOT_UNDERSTOOD;
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			}
			return reply;
		}

		private boolean registerNewHotel(Hotel hotel) {
			return hotelDAO .registerNewHotel(hotel.getHotel_name());
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
			 //The ContentManager transforms the message content (string) in

			try {
				ContentElement ce = getContentManager().extractContent(msg);

				// We expect an action inside the message
				if (ce instanceof Action) {
					Action agAction = (Action) ce;
					Concept conc = agAction.getAction();

					// If the action is Registration Request...
					if (conc instanceof HotelsInfoRequest) {
						// execute request
						ACLMessage reply = answerHotelsInfoRequest(msg);
						// send reply
						myAgent.send(reply);

						System.out.println(myAgent.getLocalName()
								+ ": answer sent -> " + this.log);
					}
				}

			} catch (CodecException | OntologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		private ACLMessage answerHotelsInfoRequest(ACLMessage msg) {
			
			System.out.println(myAgent.getLocalName() + ": received HotelsInfo Request from " + (msg.getSender()).getLocalName());
			ACLMessage reply = msg.createReply();
			
			if (hotelDAO.getListHotel() != null) {
				this.log = Constants.AGREE;
				reply.setPerformative(ACLMessage.AGREE);
				AbsContentElement hotels = getAllHotels();
				// The ContentManager transforms the java objects into strings
				try {
					myAgent.getContentManager().fillContent(msg, hotels);
				} catch (CodecException | OntologyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				this.log = Constants.REFUSE;
				reply.setPerformative(ACLMessage.REFUSE); 
			}
			return reply;
		}

		private AbsContentElement getAllHotels() {
			// TODO Auto-generated method stub
			return null;
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

						System.out.println(myAgent.getLocalName()
								+ ": answer sent -> " + this.log);
					}
				}

			} catch (CodecException | OntologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		private ACLMessage rateHotel(ACLMessage msg, RateHotel ratingData) {
		
			System.out.println(myAgent.getLocalName()
					+ ": received Rating Request from "
					+ (msg.getSender()).getLocalName());

			ACLMessage reply = msg.createReply();

			if (ratingData != null && ratingData.getHotel() != null) {
				if (registerNewRating(ratingData)) {
					this.log = Constants.AGREE;
					reply.setPerformative(ACLMessage.AGREE);
				} else {
					this.log = Constants.REFUSE;
					reply.setPerformative(ACLMessage.REFUSE);
				}
			} else {
				this.log = Constants.NOT_UNDERSTOOD;
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			}
			return reply;
		}
		

		/**
		 * Register a new rating and update the ratings for each hotel.
		 * @param ratingData
		 * @return
		 */
		private boolean registerNewRating(RateHotel ratingData) {
			return ratingDAO .registerNewRating(ratingData.getHotel()
					.getHotel_name(), ratingData.getRatings()
					.getCleanliness_rating(), ratingData.getRatings()
					.getCookers_rating(), ratingData.getRatings()
					.getPrice_rating(), ratingData.getRatings()
					.getRoom_staff_rating()); //TODO use a method to convert the object...
		}
	}
	

	@Override
	public void receivedAcceptance(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receivedReject(ACLMessage message) {
		if (message.getProtocol().equals(Constants.REGISTRATION_PROTOCOL)) {
			logRejectedMessage(Constants.REGISTRATION_PROTOCOL, message);
		} else if (message.getProtocol().equals(Constants.RATEHOTEL_PROTOCOL)) {
			logRejectedMessage(Constants.RATEHOTEL_PROTOCOL, message);
		} else if (message.getProtocol().equals(Constants.CONSULTHOTELSINFO_PROTOCOL)) {
			logRejectedMessage(Constants.CONSULTHOTELSINFO_PROTOCOL, message);
		}
	}

	@Override
	public void receivedNotUnderstood(ACLMessage message) {
		if (message.getProtocol().equals(Constants.REGISTRATION_PROTOCOL)) {
			logNotUnderstoodMessage(Constants.REGISTRATION_ACTION, message);
		} else if (message.getProtocol().equals(Constants.RATEHOTEL_PROTOCOL)) {
			logNotUnderstoodMessage(Constants.RATEHOTEL_PROTOCOL, message);
		} else if (message.getProtocol().equals(Constants.CONSULTHOTELSINFO_PROTOCOL)) {
			logNotUnderstoodMessage(Constants.CONSULTHOTELSINFO_PROTOCOL, message);
		}
	}
}
