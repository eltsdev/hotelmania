
package hotelmania.group2.platform;

import hotelmania.group2.dao.Client;
import hotelmania.ontology.DayEvent;
import hotelmania.ontology.NotificationDayEvent;
import hotelmania.ontology.NotificationEndSimulation;
import jade.content.Predicate;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionResponder;
import jade.proto.SubscriptionResponder.Subscription;
import jade.proto.SubscriptionResponder.SubscriptionManager;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

public class AgPlatform2 extends MetaAgent
{
	private static final long serialVersionUID = -4208905954219155107L;

	private MySubscriptionResponder dayEventResponder;
	private MySubscriptionResponder endSimulationResponder;

	//------------------------------------------------- 
	// Setup
	//-------------------------------------------------

	@Override
	protected void setup() 
	{
		super.setup();
		registerServices(Constants.SUBSCRIBETODAYEVENT_ACTION, Constants.END_SIMULATION_ACTION); //TODO + set time behavior?

		// Behaviors

		addBehaviour(new GeneratePlatformAgentsBehavior(this));

		dayEventResponder = new MySubscriptionResponder(this, Constants.SUBSCRIBETODAYEVENT_PROTOCOL);
		addBehaviour(dayEventResponder);
		addBehaviour(new DayTickerBehaviour(this, Constants.DAY_IN_MILLISECONDS));
		
		endSimulationResponder = new MySubscriptionResponder(this, Constants.END_SIMULATION_PROTOCOL);
		addBehaviour(endSimulationResponder);

	}

	 public static void loadProperties() {
		// create and load default properties
		Properties defaultProps = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream("resources/settings.properties");
			defaultProps.load(in);
			//Official settings
			Constants.DAY_IN_MILLISECONDS = Integer.parseInt(defaultProps.getProperty("day.length","15"))*1000;
			Constants.SIMULATION_DAYS = Integer.parseInt(defaultProps.getProperty("simulation.days","10"));
			Constants.CLIENTS_PER_DAY=Integer.parseInt(defaultProps.getProperty("simulation.clients_per_day","2"));
			Constants.CLIENTS_BUDGET=Integer.parseInt(defaultProps.getProperty("clients.budget","90"));
			Constants.CLIENTS_BUDGET_VARIANCE=Integer.parseInt(defaultProps.getProperty("clients.budget_variance","20"));
			Constants.SIMULATION_TIME_TO_START = Integer.parseInt(defaultProps.getProperty("simulation.time_to_start","1"));
			//Private settings
			Constants.REPORT_FILE=defaultProps.getProperty("","results.txt");
			Constants.LOG_DEBUG=Boolean.parseBoolean(defaultProps.getProperty("log.debug","true"));
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (Constants.SIMULATION_DAYS <= 1) {
			//throw new Exception("ERROR: The number of days in the settings does not allow the generation of valid booking dates for clients. Please fix it: "+Constants.SIMULATION_DAYS);
			Logger.logError("The number of days in the settings does not allow the generation of valid booking dates for clients. Please fix it to at least 2 days."+Constants.SIMULATION_DAYS);
		}
	}


	// --------------------------------------------------------
	// Behaviors
	// --------------------------------------------------------

	//-----------------------------------------------------------------------
	// Special-purpose methods - they can overlap with the agent's behaviors 
	//-----------------------------------------------------------------------
	
	/**
	 * Explicitly FALSE! To avoid sending messages to itself
	 */
	@Override
	protected boolean setRegisterForDayEvents() {
		return false;
	}
	
	/**
	 * Explicitly FALSE! To avoid sending messages to itself
	 */
	@Override
	protected boolean setRegisterForEndSimulation() {
		return false;
	}

	private final class GeneratePlatformAgentsBehavior extends MetaSimpleBehaviour 
	{
		private static final long serialVersionUID = -9078033789982364797L;

		private GeneratePlatformAgentsBehavior(Agent a) {
			super(a);
		}

