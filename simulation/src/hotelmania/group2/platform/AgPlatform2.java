
package hotelmania.group2.platform;

import hotelmania.group2.dao.Client;
import hotelmania.group2.dao.Stay;
import hotelmania.ontology.DayEvent;
import hotelmania.ontology.NotificationDayEvent;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
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
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class AgPlatform2 extends MetaAgent
{
	private static final long serialVersionUID = -4208905954219155107L;

	private MySubscriptionResponder dayEventResponder;
	private MySubscriptionResponder endSimulationResponder;
	private Random randomNumber = new Random();

	//------------------------------------------------- 
	// Setup
	//-------------------------------------------------

	@Override
	protected void setup() 
	{
		super.setup();
		loadProperties();
		registerServices(Constants.SUBSCRIBETODAYEVENT_ACTION, Constants.END_SIMULATION_ACTION); //TODO + set time behavior?

		// Behaviors

		addBehaviour(new GeneratePlatformAgentsBehavior(this));

		dayEventResponder = new MySubscriptionResponder(this, Constants.SUBSCRIBETODAYEVENT_PROTOCOL);
		addBehaviour(dayEventResponder);
		addBehaviour(new TickerBehaviourExtension(this, Constants.DAY_IN_SECONDS));
		
		endSimulationResponder = new MySubscriptionResponder(this, Constants.END_SIMULATION_PROTOCOL);
		addBehaviour(endSimulationResponder);


	}

	 private final class TickerBehaviourExtension extends TickerBehaviour {
		private static final long serialVersionUID = 6616055369402031518L;

		private TickerBehaviourExtension(Agent a, long period) {
			super(a, period);
		}

		@Override
		public void stop() {
			System.out.println("*************************************************************");
			System.out.println("Stopping Simulation.");
			System.out.println("*************************************************************");
			//stop/delete all agents
			
			
			super.stop();
		}
		
		public void onTick() 
		{
			//Day number
			int day = getTickCount();
			
			NotificationDayEvent notificationDayEvent = new NotificationDayEvent();
			DayEvent dayEvent = new DayEvent();
			dayEvent.setDay(day);
			notificationDayEvent.setDayEvent(dayEvent);
			
			System.out.println("*************************************************************");
			System.out.println("Day = "+day);
			System.out.println("*************************************************************");
			
			
			System.out.println(myName() + ": Sending day notification to  # subscribers: "+ dayEventResponder.getSubscriptions().size());
			
			for(Object subscriptionObj : dayEventResponder.getSubscriptions())
			{
				if (subscriptionObj instanceof Subscription) {
					Subscription subscription = (Subscription) subscriptionObj;
					notify(subscription, notificationDayEvent);
					System.out.println(myName()+": sending day event to: "+subscription.getMessage().getSender().getLocalName());
				}
			}

			if (!isSubscriptionToDayEventActive(day)) {
				stop();
				return;
			}
			
			doOnNewDayBeforeEveryBody(day);
		}

		private void notify(SubscriptionResponder.Subscription sub, NotificationDayEvent data) {
			try {
				ACLMessage notification = sub.getMessage().createReply();
				notification.addUserDefinedParameter(ACLMessage.IGNORE_FAILURE, "true");
				notification.setPerformative(ACLMessage.INFORM);

				getContentManager().fillContent(notification, data);
				sub.notify(notification);
			}
			catch (Exception e) {
				e.printStackTrace();
				//FIXME: Check whether a FAILURE message should be sent back.       
			}
		}
	}

	private boolean isSubscriptionToDayEventActive(int day) {
		//TODO || cancelled ;
		return day < Constants.SIMULATION_DAYS; 
	}

	private void loadProperties() {
		// create and load default properties
		Properties defaultProps = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream("resources/settings.properties");
			defaultProps.load(in);
			Constants.DAY_IN_SECONDS = Integer.parseInt(defaultProps.getProperty("day.length","15"))*1000;
			Constants.SIMULATION_DAYS = Integer.parseInt(defaultProps.getProperty("simulation.days","10"));
			Constants.CLIENTS_PER_DAY=Integer.parseInt(defaultProps.getProperty("simulation.clients_per_day","2"));
			Constants.ROOMS_PER_HOTEL = 6;
			Constants.CLIENTS_BUDGET=Integer.parseInt(defaultProps.getProperty("clients.budget","90"));
			Constants.CLIENTS_BUDGET_VARIANCE=Integer.parseInt(defaultProps.getProperty("clients.budget_variance","20"));
			//TODO load other parameters...
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	// --------------------------------------------------------
	// Behaviors
	// --------------------------------------------------------

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
				ContainerController containController = getContainerController();
				AgentController agentController = null;

				agentController = containController.createNewAgent("reporter", AgReporter.class.getName(), null);
				agentController.start();

				agentController = containController.createNewAgent("hotelmania", AgHotelmania.class.getName(), null);
				agentController.start();

				agentController = containController.createNewAgent("agency", AgAgency.class.getName(), null);
				agentController.start();

				agentController = containController.createNewAgent("bank", AgBank.class.getName(), null);
				agentController.start();
				
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

	//-----------------------------------------------------------------------
	// Special-purpose methods - they can overlap with the agent's behaviors 
	//-----------------------------------------------------------------------

	final class CreateDayEventsBehavior extends SubscriptionResponder 
	{
		private static final long serialVersionUID = -7739604235932591107L;

		public CreateDayEventsBehavior(Agent agent, MessageTemplate mt) {
			super(agent, mt);
		}
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
			 System.out.println(myName()+": ["+this.protocol+"]  subscription received from "+proposal.getSender().getLocalName());

			 if (checkAction(proposal)) {

				 if (proposal.getConversationId() == null) {
					 String conversationId = String.valueOf(proposal.getSender().hashCode()) + UUID.randomUUID().toString();
					 proposal.setConversationId(conversationId);
				 }
				 subscription = this.createSubscription(proposal);
				 try {
					 this.mySubscriptionManager.register(subscription);
				 } catch (Exception e) {
					 e.printStackTrace();
					 System.out.println(myName()+": ["+this.protocol+"]  error subscribing " + proposal.getSender().getLocalName() );
				 }
				 ACLMessage agree = proposal.createReply();
				 agree.setPerformative(ACLMessage.AGREE);
				 System.out.println(myName()+": ["+this.protocol+"]  subscription agree for " + proposal.getSender().getLocalName() );
				 return agree;

			 } else {
				 // We refuse to perform the action
				 //System.out.println("Agent "+myName()+": Refuse");
				 //throw new RefuseException("check-failed");
				 ACLMessage refuse = proposal.createReply();
				 refuse.setPerformative(ACLMessage.REFUSE);
				 System.out.println(myName()+": ["+this.protocol+"] refuse subscription from " + proposal.getSender().getLocalName() );
				 return refuse;
			 }

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
		 // Otherwise deregister the Subscription with the same convID (default)
		 protected ACLMessage handleCancel(ACLMessage cancel) throws FailureException {
			 try {
				 System.out.printf(myName()+": ["+this.protocol+"] %s: CANCEL subscription received from " +  cancel.getSender().getLocalName());
				 //El SubscriptionManager elimina del registro la suscripcion
				 this.mySubscriptionManager.deregister(this.subscription); //TODO check if: this.mySubscriptionManager.deregister(createSubscription(cancel));
				 
			 } catch (Exception e) {
				 System.out.printf(myName()+": ["+this.protocol+"] %s: error removing subscription from " +  cancel.getSender().getLocalName());
			 }

			 ACLMessage response = cancel.createReply();
			 response.setPerformative(ACLMessage.INFORM);
			 return response;
		 }

	 }

	/**
	 * Called internally (not by subscription)
	 */
	private void doOnNewDayBeforeEveryBody(int day) 
	{
		//Generate Clients Behavior
		ContainerController cc = getContainerController();
		AgentController ac = null;
		
		for (int i = 0; i < Constants.CLIENTS_PER_DAY; i++) {

			try {
				Client client = new Client();
				client.setStay(randomStay());
				client.setBudget(randomBudget());

				String clientName = "Client_born_"+day+"_sn_"+i;
				ac = cc.createNewAgent(clientName, AgClient.class.getName(), new Object[]{client});
				ac.start();

			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * A random normally distributed number. 
	 * @return
	 */
	private double randomBudget() {
		return randomBetween(Constants.CLIENTS_BUDGET, Math.sqrt(Constants.CLIENTS_BUDGET_VARIANCE));
	}

	private double randomBetween(double mean ,
			double standardDeviation) {
		return randomNumber.nextGaussian()*standardDeviation + mean;
	}

	private Stay randomStay() {
		int start = randomBetween(1, Constants.SIMULATION_DAYS);
		int days = randomBetween(0, Constants.CLIENTS_MAX_STAY_DAYS);
		return new Stay(start ,start+days);
	}

	private int randomBetween(int lower, int upper) {
		return randomNumber.nextInt(upper)+lower;
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
}
