package hotelmania.group2.platform;

import hotelmania.group2.dao.AccountDAO;
import hotelmania.group2.dao.BookingDAO;
import hotelmania.group2.dao.ClientDAO;
import hotelmania.group2.dao.ContractDAO;
import hotelmania.group2.dao.HotelDAO;
import hotelmania.group2.dao.RateDAO;
import hotelmania.ontology.DayEvent;
import hotelmania.ontology.NotificationDayEvent;
import hotelmania.ontology.SharedAgentsOntology;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.abs.AbsConcept;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public abstract class MetaAgent extends Agent {

	private static final long serialVersionUID = 3898945377957867754L;
	
	// Codec for the SL language used
	protected Codec codec = new SLCodec();
	// External communication protocol's ontology
	protected Ontology ontology = SharedAgentsOntology.getInstance();
	
	//Data access objects
	protected BookingDAO bookDao;
	protected ContractDAO contractDAO;
	protected AccountDAO accountDAO;
	protected ClientDAO clientDAO;
	protected HotelDAO hotelDAO;
	protected RateDAO rateDAO;

	//Attributes
	protected String name;
	protected int today;
	/**
	 * This value is updated by simulator only if the subscription is TRUE
	 */
	protected int day;
	

	@Override
	protected void setup() {
		super.setup();
		System.out.println(getLocalName() + ": HAS ENTERED");
		addBehaviour(new ReceiveAcceptanceMsgBehavior(this));
		addBehaviour(new ReceiveRejectionMsgBehavior(this));
		addBehaviour(new ReceiveNotUnderstoodMsgBehavior(this));
	
		// Get services instances
		bookDao = BookingDAO.getInstance();
		contractDAO = ContractDAO.getInstance();
		accountDAO = AccountDAO.getInstance();
		clientDAO = ClientDAO.getInstance();
		hotelDAO = HotelDAO.getInstance();
		rateDAO = RateDAO.getInstance();
	
		// Register codec and ontology in ContentManager
		getContentManager().registerLanguage(this.codec);
		getContentManager().registerOntology(this.ontology);
		
		if(setRegisterForDayEvents())
		{
			subscribeToDayEvent();
		}
	}
 
	protected abstract boolean setRegisterForDayEvents();


	/**
	 * Subscribe to day change events (topic "newDay").
	 * @author elts
	 */
	private void subscribeToDayEvent() {
		try {
			TopicManagementHelper topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
			final AID topic = topicHelper.createTopic(Constants.NEW_DAY_TOPIC);
			topicHelper.register(topic);

			// Add a behaviour collecting messages about topic "NEW DAY"
			addBehaviour(new CyclicBehaviour(this) 
			{
				private static final long serialVersionUID = -8091307136133265354L;

				public void action() 
				{
					ACLMessage msg = myAgent.receive(MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
							MessageTemplate.MatchLanguage(codec.getName()),
							MessageTemplate.MatchOntology(ontology.getName())),
							MessageTemplate.MatchProtocol(Constants.SUBSCRIBETODAYEVENT_PROTOCOL)),
							MessageTemplate.MatchPerformative(ACLMessage.INFORM)),
							MessageTemplate.MatchTopic(topic)));
					
					if (msg == null) {
						block();
						return;
					}
						
					try {
						//Update DAY variable
						
						ContentElement ce = getContentManager().extractContent(msg);
						MetaAgent.this.day = ((NotificationDayEvent)ce).getDayEvent().getDay();
						
						//LOG
//						System.out.println("Agent "+myAgent.getLocalName()+": "+topic.getLocalName()+" = " + strday );
						
						doOnNewDay();
						
					} catch (Exception e) {
						e.printStackTrace();
						block();
					}
				}
			} );
		}
		catch (Exception e) {
			System.err.println("Agent "+getLocalName()+": ERROR registering to topic of NEW DAY");
			e.printStackTrace();
		}
	}


	public AID locateAgent(String type, Agent myAgent) {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		dfd.addServices(sd);

		try {
			// It finds agents of the required type
			DFAgentDescription[] agents = DFService.search(myAgent, dfd);

			if (agents != null && agents.length > 0) {

				for (DFAgentDescription description : agents) {
					return description.getName(); // only expects 1
					// agent...
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Agent not found:" + type);
		return null;
	}

	protected abstract void doOnNewDay();
	
	//TODO fix the method params!
	public void sendRequest(Agent sender, AID receiver, Concept concept,
			Codec codec, Ontology ontology, String protocol, int messageType) {
		ACLMessage msg = new ACLMessage(messageType);
		msg.addReceiver(receiver);
		msg.setLanguage(codec.getName());
		msg.setOntology(ontology.getName());
		msg.setProtocol(protocol);

		// As it is an action and the encoding language the SL,
		// it must be wrapped into an Action
		Action agAction = new Action(receiver, concept);
		try {
			// The ContentManager transforms the java objects into strings
			sender.getContentManager().fillContent(msg, agAction);
			sender.send(msg);
		} catch (CodecException ce) {
			ce.printStackTrace();
		} catch (OntologyException oe) {
			oe.printStackTrace();
		}

	}
	
	public void registerServices(String...services) {
		DFAgentDescription dfd = new DFAgentDescription();
		for (int i = 0; i < services.length; i++) {
			ServiceDescription registrationService = new ServiceDescription();
			registrationService.setName(this.getName());
			registrationService.setType(services[i]);
			dfd.addServices(registrationService);
		}

		try {	
			// Registers its description in the DF
			DFService.register(this, dfd);
			System.out.println(getLocalName() + ": registered in the DF");
			dfd = null;
			
			//TODO
//			doWait(10000);

		} catch (FIPAException e) {
			// TODO handle
			e.printStackTrace();
		}
	}
	
	private final class ReceiveAcceptanceMsgBehavior extends CyclicBehaviour {
		private static final long serialVersionUID = -4878774871721189228L;

		private ReceiveAcceptanceMsgBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// Waits for acceptance messages
			ACLMessage msg = receive(MessageTemplate
					.MatchPerformative(ACLMessage.AGREE));

			if (msg != null) {
				// If an acceptance arrives...
				String request = "*Request*" ;
				System.out.println(myAgent.getLocalName()
						+ ": received "+request +" acceptance from "
						+ (msg.getSender()).getLocalName());
				receivedAcceptance(msg);
			} else {
				// If no message arrives
				block();
			}

		}
	}

	private final class ReceiveRejectionMsgBehavior extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		private ReceiveRejectionMsgBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// Waits for rejection message
			ACLMessage msg = receive(MessageTemplate
					.MatchPerformative(ACLMessage.REFUSE));

			if (msg != null) {
				// If a rejection arrives...
				System.out.println(myAgent.getLocalName()
						+ ": received work rejection from "
						+ (msg.getSender()).getLocalName());
				receivedReject(msg);
			} else {
				// If no message arrives
				block();
			}

		}
	}

	private final class ReceiveNotUnderstoodMsgBehavior extends CyclicBehaviour {
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
				receivedNotUnderstood(msg);
			} else {
				// If no message arrives
				block();
			}

		}
	}
	
	public abstract void receivedAcceptance(ACLMessage message);
	
	public abstract void receivedReject(ACLMessage message);
	
	public  abstract void receivedNotUnderstood(ACLMessage message);
	
	

}
