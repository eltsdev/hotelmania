package hotelmania.group2.platform;

import hotelmania.ontology.SharedAgentsOntology;
import jade.content.Concept;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class MetaAgent extends Agent {

	private static final long serialVersionUID = 3898945377957867754L;
	

	// Codec for the SL language used
	protected Codec codec = new SLCodec();

	// External communication protocol's ontology
	protected Ontology ontology = SharedAgentsOntology.getInstance();

	@Override
	protected void setup() {
		super.setup();
		
		System.out.println(getLocalName() + ": HAS ENTERED");

		// Register codec and ontology in ContentManager
		getContentManager().registerLanguage(this.codec);
		getContentManager().registerOntology(this.ontology);
		
		addBehaviour(new ReceiveAcceptanceMsgBehavior(this));
		addBehaviour(new ReceiveRejectionMsgBehavior(this));
		addBehaviour(new ReceiveNotUnderstoodMsgBehavior(this));
	}
 
	
	public AID locateAgent(String type, Agent myAgent) {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		dfd.addServices(sd);

		try {
			// It finds agents of the required type
			DFAgentDescription[] agents = DFService.search(myAgent, dfd);

			if (agents != null && agents.length > 0) {

				for (DFAgentDescription description : agents) {
					return description.getName(); // only expects 1
					// agent...
				}
			}
		} catch (Exception e) {
			//TODO
			e.printStackTrace();
		}
		return null;
	}

	public void sendRequest(Agent sender, AID receiver, Concept concept,
			Codec codec, Ontology ontology, String protocol, int messageType) {
		ACLMessage msg = new ACLMessage(messageType);
		msg.addReceiver(receiver);
		msg.setLanguage(codec.getName());
		msg.setOntology(ontology.getName());
		msg.setProtocol(protocol);

		// As it is an action and the encoding language the SL,
		// it must be wrapped into an Action
		Action agAction = new Action(receiver, concept);
		try {
			// The ContentManager transforms the java objects into strings
			sender.getContentManager().fillContent(msg, agAction);
			sender.send(msg);
		} catch (CodecException ce) {
			ce.printStackTrace();
		} catch (OntologyException oe) {
			oe.printStackTrace();
		}

	}
	
	public void registerServices(String...services) {
		DFAgentDescription dfd = new DFAgentDescription();
		for (int i = 0; i < services.length; i++) {
			ServiceDescription registrationService = new ServiceDescription();
			registrationService.setName(this.getName());
			registrationService.setType(services[i]);
			dfd.addServices(registrationService);
		}

		try {	
			// Registers its description in the DF
			DFService.register(this, dfd);
			System.out.println(getLocalName() + ": registered in the DF");
			dfd = null;
			
			// TODO handle
			doWait(10000);

		} catch (FIPAException e) {
			// TODO handle
			e.printStackTrace();
		}
	}
	
	private final class ReceiveAcceptanceMsgBehavior extends CyclicBehaviour {
		private static final long serialVersionUID = -4878774871721189228L;

		private ReceiveAcceptanceMsgBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// Waits for acceptance messages
			ACLMessage msg = receive(MessageTemplate
					.MatchPerformative(ACLMessage.AGREE));

			if (msg != null) {
				// If an acceptance arrives...
				String request = "*Request*" ;
				System.out.println(myAgent.getLocalName()
						+ ": received "+request +" acceptance from "
						+ (msg.getSender()).getLocalName());
				receivedAcceptance(msg);
			} else {
				// If no message arrives
				block();
			}

		}
	}

	private final class ReceiveRejectionMsgBehavior extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		private ReceiveRejectionMsgBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// Waits for rejection message
			ACLMessage msg = receive(MessageTemplate
					.MatchPerformative(ACLMessage.REFUSE));

			if (msg != null) {
				// If a rejection arrives...
				System.out.println(myAgent.getLocalName()
						+ ": received work rejection from "
						+ (msg.getSender()).getLocalName());
				receivedReject(msg);
			} else {
				// If no message arrives
				block();
			}

		}
	}

	private final class ReceiveNotUnderstoodMsgBehavior extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		private ReceiveNotUnderstoodMsgBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// Waits for estimations not understood
			ACLMessage msg = receive(MessageTemplate
					.MatchPerformative(ACLMessage.NOT_UNDERSTOOD));
			if (msg != null) {
				// If a not understood message arrives...
				System.out.println(myAgent.getLocalName()
						+ ": received NOT_UNDERSTOOD from "
						+ (msg.getSender()).getLocalName());
				receivedNotUnderstood(msg);
			} else {
				// If no message arrives
				block();
			}

		}
	}
	
	public void receivedAcceptance(ACLMessage message) {
		
	}
	
	public void receivedReject(ACLMessage message) {
		
	}
	
	public void receivedNotUnderstood(ACLMessage message) {
		
	}
	
	

}
