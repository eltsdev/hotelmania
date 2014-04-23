/**
 * 
 */
package emse.hotelmania.dao;

import java.util.ArrayList;

/**
 * @author user
 *
 */
public class BookingDAO {
	private ArrayList<Booking> booking;
	
	/**
	 * 
	 */
	public BookingDAO() {
		booking = new ArrayList<Booking>();
	}
	
	public void booking(int days, String startDate){
		booking.add(new Booking(days, startDate));
	}

}
