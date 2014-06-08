package hotelmania.group2.dao;

import jade.core.AID;

import java.util.ArrayList;

public class HotelDAO {
	private ArrayList<HotelInformation> listHotel;

	public HotelDAO() {
		listHotel = new ArrayList<HotelInformation>();
	}

	public ArrayList<HotelInformation> getHotelsRegistered() {
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
			if (listHotel.get(i).getHotel().getName().equals(name)) {
				return false;
			}

		}
		
		//register
		HotelInformation hotelRecord = new HotelInformation();
		Hotel hotel = new Hotel(name, hotelAgent);
		hotelRecord.setHotel(hotel);
		hotelRecord.setRating(5);
		listHotel.add(hotelRecord);
		return true;
	}

}
