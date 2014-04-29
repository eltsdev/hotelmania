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
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

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

}
