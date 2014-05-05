package hotelmania.group2.platform;

import hotelmania.ontology.DayEvent;
import hotelmania.ontology.NotificationDayEvent;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class AgPlatform2 extends MetaAgent
{
	private static final long serialVersionUID = -4208905954219155107L;

	//------------------------------------------------- 
	// Setup
	//-------------------------------------------------
	
	@Override
	protected void setup() 
	{
		super.setup();
		
		// Behaviors
//		addBehaviour(new SetTimeSpeedBehavior(this));
		createDayEventsBehavior();
		addBehaviour(new GeneratePlatformAgentsBehavior(this));
		//addBehaviour(new GenerateClientsBehavior(this));
	}
	
		
	/**
	 * Periodically send messages about topic "NEW_DAY"
	 */
	private void createDayEventsBehavior() {
		try {
			TopicManagementHelper topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
			
			final AID topic = topicHelper.createTopic(Constants.NEW_DAY_TOPIC);
			
			addBehaviour(new TickerBehaviour(this, Constants.DAY_IN_SECONDS) 
			{
				private static final long serialVersionUID = 6616055369402031517L;

				public void onTick() 
				{
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.addReceiver(topic);
					msg.setLanguage(codec.getName());
					msg.setOntology(ontology.getName());
					msg.setProtocol(Constants.SUBSCRIBETODAYEVENT_PROTOCOL);
					//Day number
					int day = getTickCount();
					NotificationDayEvent notificationDayEvent = new NotificationDayEvent();
					DayEvent dayEvent = new DayEvent();
					dayEvent.setDay(day);
					notificationDayEvent.setDayEvent(dayEvent);
					
					try {
						getContentManager().fillContent(msg, notificationDayEvent);
						myAgent.send(msg);
						System.out.println("Agent "+myAgent.getLocalName()+": " + topic.getLocalName() +" = "+day);
					} catch (CodecException | OntologyException e) {
						e.printStackTrace();
					}
				}
			} );
		}
		catch (Exception e) {
			System.err.println("Agent "+getLocalName()+": ERROR creating topic \""+Constants.NEW_DAY_TOPIC+"\"");
			e.printStackTrace();
		}
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
	
	private final class GeneratePlatformAgentsBehavior extends SimpleBehaviour 
	{
		private static final long serialVersionUID = -9078033789982364797L;
		
		boolean done;

		private GeneratePlatformAgentsBehavior(Agent a) {
			super(a);
		}

		public void action() {
			//TODO Complete
			try {
				ContainerController cc = getContainerController();
				AgentController ac = null;
				
				ac = cc.createNewAgent("reporter", "hotelmania.group2.platform.AgReporter", null);
				ac.start();
				
				ac = cc.createNewAgent("hotelmania", "hotelmania.group2.platform.AgHotelmania", null);
				ac.start();
				
				ac = cc.createNewAgent("agency", "hotelmania.group2.platform.AgAgency", null);
				ac.start();

//				ac = cc.createNewAgent("bank", "hotelmania.group2.platform.AgBank", null);
//				ac.start();
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

	@Deprecated
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
	
	//-----------------------------------------------------------------------
	// Special-purpose methods - they can overlap with the agent's behaviors 
	//-----------------------------------------------------------------------
	
	/**
	 * Explicitly FALSE! To avoid race conditions never let it TRUE
	 */
	@Override
	protected boolean setRegisterForDayEvents() {
		return false;
	}

	@Override
	protected void doOnNewDay() 
	{
		
		//Generate Clients Behavior
		//TODO Complete
		
		try {
			ContainerController cc = getContainerController();
			AgentController ac = null;
			ac = cc.createNewAgent("client1", "hotelmania.group2.platform.AgClient", null);
			ac.start();


		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
}
