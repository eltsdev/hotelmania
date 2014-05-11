package hotelmania.group2.platform;

import hotelmania.ontology.DayEvent;
import hotelmania.ontology.NotificationDayEvent;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
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
import java.util.Set;
import java.util.UUID;

public class AgPlatform2 extends MetaAgent
{
	private static final long serialVersionUID = -4208905954219155107L;

	private SubscriptionResponder subscriptionResponder;
	private Set<Subscription> suscriptions = new HashSet<Subscription>();

	//------------------------------------------------- 
	// Setup
	//-------------------------------------------------

	@Override
	protected void setup() 
	{
		super.setup();
		loadProperties();
		registerServices(Constants.SUBSCRIBETODAYEVENT_ACTION); //TODO + set time behavior?

		// Behaviors

		//addBehaviour(new SetTimeSpeedBehavior(this));
		addBehaviour(new GeneratePlatformAgentsBehavior(this));
		//addBehaviour(new GenerateClientsBehavior(this));
		//addBehaviour(new CreateDayEventsBehavior(this, mt ));

		// Set up notification of day events
		
		MessageTemplate subscriptionTemplate = MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
				MessageTemplate.MatchLanguage(codec.getName()),
				MessageTemplate.MatchOntology(ontology.getName())),
				MessageTemplate.MatchProtocol(Constants.SUBSCRIBETODAYEVENT_PROTOCOL)),
				MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE), MessageTemplate.MatchPerformative(ACLMessage.CANCEL)));
		
		SubscriptionManager gestor = new MySubscriptionManager();
		subscriptionResponder = new MySubscriptionResponder(this, subscriptionTemplate, gestor);
		addBehaviour(subscriptionResponder);

		addBehaviour(new TickerBehaviourExtension(this, Constants.DAY_IN_SECONDS));
	}

	 private final class TickerBehaviourExtension extends TickerBehaviour {
		private static final long serialVersionUID = 6616055369402031518L;

		private TickerBehaviourExtension(Agent a, long period) {
			super(a, period);
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
			
			
			System.out.println("Sending day notification to  # subscribers: "+ subscriptionResponder.getSubscriptions().size());
			
			for(Object subscriptionObj : subscriptionResponder.getSubscriptions())
			{
				if (subscriptionObj instanceof Subscription) {
					Subscription subscription = (Subscription) subscriptionObj;
					notify(subscription, notificationDayEvent);
					System.out.println(getLocalName()+": sending day event to: "+subscription.getMessage().getSender().getLocalName());
				}
			}
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

	 private class MySubscriptionResponder extends SubscriptionResponder {

		 private static final long serialVersionUID = 1487262053281422988L;
		 
		 private Subscription subscription; //TODO verify if this is neccesary

		 public MySubscriptionResponder(Agent agent, MessageTemplate template, SubscriptionManager gestor) {
			 super(agent, template, gestor);
		 }

		 protected ACLMessage handleSubscription(ACLMessage proposal) throws NotUnderstoodException ,RefuseException 
		 {
			 System.out.println(myAgent.getLocalName()+": subscription received from "+proposal.getSender().getLocalName());

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
					 System.out.println("error subscribing " + proposal.getSender().getLocalName() );
				 }
				 ACLMessage agree = proposal.createReply();
				 agree.setPerformative(ACLMessage.AGREE);
				 return agree;

			 } else {
				 // We refuse to perform the action
				 //System.out.println("Agent "+getLocalName()+": Refuse");
				 //throw new RefuseException("check-failed");
				 ACLMessage refuse = proposal.createReply();
				 refuse.setPerformative(ACLMessage.REFUSE);
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
				 System.out.printf("%s: CANCEL subscription received from " +  cancel.getSender().getLocalName());
				 //El SubscriptionManager elimina del registro la suscripcion
				 this.mySubscriptionManager.deregister(this.subscription); //TODO check if: this.mySubscriptionManager.deregister(createSubscription(cancel));
				 
			 } catch (Exception e) {
				 System.out.printf("%s: error removing subscription from " +  cancel.getSender().getLocalName());
			 }

			 ACLMessage response = cancel.createReply();
			 response.setPerformative(ACLMessage.INFORM);
			 return response;
		 }

	 }
	
	private boolean subscriptionActive() {
		return day >= Constants.SIMULATION_DAYS; //TODO || cancelled ;
	}

	private void loadProperties() {
		// create and load default properties
		Properties defaultProps = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream("resources/settings.properties");
			defaultProps.load(in);
			Constants.DAY_IN_SECONDS = Integer.parseInt(defaultProps.getProperty("day.length","5"))*1000;
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

				ac = cc.createNewAgent("bank", "hotelmania.group2.platform.AgBank", null);
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

	final class CreateDayEventsBehavior extends SubscriptionResponder 
	{
		private static final long serialVersionUID = -7739604235932591107L;

		public CreateDayEventsBehavior(Agent agent, MessageTemplate mt) {
			super(agent, mt);
		}
	}

	private final class MySubscriptionManager implements
			SubscriptionManager {
		public boolean register(Subscription subscription) {
		    suscriptions.add(subscription);
		    return true;
		}
	
		public boolean deregister(Subscription subscription) {
		    suscriptions.remove(subscription);
		    return true;
		}
	}
	


	@Override
	public void receivedAcceptance(ACLMessage message) {
		// TODO Auto-generated method stub

	}


	@Override
	public void receivedReject(ACLMessage message) {
		// TODO logs
	}


	@Override
	public void receivedNotUnderstood(ACLMessage message) {
//		TODO logs

	}
}
