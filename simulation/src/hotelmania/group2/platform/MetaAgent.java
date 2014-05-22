package hotelmania.group2.platform;

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
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionInitiator;

public abstract class MetaAgent extends Agent {

	private static final long serialVersionUID = 3898945377957867754L;
	
	// Codec for the SL language used
	protected Codec codec = new SLCodec();
	// External communication protocol's ontology
	protected Ontology ontology = SharedAgentsOntology.getInstance();
	
	/**
	 * This value is updated by simulator only if the subscription is TRUE
	 */
	private int day = Constants.FIRST_DAY-1;
	

	@Override
	protected void setup() {
		super.setup();
		
		System.out.println(myName() + ": HAS ENTERED");
		
		addBehaviour(new ReceiveInformMsgBehavior(this));
		addBehaviour(new ReceiveAcceptanceMsgBehavior(this));
		addBehaviour(new ReceiveRefuseMsgBehavior(this));
		addBehaviour(new ReceiveNotUnderstoodMsgBehavior(this));
		addBehaviour(new ReceiveFailureMsgBehavior(this));
	
		getContentManager().registerLanguage(this.codec);
		getContentManager().registerOntology(this.ontology);
		
		if(setRegisterForDayEvents())
		{
			addBehaviour(new LocateSimulatorBehavior(this));
		}
	}
	
	public String myName()
	{
		return super.getLocalName();
	}
 
	/**
	 * Override to set the registration to day events.  
	 * @return
	 */
	protected abstract boolean setRegisterForDayEvents();

	protected void doOnNewDay() {
	}
	
	public int getDay() throws Exception {
		if (day < Constants.FIRST_DAY || !setRegisterForDayEvents()) {
			throw new Exception("Error: The agent tried to query the day without previous subscription to updates.");
		}
		return this.day;
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

		//System.out.println(this.myName() + ": Agent not found:" + type);
		return null;
	}

	public void sendRequest(AID receiver, Concept content, String protocol, int performative) {
		ACLMessage msg = new ACLMessage(performative);
		msg.addReceiver(receiver);
		msg.setLanguage(this.codec.getName());
		msg.setOntology(this.ontology.getName());
		msg.setProtocol(protocol);

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

		System.out.println(myName() + ": REQUESTS " + content.getClass().getSimpleName());
	}
	

	public void sendRequestPredicate(AID receiver, Predicate content, String protocol, int performative) {
		ACLMessage msg = new ACLMessage(performative);
		msg.addReceiver(receiver);
		msg.setLanguage(this.codec.getName());
		msg.setOntology(this.ontology.getName());
		msg.setProtocol(protocol);

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

		System.out.println(myName() + ": REQUESTS " + content.getClass().getSimpleName());
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
			System.out.println(myName() + ": registered in the DF");
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

		System.out.println(this.myName()+": subscription to day event sent!");

		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.addReceiver(agSimulator);
		msg.setProtocol(Constants.SUBSCRIBETODAYEVENT_PROTOCOL);
		msg.setLanguage(this.codec.getName());
		msg.setOntology(this.ontology.getName());

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
	}

	private void addSubscriptionToSimulationEnd(AID agSimulator) {

		if (agSimulator == null) {
			return;
		}

		System.out.println(this.myName()+": subscription to [end simulation] sent!");

		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.addReceiver(agSimulator);
		msg.setProtocol(Constants.END_SIMULATION_PROTOCOL);
		msg.setLanguage(this.codec.getName());
		msg.setOntology(this.ontology.getName());

		EndSimulation actionEndSim = new EndSimulation(); 
		Action action = new Action(agSimulator, actionEndSim);
		try {
			this.getContentManager().fillContent(msg, action);
		} catch (CodecException e) {
			e.printStackTrace();
		} catch (OntologyException e) {
			e.printStackTrace();
		}

		addBehaviour(new EndSimulationSubscriptor(this, msg)); //FIXME  USE THE REAL ONE! 		
	}

	
	private final class DayEventSubscriptor extends SubscriptionInitiator {
		
		private static final long serialVersionUID = 1L;

		private DayEventSubscriptor(Agent a, ACLMessage msg) {
			super(a, msg);
		}

		protected void handleInform(ACLMessage inform) {
			logInformMessage(inform.getProtocol(), inform);
			day++;
			doOnNewDay();
		}

		protected void handleRefuse(ACLMessage refuse) {
			logRefuseMessage(refuse.getProtocol(), refuse);
		}

		protected void handleFailure(ACLMessage failure) {
			/*TODO refine message logs
			if (failure.getSender().equals(myAgent.getAMS())) {
				// FAILURE notification from the JADE runtime: 
				// the receiver does not exist
				System.out.println("Responder does not exist");
			} else {
				System.out.println("failed to perform the requested action");
			}
			*/
			logFailureMessage(failure.getProtocol(), failure);
		}
	}

	private final class EndSimulationSubscriptor extends SubscriptionInitiator {
		
		private static final long serialVersionUID = 1L;

		private EndSimulationSubscriptor(Agent a, ACLMessage msg) {
			super(a, msg);
		}

		protected void handleInform(ACLMessage inform) {
			logInformMessage(inform.getProtocol(), inform);
			MetaAgent.this.doDelete();
		}

