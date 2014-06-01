package hotelmania.group2.platform;

import hotelmania.group2.platform.AgentState.State;
import hotelmania.ontology.EndSimulation;
import hotelmania.ontology.SharedAgentsOntology;
import hotelmania.ontology.SubscribeToDayEvent;
import jade.content.Concept;
import jade.content.Predicate;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionInitiator;

public abstract class AbstractAgent extends Agent implements IMyName {
	private static final long serialVersionUID = -5526226222527748058L;

	protected Logger log = new Logger(this);

	// Codec for the SL language used
	protected Codec codec = new SLCodec();

	// External communication protocol's ontology
	protected Ontology ontology = SharedAgentsOntology.getInstance();

	/**
	 * Message template used for all kind of agents (except for subscriptions)
	 */
	public final MessageTemplate BASIC_TEMPLATE = MessageTemplate.and(
			MessageTemplate.MatchLanguage(codec.getName()),
			MessageTemplate.MatchOntology(ontology.getName()));

	/**
	 * State manager to track state changes 
	 */
	protected AgentState state;

	@Override
	protected void setup() {
		super.setup();

		Logger.logDebug(myName() + ": HAS ENTERED");
		state = new AgentState(false, myName());
		state.check(State.LOADED);

		getContentManager().registerLanguage(this.codec);
		getContentManager().registerOntology(this.ontology);

		//Behaviors
		addBehaviour(new LocateSimulatorBehavior(this));

	}

	public String myName()
	{
		return super.getLocalName();
	}

	final class LocateSimulatorBehavior extends MetaSimpleBehaviour {

		private static final long serialVersionUID = 9034688687283651380L;
		private AID agSimulator;

