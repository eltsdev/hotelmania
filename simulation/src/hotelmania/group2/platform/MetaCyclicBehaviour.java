package hotelmania.group2.platform;

import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class MetaCyclicBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = 1L;
	
	protected String log = "";

	public MetaCyclicBehaviour(Agent a) {
		super(a);
	}

	@Override
	public void action() { }
	
	public Concept getConceptFromMessage(ACLMessage msg) {
		ContentElement ce;
		try {
			ce = myAgent.getContentManager().extractContent(msg);
			if (ce instanceof Action) {
				Action agAction = (Action) ce;
				Concept conc = agAction.getAction();
				return conc;
			}
			System.out.println("getConceptFromMessage = null : No content extracted because it is not an action");
		} catch (CodecException | OntologyException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