		protected void handleRefuse(ACLMessage refuse) {
			logRefuseMessage(refuse.getProtocol(), refuse);
		}

		protected void handleFailure(ACLMessage failure) {
			logFailureMessage(failure.getProtocol(), failure);
		}
	}
	
	private final class ReceiveAcceptanceMsgBehavior extends CyclicBehaviour {
			
			private static final long serialVersionUID = -4878774871721189228L;
	
			private ReceiveAcceptanceMsgBehavior(Agent a) {
				super(a);
			}
	
			public void action() {
				ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
						MessageTemplate.MatchLanguage(codec.getName()),
						MessageTemplate.MatchOntology(ontology.getName())),
						MessageTemplate.MatchPerformative(ACLMessage.AGREE)),
						MessageTemplate.not(MessageTemplate.MatchProtocol(Constants.SUBSCRIBETODAYEVENT_PROTOCOL))));
	
				if (msg != null) {
					logAgreeMessage(msg.getProtocol(), msg);
					receivedAcceptance(msg);
					
				} else {
					// If no message arrives
					block();
				}
	
			}
		}

	private final class ReceiveInformMsgBehavior extends CyclicBehaviour {
	
		private static final long serialVersionUID = -4878774871721189228L;
	
		private ReceiveInformMsgBehavior(Agent a) {
			super(a);
		}
	
		public void action() {
			ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM)),
					MessageTemplate.not(MessageTemplate.MatchProtocol(Constants.SUBSCRIBETODAYEVENT_PROTOCOL))));
	
			if (msg != null) {
				logInformMessage(msg.getProtocol(), msg);
				//TODO NOT NECESSARY ANYMORE: The message is now filtered. But this must be tested better.
//				if (msg.getProtocol().equals(Constants.SUBSCRIBETODAYEVENT_PROTOCOL)) {
//					day++; 
//					doOnNewDay();
//				}
				
				receivedInform(msg);
	
			} else {
				// If no message arrives
				block();
			}
	
		}
	}

	private final class ReceiveRefuseMsgBehavior extends CyclicBehaviour {
		
		private static final long serialVersionUID = 1L;

		private ReceiveRefuseMsgBehavior(Agent a) {
			super(a);
		}

		public void action() {
			ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchPerformative(ACLMessage.REFUSE)),
					MessageTemplate.not(MessageTemplate.MatchProtocol(Constants.SUBSCRIBETODAYEVENT_PROTOCOL))));
					
			if (msg != null) {
				logRefuseMessage(msg.getProtocol(), msg);
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
			ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchPerformative(ACLMessage.NOT_UNDERSTOOD)),
					MessageTemplate.not(MessageTemplate.MatchProtocol(Constants.SUBSCRIBETODAYEVENT_PROTOCOL))));
			
			if (msg != null) {
				logNotUnderstoodMessage(msg.getProtocol(), msg);
				receivedNotUnderstood(msg);
			} else {
				// If no message arrives
				block();
			}

		}
	}
	
	private final class ReceiveFailureMsgBehavior extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;

		private ReceiveFailureMsgBehavior(Agent a) {
			super(a);
		}

		public void action() {
			ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchPerformative(ACLMessage.FAILURE)),
					MessageTemplate.not(MessageTemplate.MatchProtocol(Constants.SUBSCRIBETODAYEVENT_PROTOCOL))));
			
			if (msg != null) {
				logFailureMessage(msg.getProtocol(), msg);
				receivedFailure(msg);
			} else {
				// If no message arrives
				block();
			}

		}
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
				addSubscriptionToDayEventBehavior(agSimulator);
				addSubscriptionToSimulationEnd(agSimulator);
				this.setDone(true);
			}
		}
	}

	/**
	 * Invoked just before the agent dies
	 */
	@Override
	protected void takeDown() {
		super.takeDown();
	}

	//TODO define format for logs
	
	
	public void logInformMessage(String protocol, ACLMessage message) {
		System.out.println(this.myName()+": Received <Inform> for Protocol: "+protocol); 		
	}

	public void logAgreeMessage(String protocol, ACLMessage message) {
		System.out.println(this.myName()+": Received <Agree> for Protocol: "+protocol);		
	}

	public void logRefuseMessage(String protocol, ACLMessage message) {
		String cause = message.getContent()==null? "unknown": message.getContent();
		System.out.println(this.myName()+": Received <Refuse> for Protocol: "+protocol + " - Cause: "+cause);				
	}

	public void logNotUnderstoodMessage(String protocol, ACLMessage message) {
		String cause = message.getContent()==null? "unknown": message.getContent();
		System.out.println(this.myName()+": Received <NotUnderstood> for Protocol: "+protocol + " - Cause: "+cause);
	}
	
	public void logFailureMessage(String protocol, ACLMessage failure) {
		String cause = failure.getContent()==null? "unknown": failure.getContent();
		System.out.println(this.myName()+": Received <Failure> for Protocol: "+protocol + " - Cause: "+cause);
	}

	public abstract void receivedAcceptance(ACLMessage message);

	//TODO change name to refuse!
	public abstract void receivedReject(ACLMessage message);

	public  abstract void receivedNotUnderstood(ACLMessage message);
	
	public abstract void receivedInform(ACLMessage message);
	
	public /*FIXME abstract*/void receivedFailure(ACLMessage msg){}

}
