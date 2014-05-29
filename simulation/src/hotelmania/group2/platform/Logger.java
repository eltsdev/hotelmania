package hotelmania.group2.platform;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Iterator;


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
		String receivers = "";
		Iterator aids = message.getAllReceiver();
		while (aids.hasNext()) {
			AID receiver = (AID) aids.next();
			receivers = receivers + receiver.getLocalName() + " ";
		}
		System.out.println("[INFO]" + SEP + me.myName()+ SEP + "Send_Request "+ SEP + ACLMessage.getPerformative(message.getPerformative()) + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo() + SEP + "TO:" + receivers); 		
	}
	
	public void logSendReply(ACLMessage message) {
		System.out.println("[INFO]"+ SEP + me.myName() + SEP +"Send_Response "+ SEP + ACLMessage.getPerformative(message.getPerformative()) + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo()); 		
	}

	//-----------------------------------------------
	// Logging of Message Reception
	//-----------------------------------------------
	

	//General
	
	public void logReceivedMsg(ACLMessage message) {
		System.out.println("[INFO]"+ SEP + me.myName() + SEP  + "Received"+ SEP + ACLMessage.getPerformative(message.getPerformative()) + SEP + "Protocol: " + message.getProtocol() + SEP + "CID: "+message.getInReplyTo() + SEP + "From: " + message.getSender().getLocalName());		
	}
	
	//By Type
	
	public void logInformMessage(ACLMessage message) {
		System.out.println("[INFO]"+ SEP + me.myName() + SEP +"Received" + SEP + "INFORM" + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo()); 
	}

	public void logAgreeMessage(ACLMessage message) {
		System.out.println("[INFO]"+ SEP + me.myName() + SEP +"Received" + SEP + "AGREE" + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo());
	}

	public void logRefuseMessage(ACLMessage message) {
		String cause = message.getContent()==null? "unknown": message.getContent();
		System.out.println("[INFO]"+ SEP + me.myName() + SEP +"Received" + SEP + "REFUSE" + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo() + SEP + "Cause: "+cause);	
	}

	public void logNotUnderstoodMessage(ACLMessage message) {
		String cause = message.getContent()==null? "unknown": message.getContent();
		System.out.println("[INFO]"+ SEP + me.myName() + SEP +"Received" + SEP + "NOT_UNDERSTOOD" + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo() + SEP + "Cause: "+cause);
	}
	
	public void logFailureMessage(ACLMessage message) {
		String cause = message.getContent()==null? "unknown": message.getContent();
		System.out.println("[INFO]"+ SEP + me.myName() + SEP +"Received" + SEP + "FAILURE" + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo()+SEP+"Cause: "+cause);
	}


}
