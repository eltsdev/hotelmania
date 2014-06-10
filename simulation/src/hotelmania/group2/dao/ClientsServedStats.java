package hotelmania.group2.dao;

import java.util.HashMap;

/**
 * Class used to report the number of clients served by hotel during the simulation
 * This class is used by the reporter at the end of the simulation
 *
 */
public class ClientsServedStats {

	private final static HashMap<String, Integer> data = new HashMap<String, Integer>();
	
	synchronized public static void notifyNewService(String hotelName){
		Integer counter = data.get(hotelName);
		if (counter == null) {
			counter=0;
		}
		data.put(hotelName, counter+1);
	}
	
	
	public HashMap<String,Integer> getStats() {
		return data;
	}
}
