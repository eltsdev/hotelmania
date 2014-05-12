package hotelmania.group2.dao;

import hotelmania.group2.platform.Constants;

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
}
