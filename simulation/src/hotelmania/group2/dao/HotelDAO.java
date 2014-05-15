package hotelmania.group2.dao;

import jade.core.AID;

import java.util.ArrayList;

public class HotelDAO {
	private ArrayList<Hotel> listHotel;

	public HotelDAO() {
		listHotel = new ArrayList<Hotel>();
	}

	public ArrayList<Hotel> getHotelsRegistered() {
		return listHotel;
	}
	
	/**
	 * @param hotelAgent 
	 * @param hotel_name
	 * @return
	 */
	public boolean registerNewHotel(String name, AID hotelAgent) {
		//look for it to avoid repetitions
		for (int i = 0; i < listHotel.size(); i++) {
			if (listHotel.get(i).getName().equals(name)) {
				return false;
			}

		}
		
		//register
		listHotel.add(new Hotel(name, hotelAgent));
		return true;
	}

}
