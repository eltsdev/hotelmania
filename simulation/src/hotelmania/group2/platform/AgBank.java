package hotelmania.group2.platform;

import hotelmania.group2.behaviours.GenericServerResponseBehaviour;
import hotelmania.group2.dao.Account;
import hotelmania.group2.dao.AccountDAO;
import hotelmania.group2.dao.Hotel;
import hotelmania.ontology.AccountStatus;
import hotelmania.ontology.AccountStatusQueryRef;
import hotelmania.ontology.ChargeAccount;
import hotelmania.ontology.CreateAccountRequest;
import hotelmania.ontology.MakeDeposit;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.ContentElementList;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

public class AgBank extends AbstractAgent {

	private static final long serialVersionUID = 2893904717857535232L;

	private AccountDAO accountDAO = new AccountDAO();

	@Override
	protected void setup() {
		super.setup();

		// Create hotel account
		addBehaviour(new CreateAccountBehavior(this));

		// Charge Staff services in hotel account
		addBehaviour(new ChargeAccountBehavior(this));

		// Used by clients to pay to hotels
		addBehaviour(new ReceiveClientDepositBehavior(this));

		// Provide info account to hotel
		addBehaviour(new ProvideHotelAccountInfoBehavior(this));
		
		// Get finance report
		addBehaviour(new GetFinanceReportBehavior(this));

		registerServices(Constants.CREATEACCOUNT_ACTION,
				Constants.CONSULTACCOUNTSTATUS_ACTION,
				Constants.CONSULTFINANCEREPORT_ACTION, 
				Constants.MAKEDEPOSIT_ACTION);

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

	private final class CreateAccountBehavior extends GenericServerResponseBehaviour {

		private static final long serialVersionUID = 7390814510706022198L;

		public CreateAccountBehavior(AbstractAgent agBank) {
			super(agBank, Constants.CREATEACCOUNT_PROTOCOL, ACLMessage.REQUEST);
		}

		
		@Override
		protected ACLMessage doSendResponse(ACLMessage msg) {
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
					if (conc instanceof CreateAccountRequest) {
						
						//Agree
						CreateAccountRequest request = (CreateAccountRequest)conc;
						this.validateAndSendAgree(msg, request);
				
						//Execute
						Hotel hotel = new Hotel(request.getHotel().getHotel_name(), msg.getSender());
						hotelmania.ontology.Account account = registerNewAccount(hotel);

						/*
						 * Inform Account Status
						 */
						ACLMessage reply = createAccount(msg, account);
						return reply;
					}
				}

			} catch (CodecException | OntologyException e) {
				e.printStackTrace();
			}

			return null;
			
		}

		private ACLMessage createAccount(ACLMessage request,hotelmania.ontology.Account accountOnto) {

			ACLMessage inform = request.createReply();
			// Create predicate Account Status
			AccountStatus predicate_account = new AccountStatus();
			predicate_account.setAccount(accountOnto);
			
			if (accountOnto == null) {
				inform.setPerformative(ACLMessage.FAILURE);
			} else {
				try {
					inform.setPerformative(ACLMessage.INFORM);
					getContentManager().fillContent(inform, predicate_account);
				} catch (CodecException | OntologyException e) {
					Logger.logDebug(myName() + ": Message: " + request.getContent());
					e.printStackTrace();
				}
			}
			return inform;
		}

		/**
		 * @param msg
		 * @param accountData
		 * @return
		 */
		private void validateAndSendAgree(ACLMessage msg, CreateAccountRequest accountData) {
			ACLMessage reply = msg.createReply();
			
			if (accountData != null && accountData.getHotel() != null) {
				reply.setPerformative(ACLMessage.AGREE);
			} else {
				reply.setPerformative(ACLMessage.REFUSE);
			}

			myAgent.send(reply);
			myAgent.getLog().logSendReply(reply);
		}

		/**
		 * @param account
		 * @return
		 */

