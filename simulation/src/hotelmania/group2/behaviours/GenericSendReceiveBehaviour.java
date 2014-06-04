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
				
				boolean finish = true;
				
				switch (msg.getPerformative()) {

				case ACLMessage.AGREE:
					finish = receiveAgree(msg);
					break;
				
				case ACLMessage.INFORM:
					//inform++;
					finish = receiveInform(msg);
					break;

				case ACLMessage.REFUSE:
					finish = receiveRefuse(msg);
					break;

				case ACLMessage.FAILURE:
					finish = receiveFailure(msg);
					break;					
				
				case ACLMessage.NOT_UNDERSTOOD:
					finish = receiveNotUnderstood(msg);
					break;
				
				default:
					//ignore
					break;
				}
				
				if (finish) {
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
	 * Placeholder for locating the target agent, and so on. 
	 * @return 
	 */
	protected boolean doPrepare() {
		return true;
	}

	/**
	 * Placeholder for sending the message to the targe agent (one or more messages to one or more agents, etc).  
	 * @return 
	 */
	protected abstract void doSend();
	
	/**
	 * Placeholder for business-logic
	 * @param msg
	 * @return retry if true the agent will finish the behavior. If false, it will keep receiving additional messages.
	 */
	protected boolean receiveAgree(ACLMessage msg) {
		return false;
	}
	/**
	 * Placeholder for business-logic
	 * @param msg
	 * @return retry if true the agent will finish the behavior. If false, it will keep receiving additional messages.
	 */
	protected boolean receiveInform(ACLMessage msg) {
		return false;
	}
	/**
	 * Placeholder for business-logic
	 * @param msg
	 * @return retry if true the agent will finish the behavior. If false, it will keep receiving additional messages.
	 */
	protected boolean receiveNotUnderstood(ACLMessage msg) {
		return false;
	}
	/**
	 * Placeholder for business-logic
	 * @param msg
	 * @return retry if true the agent will finish the behavior. If false, it will keep receiving additional messages.
	 */
	protected boolean receiveFailure(ACLMessage msg) {
		return false;
	}
	/**
	 * Placeholder for business-logic
	 * @param msg
	 * @return retry if true the agent will finish the behavior. If false, it will keep receiving additional messages.
	 */
	protected boolean receiveRefuse(ACLMessage msg) {
		return true;
	}	
}
