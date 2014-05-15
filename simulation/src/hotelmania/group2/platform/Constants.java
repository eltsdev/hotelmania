package hotelmania.group2.platform;


public class Constants {
	
	// Protocols
	public static final String BOOKROOM_PROTOCOL = "BOOKROOM";
	public static final String RATEHOTEL_PROTOCOL = "RATEHOTEL";
	public static final String MAKEDEPOSIT_PROTOCOL = "MAKEDEPOSIT";
	public static final String SIGNCONTRACT_PROTOCOL = "SignContract";
	public static final String REGISTRATION_PROTOCOL = "Registration";
	public static final String CONSULTHOTELSINFO_PROTOCOL = "QueryHotelmaniaInformation";
	public static final String CONSULTHOTELSSTAFF_PROTOCOL = "CONSULTHOTELSSTAFF";
	public static final String CONSULTROOMPRICES_PROTOCOL = "CONSULTROOMPRICES";
	public static final String CONSULTACCOUNTSTATUS_PROTOCOL = "AccountStatus";
	public static final String CREATEACCOUNT_PROTOCOL = "CreateAccount";
	public static final String CHARGEACCOUNT_PROTOCOL = "CHARGEACCOUNT";
	public static final String SUBSCRIBETODAYEVENT_PROTOCOL = "SubscribeToDayEvent";	
	public static final String CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL = "NumberOfClients";	
	

	// Request Actions
	public static final String BOOKROOM_ACTION = BOOKROOM_PROTOCOL;
	public static final String RATEHOTEL_ACTION = RATEHOTEL_PROTOCOL;
	public static final String MAKEDEPOSIT_ACTION = MAKEDEPOSIT_PROTOCOL;
	public static final String SIGNCONTRACT_ACTION = SIGNCONTRACT_PROTOCOL;
	public static final String REGISTRATION_ACTION = REGISTRATION_PROTOCOL;
	public static final String CREATEACCOUNT_ACTION = CREATEACCOUNT_PROTOCOL;
	public static final String CHARGEACCOUNT_ACTION = CHARGEACCOUNT_PROTOCOL;
	
	// Query-ref Actions
	public static final String CONSULTHOTELSINFO_ACTION = "QueryHotelmaniaHotel";
	public static final String CONSULTHOTELSSTAFF_ACTION = CONSULTHOTELSSTAFF_PROTOCOL;
	public static final String CONSULTROOMPRICES_ACTION = CONSULTROOMPRICES_PROTOCOL;
	public static final String CONSULTACCOUNTSTATUS_ACTION = CONSULTACCOUNTSTATUS_PROTOCOL;
	public static final String CONSULTHOTELNUMBEROFCLIENTS_ACTION = CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL;
	
	// Subscribe Actions
	public static final String SUBSCRIBETODAYEVENT_ACTION = SUBSCRIBETODAYEVENT_PROTOCOL;
	
	// Performatives
	public static final String AGREE = "AGREE";
	public static final String REFUSE = "REFUSE";
	public static final String NOT_UNDERSTOOD = "NOT_UNDERSTOOD";
	public static final String INFORM = "INFORM";
	public static final String FAILURE = "FAILURE";
	
	
	// Simulation parameters
	public static final String HOTEL_NAME = "Hotel2";
	public static final int FIRST_DAY = 1;
	public static int DAY_IN_SECONDS = 5*1000;
	public static int SIMULATION_DAYS = 10;
	public static int CLIENTS_PER_DAY = 10;
	
	
}