		private hotelmania.ontology.Account registerNewAccount(Hotel hotel) {
			Account newAccount = accountDAO.registerNewAccount(hotel , 0);
			return newAccount.getConcept();

		}

	
	}

	private final class ProvideHotelAccountInfoBehavior extends  GenericServerResponseBehaviour {

		private static final long serialVersionUID = -4414753731149819352L;

		public ProvideHotelAccountInfoBehavior(AbstractAgent agBank) {
			super(agBank,Constants.CONSULTACCOUNTSTATUS_PROTOCOL, ACLMessage.QUERY_REF);
		}

		
		@Override
		protected ACLMessage doSendResponse(ACLMessage msg) {
			Concept conc = getConceptFromMessage(msg);

			if (conc instanceof AccountStatusQueryRef) {
				// Search the account
				AccountStatusQueryRef action = (AccountStatusQueryRef) conc;
				int idToRequest = action.getId_account();
				Account account = accountDAO.getAcountWithId(idToRequest);
				
				// send reply
				ACLMessage reply = answerGetInfoAccount(msg, account);
				return reply;
			}else{
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				reply.setContent("No sending the right Ontology");
				return reply;
			}
		}

		private ACLMessage answerGetInfoAccount(ACLMessage msg, Account account) {
			ACLMessage reply = msg.createReply();

			// Send the response
			if (account == null) {
				reply.setPerformative(ACLMessage.FAILURE);
			} else {
				AccountStatus accountStatus = new AccountStatus();
				accountStatus.setAccount(account.getConcept());
				try {
					reply.setPerformative(ACLMessage.INFORM);
					myAgent.getContentManager().fillContent(reply, accountStatus);
				} catch (CodecException | OntologyException e) {
					Logger.logDebug(myName() + ": Message: " + msg.getContent());
					e.printStackTrace();
				}
			}
			return reply;
		}

		
	}

	private final class ChargeAccountBehavior extends GenericServerResponseBehaviour {

		private static final long serialVersionUID = 5591566038041266929L;

		public ChargeAccountBehavior(AbstractAgent agBank) {
			super(agBank, Constants.CHARGEACCOUNT_PROTOCOL, ACLMessage.REQUEST);
		}

		@Override
		protected ACLMessage doSendResponse(ACLMessage msg) {
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
						ACLMessage reply = chargeAccount(msg,(ChargeAccount) conc);
						myAgent.send(reply);
						myAgent.getLog().logSendReply(reply);
						
						if (reply.getPerformative()==ACLMessage.AGREE) {
							reply.setPerformative(ACLMessage.INFORM);
						}else if (reply.getPerformative()==ACLMessage.REFUSE) {
							reply.setPerformative(ACLMessage.FAILURE);
						}
						return reply;
						
					}
					
				}

			} catch (CodecException | OntologyException e) {
				Logger.logDebug(myName() + ": Message: " + msg.getContent());
				e.printStackTrace();
				
			}
			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			reply.setContent("No sending the right Ontology");
			return reply;
		}

		private ACLMessage chargeAccount(ACLMessage msg, ChargeAccount money) {
			ACLMessage reply = msg.createReply();

			if (money != null && money.getHotel() != null) {
				if (chargeMoney(money)) {
					reply.setPerformative(ACLMessage.AGREE);
				} else {
					reply.setPerformative(ACLMessage.REFUSE);
				}
			} else {
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			}

			return reply;
		}

		private boolean chargeMoney(ChargeAccount money) {
			return accountDAO.chargeMoney(money.getHotel().getHotel_name(),
					money.getMoney());
		}

	
	}

	private final class ReceiveClientDepositBehavior extends GenericServerResponseBehaviour {

		private static final long serialVersionUID = 5591566038041266929L;

		public ReceiveClientDepositBehavior(AbstractAgent agBank) {
			super(agBank,Constants.MAKEDEPOSIT_PROTOCOL, ACLMessage.REQUEST);
		}
		
		@Override
		protected ACLMessage doSendResponse(ACLMessage msg) {
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
						return reply;
					}
				}

			} catch (CodecException | OntologyException e) {
				e.printStackTrace();
			}
			
			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			reply.setContent("No sending the right Ontology");
			return reply;

		}

		private ACLMessage makeDeposit(ACLMessage msg, MakeDeposit deposit) {
			ACLMessage reply = msg.createReply();

			if (deposit != null && deposit.getHotel() != null) {
				if (registerNewDeposit(deposit)) {
					reply.setPerformative(ACLMessage.AGREE);
					// TODO attach the hotels info!!!
				} else {
					reply.setPerformative(ACLMessage.REFUSE);
				}
			} else {
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			}
			return reply;
		}

		private boolean registerNewDeposit(MakeDeposit deposit) {
			return accountDAO.registerNewDeposit(deposit.getHotel().getHotel_name(), deposit.getMoney());
		}
	}

	private final class GetFinanceReportBehavior extends GenericServerResponseBehaviour {
		private static final long serialVersionUID = 3826752372367438517L;
		
		public GetFinanceReportBehavior(AbstractAgent agBank) {
			super(agBank,Constants.CONSULTFINANCEREPORT_PROTOCOL, ACLMessage.QUERY_REF);
		}

		
		@Override
		protected ACLMessage doSendResponse(ACLMessage msg) {
			ContentElementList data = buildFinanceReport();
			ACLMessage reply = sendResponse(msg, data);
			return reply;
		}
	
		private ACLMessage sendResponse(ACLMessage msg, ContentElementList accounts) {
			ACLMessage reply = msg.createReply();

			if (accounts != null && !accounts.isEmpty()) {
				reply.setPerformative(ACLMessage.INFORM);
				// The ContentManager transforms the java objects into strings
				try {
					myAgent.getContentManager().fillContent(reply, accounts);
				} catch (CodecException | OntologyException e) {
					e.printStackTrace();
				}				

			} else {
				reply.setPerformative(ACLMessage.REFUSE);
				reply.setContent("No hotels registered yet.");
			}
			//..there is no option of NOT UNDERSTOOD

			//Send
			return reply;
		}

		private ContentElementList buildFinanceReport() {
			ContentElementList result = new ContentElementList();
			ArrayList<Account> accounts = accountDAO.getListAccount();
			for (Account account : accounts) {
				result.add((ContentElement) toAccountStatus(account));
			}
			return result;
		}

		private AccountStatus toAccountStatus(Account account) {
			AccountStatus status = new AccountStatus();
			status.setAccount(account.getConcept());
			
			return status;
		}
	
	}
	
	@Override
	public boolean doBeforeDie() {
		return false;
	}
}
