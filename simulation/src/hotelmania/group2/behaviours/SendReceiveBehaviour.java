package hotelmania.group2.behaviours;

import hotelmania.group2.platform.AbstractAgent;
import hotelmania.group2.platform.Constants;
import jade.core.AID;


public abstract class SendReceiveBehaviour extends GenericSendReceiveBehaviour {
	
	protected static final long serialVersionUID = -4878507137076376248L;
	protected AID server;
	private String serviceToLookUp;
	protected int sendPerformative;

	public SendReceiveBehaviour(AbstractAgent myAgent, String protocol, String serviceToLookUp, int sendPerformative) {
		super(myAgent,protocol);
		this.myAgent = myAgent;
		this.serviceToLookUp = serviceToLookUp;
		this.sendPerformative = sendPerformative;
	}
	
	@Override
	protected boolean doPrepare() {
		if (server == null) {
			server = myAgent.locateAgent(serviceToLookUp, myAgent);
			return false;
		}
		return true;
	}
	
	@Override
	protected void doSend() {
			myAgent.sendRequest(server, Constants.CONSULTHOTELSINFO_PROTOCOL, sendPerformative);
	}
}