		public LocateSimulatorBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			if (agSimulator == null) {
				agSimulator = locateAgent(Constants.SUBSCRIBETODAYEVENT_ACTION, myAgent);
			}else {
				if(setRegisterForDayEvents())
				{
					addSubscriptionToDayEventBehavior(agSimulator);
				}

				if (setRegisterForEndSimulation()) {
					addSubscriptionToEndSimulation(agSimulator);
				}
				this.setDone(true);
			}
		}
	}

	/**
	 * Override to set the registration to day events.  
	 * @return
	 */
	protected abstract boolean setRegisterForDayEvents();

	protected boolean setRegisterForEndSimulation()
	{
		return true;
	}

	protected void doOnNewDay() {
		state.check(State.RECEIVED_DAY_NOTIFICATION);
	}

	public int getDay() {
		return Constants.DAY;
	}

	public AID locateAgent(String type, Agent myAgent) {

		DFAgentDescription directoryFDesc = new DFAgentDescription();
		ServiceDescription service = new ServiceDescription();
		service.setType(type);
		directoryFDesc.addServices(service);

		try {
			// It finds agents of the required type
			DFAgentDescription[] agents = DFService.search(myAgent, directoryFDesc);

			if (agents != null && agents.length > 0) {

				for (DFAgentDescription description : agents) {
					// only expects 1 agent...
					return description.getName(); 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Logger.logDebug(this.myName() + ": Agent not found:" + type);
		return null;
	}

	public void sendRequest(AID receiver, Concept content, String protocol, int performative) {
		ACLMessage msg = new ACLMessage(performative);
		msg.addReceiver(receiver);
		msg.setLanguage(this.codec.getName());
		msg.setOntology(this.ontology.getName());
		msg.setProtocol(protocol);
		msg.setReplyWith(ConversationID.newCID());

		// As it is an action and the encoding language the SL,
		// it must be wrapped into an Action
		Action agAction = new Action(receiver, content);
		try {
			// The ContentManager transforms the java objects into strings
			this.getContentManager().fillContent(msg, agAction);
			this.send(msg);
		} catch (CodecException ce) {
			ce.printStackTrace();
		} catch (OntologyException oe) {
			oe.printStackTrace();
		}

		log.logSendRequest(msg);
	}


	public void sendRequestPredicate(AID receiver, Predicate content, String protocol, int performative) {
		ACLMessage msg = new ACLMessage(performative);
		msg.addReceiver(receiver);
		msg.setLanguage(this.codec.getName());
		msg.setOntology(this.ontology.getName());
		msg.setProtocol(protocol);
		msg.setReplyWith(ConversationID.newCID());

		// As it is an action and the encoding language the SL,
		// it must be wrapped into an Action

		try {
			// The ContentManager transforms the java objects into strings
			this.getContentManager().fillContent(msg, content);
			this.send(msg);
		} catch (CodecException ce) {
			ce.printStackTrace();
		} catch (OntologyException oe) {
			oe.printStackTrace();
		}
		log.logSendRequest(msg);
	}

	public void sendRequestEmpty(AID receiver, String protocol, int performative) {
		ACLMessage msg = new ACLMessage(performative);
		msg.addReceiver(receiver);
		msg.setLanguage(this.codec.getName());
		msg.setOntology(this.ontology.getName());
		msg.setProtocol(protocol);
		msg.setReplyWith(ConversationID.newCID());

		this.send(msg);
		log.logSendRequest(msg);
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
			Logger.logDebug(myName() + ": registered in the DF");
			dfd = null;
		} catch (FIPAException e) {
			// TODO handle
			e.printStackTrace();
		}
	}

	private void addSubscriptionToDayEventBehavior(AID agSimulator) {

		if (agSimulator == null) {
			return;
		}

		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.addReceiver(agSimulator);
		msg.setProtocol(Constants.SUBSCRIBETODAYEVENT_PROTOCOL);
		msg.setLanguage(this.codec.getName());
		msg.setOntology(this.ontology.getName());
		msg.setReplyWith(ConversationID.newCID());

		SubscribeToDayEvent subscribeToDayEvent = new SubscribeToDayEvent();
		Action action = new Action(agSimulator, subscribeToDayEvent);
		try {
			this.getContentManager().fillContent(msg, action);
		} catch (CodecException e) {
			e.printStackTrace();
		} catch (OntologyException e) {
			e.printStackTrace();
		}

		addBehaviour(new DayEventSubscriptor(this, msg));
		log.logSendRequest(msg);
	}

	private void addSubscriptionToEndSimulation(AID agSimulator) {

		if (agSimulator == null) {
			return;
		}

		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.addReceiver(agSimulator);
		msg.setProtocol(Constants.END_SIMULATION_PROTOCOL);
		msg.setLanguage(this.codec.getName());
		msg.setOntology(this.ontology.getName());
		msg.setReplyWith(ConversationID.newCID());

		EndSimulation actionEndSim = new EndSimulation(); 
		Action action = new Action(agSimulator, actionEndSim);
		try {
			this.getContentManager().fillContent(msg, action);
		} catch (CodecException e) {
			e.printStackTrace();
		} catch (OntologyException e) {
			e.printStackTrace();
		}

		addBehaviour(new EndSimulationSubscriptor(this, msg));
		log.logSendRequest(msg);
	}


	private final class DayEventSubscriptor extends SubscriptionInitiator {

		private static final long serialVersionUID = 1L;

		private DayEventSubscriptor(Agent a, ACLMessage msg) {
			super(a, msg);
		}

		@Override
		protected void handleAgree(ACLMessage agree) {
			log.logAgreeMessage(agree);
			state.check(State.DAYEVENT_SUBSCRIBED);
		}

		protected void handleInform(ACLMessage inform) {
			log.logInformMessage(inform);
			doOnNewDay();
		}

		protected void handleRefuse(ACLMessage refuse) {
			log.logRefuseMessage(refuse);
			state.uncheck(State.DAYEVENT_SUBSCRIBED);
		}

		protected void handleFailure(ACLMessage failure) {
			/*TODO refine message logs
				if (failure.getSender().equals(myAgent.getAMS())) {
					// FAILURE notification from the JADE runtime: 
					// the receiver does not exist
					Logger.logDebug("Responder does not exist");
				} else {
					Logger.logDebug("failed to perform the requested action");
				}
			 */
			log.logFailureMessage(failure);
			state.uncheck(State.DAYEVENT_SUBSCRIBED);
		}
	}

	private final class EndSimulationSubscriptor extends SubscriptionInitiator {

		private static final long serialVersionUID = 1L;

		private EndSimulationSubscriptor(Agent a, ACLMessage msg) {
			super(a, msg);
		}

		@Override
		protected void handleAgree(ACLMessage agree) {
			state.check(State.ENDSIMULATION_SUBSCRIBED);
			log.logAgreeMessage(agree);
			super.handleAgree(agree);
		}

		protected void handleInform(ACLMessage inform) {
			state.check(State.RECEIVED_ENDSIMULATION_NOTIFICATION);
			log.logInformMessage(inform);
			boolean die = doBeforeDie();
			if (die) {
				myAgent.doDelete();
				Logger.logDebug(myName()+": DIED");
			}else {
				Logger.logDebug(myName()+": RESISTED TO DIE");
			}
		}

		protected void handleRefuse(ACLMessage refuse) {
			state.uncheck(State.ENDSIMULATION_SUBSCRIBED);
			log.logRefuseMessage(refuse);
		}

		protected void handleFailure(ACLMessage failure) {
			state.uncheck(State.ENDSIMULATION_SUBSCRIBED);
			log.logFailureMessage(failure);
		}
	}

	/**
	 * Invoked just before the agent dies
	 * @return die true if this agent shall die immediately. False if the agent needs to keep alive.
	 */
	public boolean doBeforeDie() {
		return true;
	}
}