package hotelmania.group2.platform;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class MetaCyclicBehaviour extends CyclicBehaviour {
	private static final long serialVersionUID = 1L;

	public MetaCyclicBehaviour(Agent a) {
		super(a);
	}
	
	public void treatReply (ACLMessage reply, String log, int answer) {
		switch (answer) {
		case Constants.VALID_REQ:
			reply.setPerformative(ACLMessage.AGREE); //TODO ACLMessage.AGREE);
			log = "AGREE";
			break;

		case Constants.REJECT_REQ:
			reply.setPerformative(ACLMessage.REFUSE); //TODO ACLMessage.REFUSE);
			log = "REFUSE";
			break;

		case Constants.NOT_UNDERSTOOD_REQ:
		default:
			reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			log = "NOT_UNDERSTOOD";
			break;
		}
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub
		
	}
}
