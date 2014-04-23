package emse.hotelmania.dao;

import java.util.ArrayList;

/**
 * @author user
 */
public class HotelDAO {
	private ArrayList<Hotel> listHotel;

	public HotelDAO() {
		listHotel = new ArrayList<Hotel>();

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
