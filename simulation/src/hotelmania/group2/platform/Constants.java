package hotelmania.group2.platform;

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
	

	// Request Actions
	public static final String BOOKROOM_ACTION = "BOOKROOM";
	public static final String RATEHOTEL_ACTION = "RATEHOTEL";
	public static final String MAKEDEPOSIT_ACTION = "MAKEDEPOSIT";
	public static final String SIGNCONTRACT_ACTION = "SIGNCONTRACT_REQUEST";
	public static final String REGISTRATION_ACTION = "Registration";
	
	
	// Query-ref Actions
	public static final String CONSULTHOTELSINFO_ACTION = "CONSULTHOTELSINFO";
	public static final String CONSULTHOTELSSTAFF_ACTION = "CONSULTHOTELSSTAFF";
	public static final String CONSULTROOMPRICES_ACTION = "CONSULTROOMPRICES";
	public static final String CONSULTACCOUNTSTATUS_ACTION = "CONSULTACCOUNTSTATUS";
	
	// Subscribe Actions
	
	// constants
	public static final int VALID_REQ = 0;
	public static final int REJECT_REQ = -1;
	public static final int NOT_UNDERSTOOD_REQ = 1;
	
}
