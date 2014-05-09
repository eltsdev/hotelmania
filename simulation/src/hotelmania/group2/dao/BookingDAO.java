package hotelmania.group2.dao;

import java.util.ArrayList;

public class BookingDAO {
	private ArrayList<Booking> booking = new ArrayList<Booking>();

	public void booking(int days, String startDate){
		booking.add(new Booking(days, startDate));
	}
}
