package hotelmania.group2.dao;

import hotelmania.group2.platform.Constants;


import java.util.ArrayList;

public class BookingDAO {
	private ArrayList<Booking> booking = new ArrayList<Booking>();
	private int[] daysClientsArray;
	private int actualPrice;
	private int room_available;

	public BookingDAO() {
		this.daysClientsArray = new int[Constants.SIMULATION_DAYS];
		room_available = Constants.ROOMS_PER_HOTEL;
	}

	public boolean booking(hotelmania.ontology.Stay stay, hotelmania.ontology.Price price) {
		
		Stay stay2 = new Stay();
		stay2.setCheckIn(stay.getCheckIn());
		stay2.setCheckOut(stay.getCheckOut());
		
		Price price2 = new Price();
		price2.setPrice(price.getPrice());
	
	
		if(room_available<Constants.ROOMS_PER_HOTEL && actualPrice==price.getPrice()){
			this.booking.add(new Booking(stay2,price2));
			room_available--;
			setNewPrice();
			return true;
		}
		return false;
		
		

		
	}
	
	/**
	 * Configure new Price according to the rooms available for today
	 */
	private void setNewPrice() {
		this.actualPrice= this.actualPrice + (Constants.ROOMS_PER_HOTEL-(this.room_available-1))*50;
		
	}

		


	public int getClientsAtDay(int day) {
		if (day > 0 && day < Constants.SIMULATION_DAYS) {
			return this.daysClientsArray[day - 1];
		}
		return -1;
	}

}
