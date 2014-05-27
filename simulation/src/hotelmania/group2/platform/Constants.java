package hotelmania.group2.platform;


public class Constants {
	// Protocols
	public static final String BOOKROOM_PROTOCOL = "BookARoom";
	public static final String RATEHOTEL_PROTOCOL = "RATEHOTEL";
	public static final String MAKEDEPOSIT_PROTOCOL = "MAKEDEPOSIT";
	public static final String SIGNCONTRACT_PROTOCOL = "SignContract";
	public static final String REGISTRATION_PROTOCOL = "Registration";
	public static final String CONSULTHOTELSINFO_PROTOCOL = "QueryHotelmaniaInformation";
	public static final String CONSULTHOTELSSTAFF_PROTOCOL = "CONSULTHOTELSSTAFF";
	public static final String CONSULTROOMPRICES_PROTOCOL = "BookingOffer";
	public static final String CONSULTACCOUNTSTATUS_PROTOCOL = "AccountStatus";
	public static final String CREATEACCOUNT_PROTOCOL = "CreateAccount";
	public static final String CHARGEACCOUNT_PROTOCOL = "CHARGEACCOUNT";
	public static final String SUBSCRIBETODAYEVENT_PROTOCOL = "SubscribeToDayEvent";	
	public static final String CONSULTHOTELNUMBEROFCLIENTS_PROTOCOL = "NumberOfClients";	
	public static final String END_SIMULATION_PROTOCOL = "EndSimulation";
	public static final String CONSULTFINANCEREPORT_PROTOCOL = "CONSULTFINANCEREPORT";

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
	public static final String CONSULTFINANCEREPORT_ACTION = CONSULTFINANCEREPORT_PROTOCOL;
	
	// Subscribe Actions
	public static final String SUBSCRIBETODAYEVENT_ACTION = SUBSCRIBETODAYEVENT_PROTOCOL;
	public static final String END_SIMULATION_ACTION = END_SIMULATION_PROTOCOL;
	
	// Simulation parameters
	public static final int FIRST_DAY = 1;
	public static final int ROOMS_PER_HOTEL = 6;
	public static int DAY_IN_MILLISECONDS;
	public static int SIMULATION_DAYS;
	public static int CLIENTS_PER_DAY;
	public static double CLIENTS_BUDGET;
	public static double CLIENTS_BUDGET_VARIANCE;
	public static int SIMULATION_TIME_TO_START;
	
	// Private settings
	public static final String HOTEL_NAME = "Hotel2";
	public static final String LOG_SEPARATOR = "\t";
	public static String REPORT_FILE;
	public static boolean LOG_DEBUG;

	// Current day during simulation
	public static int DAY = FIRST_DAY-1;

}
