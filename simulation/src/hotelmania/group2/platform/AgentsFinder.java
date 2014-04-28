package hotelmania.group2.platform;
import jade.content.Concept;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AgentsFinder {
	
	private static AgentsFinder INSTANCE = new AgentsFinder();
	
	private AgentsFinder() {}

	public static AgentsFinder getInstance () {
		return INSTANCE;
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
			return null;
		}
		return null;
	}
	
	public void sendRequest (Agent sender, AID receiver, Concept concept, Codec codec, Ontology ontology) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(receiver);
		msg.setLanguage(codec.getName());
		msg.setOntology(ontology.getName());
		// As it is an action and the encoding language the SL,
		// it must be wrapped into an Action
		Action agAction = new Action(receiver, concept);
		try {
			// The ContentManager transforms the java objects into strings
			sender.getContentManager().fillContent(msg, agAction);
			sender.send(msg);
			//
		} catch (CodecException ce) {
			ce.printStackTrace();
		} catch (OntologyException oe) {
			oe.printStackTrace();
		}

	}

}
