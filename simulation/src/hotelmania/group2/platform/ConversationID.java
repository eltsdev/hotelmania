package hotelmania.group2.platform;

public class ConversationID {
	
	private static int conversationID = 1000;
	
	public synchronized static String newCID() {
		return "" + conversationID++ ;
	}
}
