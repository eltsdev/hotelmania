package hotelmania.group2.platform;

import hotelmania.group2.dao.HotelDAO;
import hotelmania.group2.dao.RateDAO;
import hotelmania.ontology.Hotel;
import hotelmania.ontology.HotelsInfoRequest;
import hotelmania.ontology.RateHotel;
import hotelmania.ontology.RegistrationRequest;
import hotelmania.ontology.SharedAgentsOntology;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgHotelmania extends MetaAgent {
	static final long serialVersionUID = -7762674314086577059L;
	private HotelDAO hotelDAO;
	private RateDAO rateDAO;

	@Override
	protected void setup() {
		super.setup();
		hotelDAO = new HotelDAO();
		rateDAO = new RateDAO();

		//Register the services
		String[] services = {Constants.REGISTRATION_ACTION,
				Constants.RATEHOTEL_ACTION,
				Constants.CONSULTHOTELSINFO_ACTION};
		this.registerServices(services);

		//add the behaviours
		addBehaviour(new ReceiveRegisterRequestBehavior(this));
		addBehaviour(new ReceiveNotUnderstoodMsgBehavior(this));
		addBehaviour(new ProvideHotelInfoBehavior(this));

		// TODO addBehaviour(new ReceiveHotelRatingBehavior(this));
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

		private static final int VALID_REQ = 0;
		private static final int REJECT_REQ = -1;
		private static final int NOT_UNDERSTOOD_REQ = 1;

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
						int answer = answerRegistrationRequest(msg,
								(RegistrationRequest) conc);

						// send reply
						ACLMessage reply = msg.createReply();
						reply.setPerformative(answer);
						//reply = this.treatReply(reply, this.log, answer);

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
		private int answerRegistrationRequest(ACLMessage msg,
				RegistrationRequest registrationRequestData) {
			System.out.println(myAgent.getLocalName()
					+ ": received Registration Request from "
					+ (msg.getSender()).getLocalName());

			if (registrationRequestData != null
					&& registrationRequestData.getHotel() != null) {
				if (registerNewHotel(registrationRequestData.getHotel())) {
					return ACLMessage.AGREE;
				} else {
					return ACLMessage.REFUSE;
				}
			} else {
				return ACLMessage.NOT_UNDERSTOOD;

			}
		}

		private boolean registerNewHotel(Hotel hotel) {
			return hotelDAO.registerNewHotel(hotel.getHotel_name());
		}
	}

	private final class ReceiveNotUnderstoodMsgBehavior extends MetaCyclicBehaviour {
		private static final long serialVersionUID = 1L;

		private ReceiveNotUnderstoodMsgBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// Waits for estimations not understood
			ACLMessage msg = receive(MessageTemplate
					.MatchPerformative(ACLMessage.NOT_UNDERSTOOD));
			if (msg != null) {
				// If a not understood message arrives...
				System.out.println(myAgent.getLocalName()
						+ ": received NOT_UNDERSTOOD from "
						+ (msg.getSender()).getLocalName());
			} else {
				// If no message arrives
				block();
			}

		}
	}

	private final class ProvideHotelInfoBehavior extends MetaCyclicBehaviour {
		private static final long serialVersionUID = 2449653047078980935L;

		public ProvideHotelInfoBehavior(AgHotelmania agHotelmania) {
			super(agHotelmania);
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
						int answer = answerHotelsInfoRequest(msg,
								(HotelsInfoRequest) conc);

						// send reply
						ACLMessage reply = msg.createReply();
						reply.setPerformative(answer); 
						//reply = this.treatReply(reply, this.log, answer);

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
		
		private int answerHotelsInfoRequest(ACLMessage msg,
				HotelsInfoRequest registrationRequestData) {
			System.out.println(myAgent.getLocalName()
					+ ": received HotelsInfo Request from "
					+ (msg.getSender()).getLocalName());
			if (hotelDAO.getListHotel() != null) {
				return ACLMessage.AGREE;
			} else {
				return ACLMessage.REFUSE;
			}
			/*

			if (registrationRequestData != null
					&& registrationRequestData.getHotel() != null) {
				if (registerNewHotel(registrationRequestData.getHotel())) {
					return VALID_REQ;
				} else {
					return REJECT_REQ;
				}
			} else {
				return NOT_UNDERSTOOD_REQ;

			}*/
		}
	}

	/**
	 * @author user
	 *
	 */
	private final class UpdateHotelRatingBehavior extends MetaCyclicBehaviour {
		private static final long serialVersionUID = 7586132058023771627L;

		private static final int VALID_REQ = 0;
		private static final int REJECT_REQ = -1;
		private static final int NOT_UNDERSTOOD_REQ = 1;

		/**
		 * @param agHotelmania
		 */
		public UpdateHotelRatingBehavior(AgHotelmania agHotelmania) {
			super(agHotelmania);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see jade.core.behaviours.Behaviour#action()
		 */
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

					// If the action is Rate Hotel...
					if (conc instanceof RateHotel) {
						// execute request
						int answer = rateHotel(msg, (RateHotel) conc);

						// send reply
						ACLMessage reply = msg.createReply();
						reply.setPerformative(answer);
						//String log = "";
						//reply= this.treatReply(reply, this.log, answer);

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
		 * @param msg
		 * @param conc
		 * @return
		 */
		private int rateHotel(ACLMessage msg, RateHotel ratingData) {
			System.out.println(myAgent.getLocalName()
					+ ": received Rating Request from "
					+ (msg.getSender()).getLocalName());

			if (ratingData != null && ratingData.getHotel() != null) {
				if (registerNewRating(ratingData)) {
					return ACLMessage.AGREE;
				} else {
					return ACLMessage.REFUSE;
				}
			} else {
				return ACLMessage.NOT_UNDERSTOOD;

			}
		}

		/**
		 * Register a new rating and update the ratings for each hotel.
		 * @param ratingData
		 * @return
		 */
		private boolean registerNewRating(RateHotel ratingData) {
			return rateDAO.registerNewRating(ratingData.getHotel()
					.getHotel_name(), ratingData.getRatings()
					.getCleanliness_rating(), ratingData.getRatings()
					.getCookers_rating(), ratingData.getRatings()
					.getPrice_rating(), ratingData.getRatings()
					.getRoom_staff_rating());
		}
	}
}