		public void action() {
			try {
				ContainerController containController = getContainerController();
				AgentController agentController = null;

				agentController = containController.createNewAgent("reporter", AgReporter.class.getName(), null);
				agentController.start();

				agentController = containController.createNewAgent("bank", AgBank.class.getName(), null);
				agentController.start();

				agentController = containController.createNewAgent("hotelmania", AgHotelmania.class.getName(), null);
				agentController.start();

				agentController = containController.createNewAgent("agency", AgAgency.class.getName(), null);
				agentController.start();

				setDone(true);
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
	}

	private final class DayTickerBehaviour extends TickerBehaviour {
		private static final long serialVersionUID = 6616055369402031518L;
	
		private DayTickerBehaviour(Agent a, long period) {
			super(a, period);
		}
	
		@Override
		public void stop() {
			//stop/delete all agents
			sendEndSimulationEventToSubscriptors();
			//stop platform
			super.stop();
		}
		
		private void sendEndSimulationEventToSubscriptors() {
			NotificationEndSimulation event = new NotificationEndSimulation(); 
			
			Logger.logDebug("*************************************************************");
			Logger.logDebug("Simulation End");
			Logger.logDebug("*************************************************************");
			
			Logger.logDebug(myName() + ": Sending end simulation order to  # subscribers: "+ endSimulationResponder.getSubscriptions().size());
			
			for(Object subscriptionObj : endSimulationResponder.getSubscriptions())
			{
				if (subscriptionObj instanceof Subscription) {
					Subscription subscription = (Subscription) subscriptionObj;
					notify(subscription, event);
					Logger.logDebug(myName()+": sending end simulation event to: "+subscription.getMessage().getSender().getLocalName());
				}
			}
		}
	
		public void onTick() 
		{
			//Day number
			int day = getTickCount();
			Constants.DAY = day;
			if (isSimulationEnd(day)) {
				stop();
				return;
			}
	
			sendDayNotificationToSubscriptors(day);
	
			if (day >= Constants.FIRST_DAY && day >= Constants.SIMULATION_TIME_TO_START && day < Constants.SIMULATION_DAYS) {
				generateClientsBehavior(day);
			}
		}
	
		private void sendDayNotificationToSubscriptors(int day) {
			NotificationDayEvent notificationDayEvent = new NotificationDayEvent();
			DayEvent dayEvent = new DayEvent();
			dayEvent.setDay(day);
			notificationDayEvent.setDayEvent(dayEvent);
			
			Logger.logDebug("*************************************************************");
			Logger.logDebug("Day = "+day);
			Logger.logDebug("*************************************************************");
			
			
			Logger.logDebug(myName() + ": Sending day notification to  # subscribers: "+ dayEventResponder.getSubscriptions().size());
			
			for(Object subscriptionObj : dayEventResponder.getSubscriptions())
			{
				if (subscriptionObj instanceof Subscription) {
					Subscription subscription = (Subscription) subscriptionObj;
					notify(subscription, notificationDayEvent);
				}
			}
		}
	
		private void notify(SubscriptionResponder.Subscription subscription, Predicate data) {
			try {
				ACLMessage notification = subscription.getMessage().createReply();
				notification.addUserDefinedParameter(ACLMessage.IGNORE_FAILURE, "true");
				notification.setPerformative(ACLMessage.INFORM);
	
				getContentManager().fillContent(notification, data);
				subscription.notify(notification);
				log.logSendRequest(notification);
				}
			catch (Exception e) {
				e.printStackTrace();
				//FIXME: Check whether a FAILURE message should be sent back.       
			}
		}
		
		private boolean isSimulationEnd(int day) {
			//TODO || cancelled ;
			return day > Constants.SIMULATION_DAYS + 1; 
		}
	}

	/**
	 * Generate Clients Behavior
	 * @param day
	 */
	private void generateClientsBehavior(int day) 
	{
		ContainerController cc = getContainerController();
		AgentController ac = null;
		
		// Generate clients except last day
		for (int i = 0; i < Constants.CLIENTS_PER_DAY ; i++) {
			try {
				Client client = ClientGenerator.randomClient(day);
	
				String clientName = "Client_born_"+day+"_#_"+i;
				ac = cc.createNewAgent(clientName, AgClient.class.getName(), new Object[]{client});
				ac.start();
	
				synchronized (this) {
					Constants.CLIENTS_GENERATED++;
				}
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
	
		}
	}


	@Override
	public void receivedAcceptance(ACLMessage message) {
	}


	@Override
	public void receivedReject(ACLMessage message) {
	}


	@Override
	public void receivedNotUnderstood(ACLMessage message) {
	}


	@Override
	public void receivedInform(ACLMessage message) {
	}

	private final class MySubscriptionManager implements
			SubscriptionManager {
		private Set<Subscription> subscriptors = new HashSet<Subscription>();
		
		public boolean register(Subscription subscription) {
		    subscriptors.add(subscription);
		    return true;
		}
	
		public boolean deregister(Subscription subscription) {
			subscriptors.remove(subscription);
		    return true;
		}
	}
	
	private class MySubscriptionResponder extends SubscriptionResponder {

		 private static final long serialVersionUID = 1487262053281422988L;
		 
		 private Subscription subscription;
		 
		 private String protocol;

		 public MySubscriptionResponder(Agent agent, String protocol) {
			 super(agent, 
						// Set up notification of day events
						MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
						MessageTemplate.MatchLanguage(codec.getName()),
						MessageTemplate.MatchOntology(ontology.getName())),
						MessageTemplate.MatchProtocol(protocol)),
						MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE), MessageTemplate.MatchPerformative(ACLMessage.CANCEL))),
						new MySubscriptionManager());
			 
