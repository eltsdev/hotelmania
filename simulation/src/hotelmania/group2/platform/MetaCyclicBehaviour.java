package hotelmania.group2.platform;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class MetaCyclicBehaviour extends CyclicBehaviour {
	private static final long serialVersionUID = 1L;
	
	protected String log = "";

	public MetaCyclicBehaviour(Agent a) {
		super(a);
	}

	/*public ACLMessage treatReply (ACLMessage reply, String log, int answer) {
		switch (answer) {
		case Constants.VALID_REQ:
			reply.setPerformative(ACLMessage.AGREE); 
			this.log = "AGREE";
			break;

		case Constants.REJECT_REQ:
			reply.setPerformative(ACLMessage.REFUSE); 
			this.log = "REFUSE";
			break;

		case Constants.NOT_UNDERSTOOD_REQ:
		default:
			reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			this.log = "NOT_UNDERSTOOD";
			break;
		}
		return reply;
	}*/

	
		
	@Override
	public void action() {
		// TODO Auto-generated method stub
		
	}
}
