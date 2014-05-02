package hotelmania.group2.dao;

import java.util.ArrayList;

/**
 * @author user
 */
public class HotelDAO {
	private static HotelDAO instance;
	
	private ArrayList<Hotel> listHotel;

	private HotelDAO() {
		listHotel = new ArrayList<Hotel>();
	}

	public static HotelDAO getInstance() {

		if (instance==null) {
			instance = new HotelDAO();

		}
		return instance;
	}
	
	/**
	 * @param hotel_name
	 * @return
	 */
	public boolean registerNewHotel(String name) {
		for (int i = 0; i < listHotel.size(); i++) {
			if (listHotel.get(i).getName().equals(name)) {
				return false;
			}

		}
		listHotel.add(new Hotel(name));
		return true;
	}

}
