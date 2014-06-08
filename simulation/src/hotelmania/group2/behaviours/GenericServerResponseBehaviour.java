package hotelmania.group2.behaviours;

import hotelmania.group2.platform.AbstractAgent;
import hotelmania.group2.platform.MetaSimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public abstract class GenericServerResponseBehaviour extends MetaSimpleBehaviour {
	
	protected static final long serialVersionUID = -4878507137076376248L;
	protected AbstractAgent myAgent;
	protected String protocol;
	protected MessageTemplate messageTemplate;
	protected int incomingMessagePerformative;

	public GenericServerResponseBehaviour(AbstractAgent myAgent, String protocol, int incomingMessagePerformative) {
		super(myAgent);
		this.myAgent = myAgent;
		this.protocol = protocol;
		this.incomingMessagePerformative = incomingMessagePerformative;
		
		this.messageTemplate = MessageTemplate.and(MessageTemplate.and(MessageTemplate.and(
				MessageTemplate.MatchLanguage(myAgent.getCodec().getName()),
				MessageTemplate.MatchOntology(myAgent.getOntology().getName())),
				MessageTemplate.MatchPerformative(this.incomingMessagePerformative)),
				MessageTemplate.MatchProtocol(this.protocol));
	}
	
	@Override
	public void action() {
		ACLMessage msg = myAgent.receive(messageTemplate);
		if (msg == null) { 	
			// No message, so wait for new ones
			block();					
		}
		else {
			//A New Message
			myAgent.getLog().logReceivedMsg(msg);
			ACLMessage reply = doSendResponse(msg);
			myAgent.send(reply);
			myAgent.getLog().logSendReply(reply);
		}
	}

	/**
	 * Placeholder for sending the message to the targe agent (one or more messages to one or more agents, etc).  
	 * @param msg 
	 * @return 
	 */
	protected abstract ACLMessage doSendResponse(ACLMessage msg);
	
}