			 this.protocol = protocol;
		 }

		 protected ACLMessage handleSubscription(ACLMessage proposal) throws NotUnderstoodException ,RefuseException 
		 {
			 log.logReceivedMsg(proposal);
			 ACLMessage reply = proposal.createReply();

			 if (checkAction(proposal)) {

				 if (proposal.getConversationId() == null) {
					 String conversationId = String.valueOf(proposal.getSender().hashCode()) + UUID.randomUUID().toString();
					 proposal.setConversationId(conversationId);
				 }
				 subscription = this.createSubscription(proposal);
				 try {
					 this.mySubscriptionManager.register(subscription);
					 reply.setPerformative(ACLMessage.AGREE);
				 } catch (Exception e) {
					 reply.setPerformative(ACLMessage.FAILURE);
					 e.printStackTrace();
				 }

			 } else {
				 // We refuse to perform the action
				 reply.setPerformative(ACLMessage.REFUSE);
			 }
			 
			 log.logSendReply(reply);
			 return reply;
		 }

		 private boolean checkAction(ACLMessage request) {
			 //Avoid self-subscriptions
			 if (request.getSender().equals(myAgent.getAID())) {
				 return false;
			 }
			 // TODO verify other cases
			 return true;
		 }

		 // If the CANCEL message has a meaningful content, use it. 
		 // Otherwise unregister the Subscription with the same convID (default)
		 protected ACLMessage handleCancel(ACLMessage cancel) throws FailureException {
			 log.logReceivedMsg(cancel);
			 
			 try {
				 //SubscriptionManager deletes subscription record
				 this.mySubscriptionManager.deregister(this.subscription); //TODO check if: this.mySubscriptionManager.deregister(createSubscription(cancel));
				 
			 } catch (Exception e) {
				 Logger.logError(myName()+": ["+this.protocol+"] %s: error removing subscription from " +  cancel.getSender().getLocalName());
			 }

			 ACLMessage response = cancel.createReply();
			 response.setPerformative(ACLMessage.INFORM);
			 log.logSendReply(response);
			 return response;
		 }

	 }
}
