package hotelmania.group2.dao;

import hotelmania.group2.platform.Constants;
import hotelmania.ontology.BookingOffer;
import hotelmania.ontology.Stay;

import java.util.ArrayList;

public class BookingDAO {
	private ArrayList<Booking> booking = new ArrayList<Booking>();
	private int[] daysClientsArray;
	
	
	
	public BookingDAO() {
		this.daysClientsArray = new int[Constants.SIMULATION_DAYS];
	}

	public void booking(int days, String startDate){
		this.booking.add(new Booking(days, startDate));
		for (int i = Integer.valueOf(startDate); i < days; i++) {
			this.daysClientsArray[i]++;
		}
	}
	
	public int getClientsAtDay(int day) {
		if (day > 0 && day < Constants.SIMULATION_DAYS) {
			return this.daysClientsArray[day-1];
		}
		return -1;
	}

	/**
	 * @param bookingOffer
	 * @param stay
	 */
	
	//TODO This Method for Provide Info Booking
	public void booking(BookingOffer bookingOffer, Stay stay) {
//		this.booking.add(new Booking(bookingOffer, startDate));
//		for (int i = Integer.valueOf(startDate); i < days; i++) {
//			this.daysClientsArray[i]++;
//		}
//		
	}
}
