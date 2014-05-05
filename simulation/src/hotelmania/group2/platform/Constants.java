package hotelmania.group2.platform;

import jade.lang.acl.ACLMessage;

public class Constants {
	
	// Protocols
	public static final String BOOKROOM_PROTOCOL = "BOOKROOM";
	public static final String RATEHOTEL_PROTOCOL = "RATEHOTEL";
	public static final String MAKEDEPOSIT_PROTOCOL = "MAKEDEPOSIT";
	public static final String CONSULTHOTELSINFO_PROTOCOL = "CONSULTHOTELSINFO";
	public static final String CONSULTHOTELSSTAFF_PROTOCOL = "CONSULTHOTELSSTAFF";
	public static final String CONSULTROOMPRICES_PROTOCOL = "CONSULTROOMPRICES";
	public static final String CONSULTACCOUNTSTATUS_PROTOCOL = "CONSULTACCOUNTSTATUS";
	public static final String REGISTRATION_PROTOCOL = "Registration";
	public static final String CREATEACCOUNT_PROTOCOL = "CREATEACCOUNT";
	public static final String CHARGEACCOUNT_PROTOCOL = "CHARGEACCOUNT";
	public static final String SIGNCONTRACT_PROTOCOL = "SIGNCONTRACT";
	public static final String SUBSCRIBETODAYEVENT_PROTOCOL = "SUBSCRIBETODAYEVENT";	
	

	// Request Actions
	public static final String BOOKROOM_ACTION = "BOOKROOM";
	public static final String RATEHOTEL_ACTION = "RATEHOTEL";
	public static final String MAKEDEPOSIT_ACTION = "MAKEDEPOSIT";
	public static final String SIGNCONTRACT_ACTION = "SIGNCONTRACT";
	public static final String REGISTRATION_ACTION = "Registration";
	public static final String CREATEACCOUNT_ACTION = "CREATEACCOUNT";
	public static final String CHARGEACCOUNT_ACTION = "CHARGEACCOUNT";
	
	// Query-ref Actions
	public static final String CONSULTHOTELSINFO_ACTION = "CONSULTHOTELSINFO";
	public static final String CONSULTHOTELSSTAFF_ACTION = "CONSULTHOTELSSTAFF";
	public static final String CONSULTROOMPRICES_ACTION = "CONSULTROOMPRICES";
	public static final String CONSULTACCOUNTSTATUS_ACTION = "CONSULTACCOUNTSTATUS";
	
	// Subscribe Actions
	public static final String SUBSCRIBETODAYEVENT_ACTION = "SUBSCRIBETODAYEVENT";
	
	// constants
	public static final String AGREE = "AGREE";
	public static final String REFUSE = "REFUSE";
	public static final String NOT_UNDERSTOOD = "NOT_UNDERSTOOD";
	
	
	public static final int FIRST_DAY = 1;
	public static final String NEW_DAY_TOPIC = "newDay";
	public static int DAY_IN_SECONDS = 5*1000;
}
