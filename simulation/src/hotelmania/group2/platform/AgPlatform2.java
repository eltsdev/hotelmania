package hotelmania.group2.platform;

import hotelmania.ontology.DayEvent;
import hotelmania.ontology.NotificationDayEvent;
import jade.content.abs.AbsIRE;
import jade.content.abs.AbsPredicate;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLVocabulary;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import jade.proto.SubscriptionResponder;
import jade.proto.SubscriptionResponder.Subscription;
import jade.proto.SubscriptionResponder.SubscriptionManager;
import jade.util.Logger;
import jade.util.leap.List;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AgPlatform2 extends MetaAgent
{
	private static final long serialVersionUID = -4208905954219155107L;
	private MessageTemplate subscriptionTemplate;
	private SubscriptionResponder subscriptionResponder;
	private Set<Subscription> suscripciones = new HashSet<Subscription>();

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
		//		timeBehavior();
		
		System.out.println(getLocalName()+" waiting for requests...");

		subscriptionTemplate = MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
				MessageTemplate.MatchLanguage(codec.getName()),
				MessageTemplate.MatchOntology(ontology.getName())),
				MessageTemplate.MatchProtocol(Constants.SUBSCRIBETODAYEVENT_PROTOCOL)),
				MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE), MessageTemplate.MatchPerformative(ACLMessage.CANCEL)));
		
		SubscriptionManager gestor = new SubscriptionManager() {
			 
            public boolean register(Subscription subscription) {
                suscripciones.add(subscription);
                return true;
            }
 
            public boolean deregister(Subscription subscription) {
                suscripciones.remove(subscription);
                return true;
            }
        };
		
		subscriptionResponder = new HacerSuscripcion(this, subscriptionTemplate, gestor);
		addBehaviour(subscriptionResponder);

		doTick();
	}

	 private class HacerSuscripcion extends SubscriptionResponder {
		 private static final long serialVersionUID = 1487262053281422988L;
			private Subscription subscription;
		 
		 public HacerSuscripcion(Agent agent, MessageTemplate template, SubscriptionManager gestor) {
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
					System.out.println(proposal);
					this.subscription = this.createSubscription(proposal);
					try {
						this.mySubscriptionManager.register(this.subscription);
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
	                this.mySubscriptionManager.deregister(this.subscription);
	            } catch (Exception e) {
					System.out.printf("%s: error removing subscription from " +  cancel.getSender().getLocalName());
	            }
	 
	            //Acepta la cancelación y responde
	            ACLMessage response = cancel.createReply();
	            response.setPerformative(ACLMessage.INFORM);
	            return response;
			}
		 
	 }
	
	@Deprecated
	private void timeBehavior() 
	{

		addBehaviour(new SubscriptionResponder(this, subscriptionTemplate) {
			private static final long serialVersionUID = 7696805654686733174L;

			protected ACLMessage handleSubscription(ACLMessage subscription) throws NotUnderstoodException ,RefuseException {
				System.out.println("Agent "+getLocalName()+": REQUEST received from "+subscription.getSender().getName()+". Action is "+subscription.getContent());
				if (checkAction(subscription)) {
					// We agree to perform the action. Note that in the FIPA-Request
					// protocol the AGREE message is optional. Return null if you
					// don't want to send it.
					System.out.println("Agent "+getLocalName()+": Agree");
					ACLMessage agree = subscription.createReply();
					agree.setProtocol(Constants.SUBSCRIBETODAYEVENT_PROTOCOL);
					agree.setPerformative(ACLMessage.AGREE);

					//					createSubscription(agree).notify(notification);

					return agree;
				}
				else {
					// We refuse to perform the action
					System.out.println("Agent "+getLocalName()+": Refuse");
					throw new RefuseException("check-failed");
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

		} );
	}

	private void doTick() {
		addBehaviour(new TickerBehaviour(this, Constants.DAY_IN_SECONDS) 
		{
			private static final long serialVersionUID = 6616055369402031518L;

			public void onTick() 
			{

				//				ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
				//				//				msg.addReceiver(topic);
				//				inform.setLanguage(codec.getName());
				//				inform.setOntology(ontology.getName());
				//				inform.setProtocol(Constants.SUBSCRIBETODAYEVENT_PROTOCOL);


				//Day number
				int day = getTickCount();
				NotificationDayEvent notificationDayEvent = new NotificationDayEvent();
				DayEvent dayEvent = new DayEvent();
				dayEvent.setDay(day);
				notificationDayEvent.setDayEvent(dayEvent);


				//				try {
				//					getContentManager().fillContent(inform, notificationDayEvent);
				//					myAgent.notify();
				//					System.out.println("Agent "+myAgent.getLocalName()+": day = "+day);
				//				} catch (CodecException | OntologyException e) {
				//					e.printStackTrace();
				//				}
				System.out.print("Sending to subscribers: ");
				System.out.println(subscriptionResponder.getSubscriptions().size());
				for(Object subscriptionObj : subscriptionResponder.getSubscriptions())
				{
					System.out.println("sending");
					Subscription subscription = (Subscription) subscriptionObj;
					notify(subscription, notificationDayEvent);
				}

			}

			private void notify(SubscriptionResponder.Subscription sub, NotificationDayEvent data) {
				try {
					ACLMessage notification = sub.getMessage().createReply();
					notification.addUserDefinedParameter(ACLMessage.IGNORE_FAILURE, "true");
					notification.setPerformative(ACLMessage.INFORM);

					getContentManager().fillContent(notification, data);
					//pass to Subscription the message to send
					sub.notify(notification);
				}
				catch (Exception e) {
					e.printStackTrace();
					//FIXME: Check whether a FAILURE message should be sent back.       
				}
			}
		} );
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
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * Periodically send messages about topic "NEW_DAY"
	 */
	@Deprecated
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


	@Override
	public void receivedAcceptance(ACLMessage message) {
		// TODO Auto-generated method stub

	}


	@Override
	public void receivedReject(ACLMessage message) {
		// TODO Auto-generated method stub

	}


	@Override
	public void receivedNotUnderstood(ACLMessage message) {
		// TODO Auto-generated method stub

	}

	final class CreateDayEventsBehavior extends SubscriptionResponder 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -7739604235932591107L;

		public CreateDayEventsBehavior(Agent agent, MessageTemplate mt) {
			super(agent, mt);
		}


	}
}
