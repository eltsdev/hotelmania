package hotelmania.group2.platform;

import hotelmania.ontology.ChargeAccount;
import hotelmania.ontology.CreateAccount;
import hotelmania.ontology.MakeDeposit;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgBank extends MetaAgent {
	
	private static final long serialVersionUID = 2893904717857535232L;

	@Override
	protected void setup() {
		super.setup();
		
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

		public ProvideHotelAccountInfoBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			/*
			 * Look for messages
			 */
			ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchProtocol(Constants.CONSULTACCOUNTSTATUS_PROTOCOL)),
					MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF)));

			/*
			 * If no message arrives
			 */
			if (msg == null) {
				block();
				return;
			}
			
			//TODO to implement...
			block();
		}

	}

	private final class ChargeAccountBehavior extends MetaCyclicBehaviour {

		private static final long serialVersionUID = 5591566038041266929L;

		public ChargeAccountBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			/*
			 * Look for messages
			 */
			ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchProtocol(Constants.CHARGEACCOUNT_PROTOCOL)),
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

						System.out.println(myAgent.getLocalName() + ": answer sent -> " + log);
					}
				}

			} catch (CodecException | OntologyException e) {
				e.printStackTrace();
			}
		}

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

		private boolean chargeMoney(ChargeAccount money) {
			return accountDAO.chargeMoney(money.getHotel().getHotel_name(),
					money.getMoney());
		}

	}

	private final class MakeDepositBehavior extends MetaCyclicBehaviour {
		
		private static final long serialVersionUID = 5591566038041266929L;

		public MakeDepositBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {

			/*
			 * Look for messages
			 */
			ACLMessage msg = receive(MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
					MessageTemplate.MatchLanguage(codec.getName()),
					MessageTemplate.MatchOntology(ontology.getName())),
					MessageTemplate.MatchProtocol(Constants.MAKEDEPOSIT_PROTOCOL)),
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
						ACLMessage reply = makeDeposit(msg, (MakeDeposit) conc);
						myAgent.send(reply);

						System.out.println(myAgent.getLocalName() + ": answer sent -> " + log);
					}
				}

			} catch (CodecException | OntologyException e) {
				e.printStackTrace();
			}

		}

		private ACLMessage makeDeposit(ACLMessage msg, MakeDeposit deposit) {
			System.out.println(myAgent.getLocalName()
					+ ": received Cliente Deposit Request from "
					+ (msg.getSender()).getLocalName());

			ACLMessage reply = msg.createReply();

			if (deposit != null && deposit.getHotel() != null) {
				if (registerNewDeposit(deposit)) {
					this.log = Constants.AGREE;
					reply.setPerformative(ACLMessage.AGREE);
					//TODO attach the hotels info!!!
				} else {
					this.log = Constants.REFUSE;
					reply.setPerformative(ACLMessage.REFUSE); 
				}
			} else {
				this.log = Constants.NOT_UNDERSTOOD;
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			}
			return reply;
		}

		private boolean registerNewDeposit(MakeDeposit deposit) {
			return accountDAO.registerNewDeposit(deposit.getHotel()
					.getHotel_name(), deposit.getMoney());
		}

	}

	
	@Override
	public void receivedAcceptance(ACLMessage message) {
		//TODO switch by message.getProtocol()
	}

	@Override
	public void receivedReject(ACLMessage message) {
		// TODO Auto-generated method stub
		if (message.getProtocol().equals(Constants.CREATEACCOUNT_PROTOCOL)) {
			logRejectedMessage(Constants.CREATEACCOUNT_PROTOCOL, message);
		} else if (message.getProtocol().equals(Constants.CONSULTACCOUNTSTATUS_PROTOCOL)) {
			logRejectedMessage(Constants.CONSULTACCOUNTSTATUS_PROTOCOL, message);
		} else if (message.getProtocol().equals(Constants.CHARGEACCOUNT_PROTOCOL)) {
			logRejectedMessage(Constants.CHARGEACCOUNT_PROTOCOL, message);
		} else if (message.getProtocol().equals(Constants.MAKEDEPOSIT_PROTOCOL)) {
			logRejectedMessage(Constants.MAKEDEPOSIT_PROTOCOL, message);
		}
	}

	@Override
	public void receivedNotUnderstood(ACLMessage message) {
		// TODO Auto-generated method stub
		if (message.getProtocol().equals(Constants.CREATEACCOUNT_PROTOCOL)) {
			logNotUnderstoodMessage(Constants.CREATEACCOUNT_PROTOCOL, message);
		} else if (message.getProtocol().equals(Constants.CONSULTACCOUNTSTATUS_PROTOCOL)) {
			logNotUnderstoodMessage(Constants.CONSULTACCOUNTSTATUS_PROTOCOL, message);
		} else if (message.getProtocol().equals(Constants.CHARGEACCOUNT_PROTOCOL)) {
			logNotUnderstoodMessage(Constants.CHARGEACCOUNT_PROTOCOL, message);
		} else if (message.getProtocol().equals(Constants.MAKEDEPOSIT_PROTOCOL)) {
			logNotUnderstoodMessage(Constants.MAKEDEPOSIT_PROTOCOL, message);
		}	
	}


}