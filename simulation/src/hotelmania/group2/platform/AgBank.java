package hotelmania.group2.platform;

import hotelmania.ontology.ChargeAccount;
import hotelmania.ontology.CreateAccount;
import hotelmania.ontology.MakeDeposit;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;

public class AgBank extends MetaAgent {
	
	private static final long serialVersionUID = 2893904717857535232L;

	//------------------------------------------------- 
	// Agent Attributes
	//-------------------------------------------------

	AID agHotel;
	AID agAgency;
	AID agReporter;

	/*
	 * List of Hotel Account
	 */
	private ArrayList<CreateAccount> listHotelAccount;

	/*
	 * (non-Javadoc)
	 * 
	 * @see jade.core.Agent#setup()
	 */
	@Override
	protected void setup() {
		super.setup();
		listHotelAccount = new ArrayList<CreateAccount>();
		registerServices(Constants.CREATEACCOUNT_ACTION);

		// Create hotel account
		addBehaviour(new CreateAccountBehavior(this));

		// Charge Staff services in hotel account
		addBehaviour(new ChargeAccountBehavior(this));

		// Used by clients to pay to hotels
		addBehaviour(new MakeDepositBehavior(this));

		// Provide info account to hotel
		addBehaviour(new ProvideHotelAccountInfoBehavior(this));

	}
	
	/**
	 * This means: I am NOT interested on this event.
	 */
	@Override
	protected boolean setRegisterForDayEvents() {
		return false;
	}

	// --------------------------------------------------------
	// BEHAVIOURS
	// --------------------------------------------------------

	private final class CreateAccountBehavior extends MetaCyclicBehaviour {
		
		private static final long serialVersionUID = 7390814510706022198L;

		public CreateAccountBehavior(Agent a) {
			super(a);
		}

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

					// If the action is Create Account...
					if (conc instanceof CreateAccount) {
						// execute request
						ACLMessage reply = createAccount(msg, (CreateAccount) conc);
						myAgent.send(reply);

						System.out.println(myAgent.getLocalName()
								+ ": answer sent -> " + log);
					}
				}

			} catch (CodecException | OntologyException e) {
				e.printStackTrace();
			}
		}

		/**
		 * @param msg
		 * @param accountData
		 * @return
		 */
		private ACLMessage createAccount(ACLMessage msg, CreateAccount accountData) {
			
			System.out.println(myAgent.getLocalName()
					+ ": received Account Request from "
					+ (msg.getSender()).getLocalName());

			ACLMessage reply = msg.createReply();
			
			if (accountData != null && accountData.getHotel() != null) {
				if (registerNewAccount(accountData)) {
					reply.setPerformative(ACLMessage.AGREE);
					this.log = Constants.AGREE;
				} else {
					reply.setPerformative(ACLMessage.REFUSE);
					this.log = Constants.REFUSE;
				}
			} else {
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				this.log = Constants.NOT_UNDERSTOOD;
			}

			return reply;
		}

		/**
		 * @param account
		 * @return
		 */
		private boolean registerNewAccount(CreateAccount account) {
			return accountDAO.registerNewAccount(account.getHotel()
					.getHotel_name(), account.getBalance());
		}
	}

	private final class ProvideHotelAccountInfoBehavior extends MetaCyclicBehaviour {

		private static final long serialVersionUID = -4414753731149819352L;

		public ProvideHotelAccountInfoBehavior(AgBank agBank) {
			super(agBank);
		}

		@Override
		public void action() {
			block();//TODO blank!
		}

	}

	private final class ChargeAccountBehavior extends MetaCyclicBehaviour {

		private static final long serialVersionUID = 5591566038041266929L;

		public ChargeAccountBehavior(AgBank agBank) {
			super(agBank);
		}

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

					// If the action is Charge Account...
					if (conc instanceof ChargeAccount) {
						// execute request
						ACLMessage reply = chargeAccount(msg, (ChargeAccount) conc);
						myAgent.send(reply);

						System.out.println(myAgent.getLocalName()
								+ ": answer sent -> " + log);
					}
				}

			} catch (CodecException | OntologyException e) {
				e.printStackTrace();
			}
		}

		/**
		 * @param msg
		 * @param conc
		 * @return
		 */
		private ACLMessage chargeAccount(ACLMessage msg, ChargeAccount money) {
			System.out.println(myAgent.getLocalName()
					+ ": received Cliente Deposit Request from "
					+ (msg.getSender()).getLocalName());

			ACLMessage reply = msg.createReply();
			
			if (money != null && money.getHotel() != null) {
				if (chargeMoney(money)) {
					reply.setPerformative(ACLMessage.AGREE);
					this.log = Constants.AGREE;
				} else {
					reply.setPerformative(ACLMessage.REFUSE);
					this.log = Constants.REFUSE;
				}
			} else {
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				this.log = Constants.NOT_UNDERSTOOD;
			}

			return reply;
			}


		/**
		 * @param money
		 * @return
		 */
		private boolean chargeMoney(ChargeAccount money) {
			return accountDAO.chargeMoney(money.getHotel().getHotel_name(),
					money.getMoney());
		}

	}

	//TODO Continue refactoring from here...
	
	private final class MakeDepositBehavior extends CyclicBehaviour {
		
		private static final long serialVersionUID = 5591566038041266929L;
		private static final int VALID_REQ = 0;
		private static final int REJECT_REQ = -1;
		private static final int NOT_UNDERSTOOD_REQ = 1;

		/**
		 * @param agBank
		 */
		public MakeDepositBehavior(AgBank agBank) {
			super(agBank);
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

					// If the action is Create Account...
					if (conc instanceof MakeDeposit) {
						// execute request
						int answer = makeDeposit(msg, (MakeDeposit) conc);

						// send reply
						ACLMessage reply = msg.createReply();
						String log = "";
						switch (answer) {
						case VALID_REQ:
							reply.setPerformative(ACLMessage.AGREE);
							log = "AGREE";
							break;

						case REJECT_REQ:
							reply.setPerformative(ACLMessage.REFUSE);
							log = "REFUSE";
							break;

						case NOT_UNDERSTOOD_REQ:
						default:
							reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
							log = "NOT_UNDERSTOOD";
							break;
						}

						myAgent.send(reply);

						System.out.println(myAgent.getLocalName()
								+ ": answer sent -> " + log);
					}
				}

			} catch (CodecException | OntologyException e) {
				e.printStackTrace();
			}

		}

		/**
		 * @param msg
		 * @param deposit
		 * @return
		 */
		private int makeDeposit(ACLMessage msg, MakeDeposit deposit) {
			System.out.println(myAgent.getLocalName()
					+ ": received Cliente Deposit Request from "
					+ (msg.getSender()).getLocalName());

			if (deposit != null && deposit.getHotel() != null) {
				if (registerNewDeposit(deposit)) {
					return ACLMessage.AGREE;
				} else {
					return ACLMessage.REFUSE;
				}
			} else {
				return ACLMessage.NOT_UNDERSTOOD;

			}
		}

		/**
		 * @param deposit
		 * @return
		 */
		private boolean registerNewDeposit(MakeDeposit deposit) {
			return accountDAO.registerNewDeposit(deposit.getHotel()
					.getHotel_name(), deposit.getMoney());
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

}