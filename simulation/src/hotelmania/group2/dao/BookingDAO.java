/**
 * 
 */
package hotelmania.group2.dao;

import java.util.ArrayList;

/**
 * @author user
 *
 */
public class BookingDAO {
	private static BookingDAO instance;
	private ArrayList<Booking> booking;

	private BookingDAO() {
		booking = new ArrayList<Booking>();
	}

	public static BookingDAO getInstance() {
		if (instance == null) {
			instance = new BookingDAO();
		}
		return instance;
	}

	
	public void booking(int days, String startDate){
		booking.add(new Booking(days, startDate));
	}
}
