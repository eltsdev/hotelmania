package hotelmania.group2.behaviours;

import hotelmania.group2.platform.AbstractAgent;
import hotelmania.group2.platform.MetaSimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public abstract class GenericSendReceiveBehaviour extends MetaSimpleBehaviour {
	
	protected static final long serialVersionUID = -4878507137076376248L;
	protected ClientStep step;
	protected AbstractAgent myAgent;
	protected String protocol;
	protected MessageTemplate messageTemplate;
	int MAX_RESPONSES = 0 ;

	public GenericSendReceiveBehaviour(AbstractAgent myAgent, String protocol) {
		super(myAgent);
		this.myAgent = myAgent;
		this.protocol = protocol;
		
		this.messageTemplate = MessageTemplate.and(MessageTemplate.and(
				MessageTemplate.MatchLanguage(myAgent.getCodec().getName()),
				MessageTemplate.MatchOntology(myAgent.getOntology().getName())),
				MessageTemplate.MatchProtocol(this.protocol));
		
		this.step = ClientStep.PREPARE;
	}
	
	@Override
	public void action() {
		
	switch (step) {
		case PREPARE:
			boolean _continue = doPrepare();
			if (_continue) {
				step = ClientStep.SEND;
			}
		break;
				
		case SEND:
			doSend();
			step = ClientStep.RECEIVE_RESPONSES;
			break;
			
		case RECEIVE_RESPONSES:

			ACLMessage msg = myAgent.receive(messageTemplate);

			if (msg == null) { 	
				// No message, so wait for new ones
				block();					
			}
			else {
				//A New Message
				myAgent.getLog().logReceivedMsg(msg);
				
				
				switch (msg.getPerformative()) {

				case ACLMessage.AGREE:
					receiveAgree(msg);
					break;
				
				case ACLMessage.INFORM:
					//inform++;
					receiveInform(msg);
					break;

				case ACLMessage.REFUSE:
					receiveRefuse(msg);
					break;

				case ACLMessage.FAILURE:
					receiveFailure(msg);
					break;					
				
				case ACLMessage.NOT_UNDERSTOOD:
					receiveNotUnderstood(msg);
					break;
				
				default:
					//ignore
					break;
				}

				if (finishOrResend(msg.getPerformative())) {
					step = ClientStep.DONE;
				}
			}
			break;
		
		case DONE:
		default:
			setDone(true);
			break;
		}
	}
	
	/**
	 * Termination condition
	 * @param performativeReceived 
	 * @return condition if true, the behavior finishes. If false, it reset the behavior to send the message again.
	 */
	protected abstract boolean finishOrResend(int performativeReceived);

	/**
	 * Placeholder for locating the target agent, and so on. 
	 * @return 
	 */
	protected boolean doPrepare() {
		return true;
	}

	/**
	 * Placeholder for sending the message to the target agent (one or more messages to one or more agents, etc).  
	 * @return 
	 */
	protected abstract void doSend();
	
	/**
	 * Placeholder for business-logic
	 * @param msg

	 */
	protected void receiveAgree(ACLMessage msg) {

	}
	/**
	 * Placeholder for business-logic
	 * @param msg

	 */
	protected void receiveInform(ACLMessage msg) {

	}
	/**
	 * Placeholder for business-logic
	 * @param msg

	 */
	protected void receiveNotUnderstood(ACLMessage msg) {

	}
	/**
	 * Placeholder for business-logic
	 * @param msg

	 */
	protected void receiveFailure(ACLMessage msg) {

	}
	/**
	 * Placeholder for business-logic
	 * @param msg

	 */
	protected void receiveRefuse(ACLMessage msg) {

	}	
}
