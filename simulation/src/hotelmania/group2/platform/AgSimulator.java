package hotelmania.group2.platform;

import hotelmania.ontology.SharedAgentsOntology;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class AgSimulator extends Agent
{
	private static final long serialVersionUID = -4208905954219155107L;

	// Codec for the SL language used
	private Codec codec = new SLCodec();

	// External communication protocol's ontology
	private Ontology ontology = SharedAgentsOntology.getInstance();

	//------------------------------------------------- 
	// Agent Attributes
	//-------------------------------------------------

	private long dayLenght;

	//------------------------------------------------- 
	// Setup
	//-------------------------------------------------
	
	@Override
	protected void setup() 
	{
		System.out.println(getLocalName() + ": HAS ENTERED");

		// Register codec and ontology in ContentManager
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);

		// Behaviors
		/*		
		addBehaviour(new SetTimeSpeedBehavior(this));
		final String NEW_DAY_TOPIC = "newDay";

		final AID topic;
		

		try {
			// Periodically send messages about topic "JADE"
			TopicManagementHelper topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
			topic = topicHelper.createTopic(NEW_DAY_TOPIC);
			addBehaviour(new TickerBehaviour(this, 10000) {
				public void onTick() {
					System.out.println("Agent "+myAgent.getLocalName()+": Sending message about topic "+topic.getLocalName());
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.addReceiver(topic);
					msg.setContent(String.valueOf(getTickCount()));
					myAgent.send(msg);
				}
			} );
		}
		catch (Exception e) {
			System.err.println("Agent "+getLocalName()+": ERROR creating topic \"JADE\"");
			e.printStackTrace();
		}
		
		MessageTemplate messageTemplate = null; //FIXME
		addBehaviour(new ControlDaysBehavior(this, messageTemplate));
 */		
		addBehaviour(new GeneratePlatformAgentsBehavior(this));
//		addBehaviour(new GenerateClientsBehavior(this));
	}
		
	// --------------------------------------------------------
	// Behaviors
	// --------------------------------------------------------
	
	private final class SetTimeSpeedBehavior extends CyclicBehaviour 
	{
		private static final long serialVersionUID = -9078033789982364795L;

		private SetTimeSpeedBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// If no message arrives
			block();
		}
	}
	
	private final class ControlDaysBehavior extends SubscriptionResponder
	{
		private static final long serialVersionUID = -8919973424010462213L;

		public ControlDaysBehavior(Agent a, MessageTemplate mt) {
			super(a, mt);
			// TODO Auto-generated constructor stub
		}

		
	}

	private final class GeneratePlatformAgentsBehavior extends SimpleBehaviour 
	{
		private static final long serialVersionUID = -9078033789982364797L;
		
		boolean done;

		private GeneratePlatformAgentsBehavior(Agent a) {
			super(a);
		}

		public void action() {

			//TODO Complete
			ContainerController cc = getContainerController();
			AgentController ac;
			try {
				ac = cc.createNewAgent("hotelmania", "hotelmania.group2.platform.AgHotelmania", null);
				ac.start();

			} catch (StaleProxyException e) {
				e.printStackTrace();
//				done = false;
//				block();
			}
			done = true;
		}

		@Override
		public boolean done() {
			return done;
		}
	}

	private final class GenerateClientsBehavior extends CyclicBehaviour 
	{
		private static final long serialVersionUID = 7520884931937975601L;

		private GenerateClientsBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// If no message arrives
			block();
		}
	}

}
