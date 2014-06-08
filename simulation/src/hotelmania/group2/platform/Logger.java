package hotelmania.group2.platform;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Iterator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


/**
 * Hotelmania logger
 * @author elts
 *
 */
public class Logger{
	static StringBuffer output = new StringBuffer();
	
	private static final String SEP = Constants.LOG_SEPARATOR;
	private final IMyName me; 
	
	public Logger(IMyName owner) {
		me = owner;
	}

	// UTIL
	public static void logError(String message) {
		String out = ("[ERROR]"+ SEP + message);
		System.err.println(out);
		output.append(out + "\n");
	}
	
	public static void logDebug(String string) {
		if (Constants.LOG_DEBUG) {
			String out = ("[DEBUG]"+ SEP +string);
			System.out.println(out);
			output.append(out + "\n");
		}
	}
	
	//-----------------------------------------------
	// Logging of Message Reception
	//-----------------------------------------------
	
	public void logSendRequest(ACLMessage message) {
		String receivers = getReceiver(message);
		String out = ("[INFO]" + SEP + me.myName()+ SEP + "Send_Request "+ SEP + ACLMessage.getPerformative(message.getPerformative()) + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo() + SEP + "TO:" + receivers);
		System.out.println(out);
		output.append(out + "\n");
	}

	
	public void logSendReply(ACLMessage message) {
		String receivers = getReceiver(message);
		String out = ("[INFO]"+ SEP + me.myName() + SEP +"Send_Response "+ SEP + ACLMessage.getPerformative(message.getPerformative()) + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo()+ SEP + "TO:" + receivers);
		System.out.println(out);
		output.append(out + "\n");
	}
	
	private String getReceiver(ACLMessage message) {
		String receivers = "";
		Iterator aids = message.getAllReceiver();
		while (aids.hasNext()) {
			AID receiver = (AID) aids.next();
			receivers = receivers + receiver.getLocalName() + " ";
		}
		return receivers;
	}


	//-----------------------------------------------
	// Logging of Message Reception
	//-----------------------------------------------
	

	//General
	
	public void logReceivedMsg(ACLMessage message) {
		String out = ("[INFO]"+ SEP + me.myName() + SEP  + "Received"+ SEP + ACLMessage.getPerformative(message.getPerformative()) + SEP + "Protocol: " + message.getProtocol() + SEP + "CID: "+message.getInReplyTo() + SEP + "From: " + message.getSender().getLocalName());
		System.out.println(out);
		output.append(out + "\n");
	}
	
	//By Type
	
	public void logInformMessage(ACLMessage message) {
		String out = ("[INFO]"+ SEP + me.myName() + SEP +"Received" + SEP + "INFORM" + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo());
		System.out.println(out);
		output.append(out + "\n");
	}

	public void logAgreeMessage(ACLMessage message) {
		String out = ("[INFO]"+ SEP + me.myName() + SEP +"Received" + SEP + "AGREE" + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo());
		System.out.println(out);
		output.append(out + "\n");
	}

	public void logRefuseMessage(ACLMessage message) {
		String cause = message.getContent()==null? "unknown": message.getContent();
		String out = ("[INFO]"+ SEP + me.myName() + SEP +"Received" + SEP + "REFUSE" + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo() + SEP + "Cause: "+cause);
		System.out.println(out);
		output.append(out + "\n");
	}

	public void logNotUnderstoodMessage(ACLMessage message) {
		String cause = message.getContent()==null? "unknown": message.getContent();
		String out = ("[INFO]"+ SEP + me.myName() + SEP +"Received" + SEP + "NOT_UNDERSTOOD" + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo() + SEP + "Cause: "+cause);
		System.out.println(out);
		output.append(out + "\n");
	}
	
	public void logFailureMessage(ACLMessage message) {
		String cause = message.getContent()==null? "unknown": message.getContent();
		String out = ("[INFO]"+ SEP + me.myName() + SEP +"Received" + SEP + "FAILURE" + SEP + "Protocol: "+message.getProtocol() + SEP + "CID: "+message.getInReplyTo()+SEP+"Cause: "+cause);
		System.out.println(out);
		output.append(out + "\n");
	}

	public static void printFile() {
		printToFile(output.toString(), "log.txt");
	}
	
	private static void printToFile(String data, String fileName) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(fileName, "UTF-8");
			writer.println(data);
			writer.close();	
			Logger.logDebug("SIMULATION REPORT GENERATED: "+fileName);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			Logger.logDebug("SIMULATION REPORT FAILED TO WRITE IN: "+fileName);

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
