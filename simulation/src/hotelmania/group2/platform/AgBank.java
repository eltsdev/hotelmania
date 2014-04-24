package hotelmania.group2.platform;

import hotelmania.group2.dao.AccountDAO;
import hotelmania.ontology.CreateAccount;
import hotelmania.ontology.RegistrationRequest;
import hotelmania.ontology.SharedAgentsOntology;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;

public class AgBank extends Agent {
	private static final long serialVersionUID = 2893904717857535232L;
	static final String CREATEACCOUNT = "CREATEACCOUNT";

	private AccountDAO accountDao;

	/*
	 * Codec for the SL language used
	 */
	private Codec codec = new SLCodec();

	/*
	 * External communication protocol's ontology
	 */
	private Ontology ontology = SharedAgentsOntology.getInstance();

	/*
	 * Agent Attributes
	 */

	String name;
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
		System.out.println(getLocalName() + ": HAS ENTERED");

		accountDao = new AccountDAO();
		/*
		 * List of Accounts
		 */
		listHotelAccount = new ArrayList<CreateAccount>();
		/*
		 * Register of codec and ontology in the ContentManager
		 */
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);

		/*
		 * Creates its own description
		 */
		DFAgentDescription dfDescription = new DFAgentDescription();
		ServiceDescription createService = new ServiceDescription();
		createService.setName(this.getName());
		createService.setType(CREATEACCOUNT);
		dfDescription.addServices(createService);

		try {
			// Registers its description in the DF
			DFService.register(this, dfDescription);
			System.out.println(getLocalName() + ": registered in the DF");
			dfDescription = null;
			createService = null;
			doWait(10000);

		} catch (FIPAException e) {
			// TODO handle
			e.printStackTrace();
		}

		// Create hotel account
		addBehaviour(new CreateAccountBehavior(this));

		// Charge Staff services in hotel account
		addBehaviour(new ChargeAccountBehavior(this));

		// Used by clients to pay to hotels
		addBehaviour(new MakeDepositBehavior(this));

		// Provide info account to hotel
		addBehaviour(new ProvideHotelAccountInfoBehavior(this));

	}

	// --------------------------------------------------------
	// BEHAVIOURS
	// --------------------------------------------------------

	/**
	 * @author user
	 *
	 */
	private final class CreateAccountBehavior extends SimpleBehaviour {
		private static final long serialVersionUID = 7390814510706022198L;
		private static final int VALID_REQ = 0;
		private static final int REJECT_REQ = -1;
		private static final int NOT_UNDERSTOOD_REQ = 1;

		/**
		 * @param agBankWithOntology
		 */
		public CreateAccountBehavior(AgBank agBankWithOntology) {
			super(agBankWithOntology);
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
					if (conc instanceof CreateAccount) {
						// execute request
						int answer = createAccount(msg, (CreateAccount) conc);

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**
		 * @param msg
		 * @param accountData
		 * @return
		 */
		private int createAccount(ACLMessage msg, CreateAccount accountData) {
			System.out.println(myAgent.getLocalName()
					+ ": received Account Request from "
					+ (msg.getSender()).getLocalName());

			if (accountData != null && accountData.getHotel() != null) {
				if (registerNewAccount(accountData)) {
					return VALID_REQ;
				} else {
					return REJECT_REQ;
				}
			} else {
				return NOT_UNDERSTOOD_REQ;

			}
		}

		/**
		 * @param account
		 * @return
		 */
		private boolean registerNewAccount(CreateAccount account) {
			return accountDao.registerNewAccount(account.getHotel()
					.getHotel_name(), account.getBalance());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see jade.core.behaviours.Behaviour#done()
		 */
		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	/**
	 * @author user
	 *
	 */
	private final class ProvideHotelAccountInfoBehavior extends CyclicBehaviour {
		private static final long serialVersionUID = -4414753731149819352L;

		public ProvideHotelAccountInfoBehavior(AgBank agBankWithOntology) {
			super(agBankWithOntology);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void action() {
			// TODO Auto-generated method stub

		}

	}

	private final class ChargeAccountBehavior extends CyclicBehaviour {
		private static final long serialVersionUID = 5591566038041266929L;

		public ChargeAccountBehavior(AgBank agBank) {
			super(agBank);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void action() {
			// TODO Auto-generated method stub

		}

	}

	private final class MakeDepositBehavior extends CyclicBehaviour {
		private static final long serialVersionUID = 5591566038041266929L;

		public MakeDepositBehavior(AgBank agBankWithOntology) {
			super(agBankWithOntology);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void action() {
			// TODO Auto-generated method stub

		}

	}

}