package hotelmania.group2.platform;

import jade.lang.acl.ACLMessage;


/**
 * Hotelmania logger
 * @author elts
 *
 */
public class Logger{
	private static final String SEP = Constants.LOG_SEPARATOR;
	private final MetaAgent me; 
	
	public Logger(MetaAgent owner) {
		me = owner;
	}

	// UTIL
	public static void logError(String message) {
		System.err.println("[ERROR]"+ SEP + message);
	}
	
	public static void logDebug(String string) {
		if (Constants.LOG_DEBUG) {
			System.out.println("[DEBUG]"+ SEP +string);
		}
	}
	
	//-----------------------------------------------
	// Logging of Message Reception
	//-----------------------------------------------
	
	public void logSendRequest(ACLMessage message) {
		System.out.println(me.myName()+": Send_Request "+ SEP + ACLMessage.getPerformative(message.getPerformative()) + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo()); 		
	}
	
	public void logSendReply(ACLMessage message) {
		System.out.println(me.myName()+": Send_Response "+ SEP + ACLMessage.getPerformative(message.getPerformative()) + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo()); 		
	}

	//-----------------------------------------------
	// Logging of Message Reception
	//-----------------------------------------------
	

	//General
	
	public void logReceivedMsg(ACLMessage message) {
		System.out.println(me.myName() + ": Received"+ SEP + ACLMessage.getPerformative(message.getPerformative()) + SEP + "Protocol: " + message.getProtocol() + SEP + "CID: "+message.getInReplyTo() + SEP + "From: " + message.getSender().getLocalName());		
	}
	
	//By Type
	
	public void logInformMessage(ACLMessage message) {
		System.out.println(me.myName()+": Received" + SEP + "INFORM" + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo()); 
	}

	public void logAgreeMessage(ACLMessage message) {
		System.out.println(me.myName()+": Received" + SEP + "AGREE" + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo());
	}

	public void logRefuseMessage(ACLMessage message) {
		String cause = message.getContent()==null? "unknown": message.getContent();
		System.out.println(me.myName()+": Received" + SEP + "REFUSE" + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo() + SEP + "Cause: "+cause);	
	}

	public void logNotUnderstoodMessage(ACLMessage message) {
		String cause = message.getContent()==null? "unknown": message.getContent();
		System.out.println(me.myName()+": Received" + SEP + "NOT_UNDERSTOOD" + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo() + SEP + "Cause: "+cause);
	}
	
	public void logFailureMessage(ACLMessage message) {
		String cause = message.getContent()==null? "unknown": message.getContent();
		System.out.println(me.myName()+": Received" + SEP + "FAILURE" + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo()+SEP+"Cause: "+cause);
	}


}
