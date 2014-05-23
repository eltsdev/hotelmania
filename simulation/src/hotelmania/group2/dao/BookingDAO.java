package hotelmania.group2.dao;

import hotelmania.group2.platform.Constants;

import java.util.ArrayList;

public class BookingDAO {
	private ArrayList<hotelmania.group2.dao.BookRoom> bookings = new ArrayList<hotelmania.group2.dao.BookRoom>();
	private int roomsAvailable = Constants.ROOMS_PER_HOTEL;
	
	public BookingDAO() {
	}

	public boolean book(hotelmania.group2.dao.BookRoom bookRoom) {
		if (this.isThereRoomAvailableAtDays(bookRoom.getStay().getCheckIn(), bookRoom.getStay().getCheckOut())) {
			this.bookings.add(bookRoom);
			return true;
		}
		return false;

	}

	public int getClientsAtDay(int day) {
		if (day > 0 && day < Constants.SIMULATION_DAYS) {
			int[] daysClientsArray = this.generateArrayOfClients(this.bookings);
			return daysClientsArray[day];
		}
		return -1;
	}
	
	private int[] generateArrayOfClients(ArrayList<hotelmania.group2.dao.BookRoom> bookings) {
		int[] daysClientsArray = new int[Constants.SIMULATION_DAYS+1];
		for (BookRoom booking : bookings) {
			int checkin = booking.getStay().getCheckIn();
			int checkout = booking.getStay().getCheckOut();
			for (int i = checkin; i < checkout; i++) {
				daysClientsArray[i]++;
			}
		}
		return daysClientsArray;
	}

	public boolean isThereRoomAvailableAtDays(int checkin, int checkout) {
		int[] daysClientsArray = this.generateArrayOfClients(this.bookings);
		int numberOfClients;
		for (int i = checkin; i < checkout; i++) {
			numberOfClients = daysClientsArray[i];
			if (numberOfClients >= this.roomsAvailable) {
				return false;
			}
		}
		return true;
	}
	//	private ArrayList<hotelmania.group2.dao.BookRoom> booking = new ArrayList<hotelmania.group2.dao.BookRoom>();
	//	private int[] daysClientsArray;
	//	private float actualPrice;
	//	private int room_available;
	//
	//	public BookingDAO() {
	//		this.daysClientsArray = new int[Constants.SIMULATION_DAYS];
	//		room_available = Constants.ROOMS_PER_HOTEL;
	//	}
	//
	//	/**
	//	 * @return the room_available
	//	 */
	//	public int getRoom_available() {
	//		return room_available;
	//	}
	//
	//	/**
	//	 * @return the actualPrice
	//	 */
	//	public float getActualPrice() {
	//		return actualPrice;
	//	}



	//	public boolean book(hotelmania.group2.dao.BookRoom booking) {
	//
	//		if (room_available < Constants.ROOMS_PER_HOTEL && actualPrice == booking.getRoomPrice().getPrice()) {
	//			this.booking.add(booking);
	//			room_available--;
	//			setNewPrice();
	//			return true;
	//		}
	//		return false;
	//
	//	}
	//
	//	/**
	//	 * Configure new Price according to the rooms available for today
	//	 */
	//	private void setNewPrice() {
	//		this.actualPrice = this.actualPrice + (Constants.ROOMS_PER_HOTEL - (this.room_available - 1)) * 50;
	//
	//	}
	//
	//	public int getClientsAtDay(int day) {
	//		if (day > 0 && day < Constants.SIMULATION_DAYS) {
	//			return this.daysClientsArray[day - 1];
	//		}
	//		return -1;
	//	}
	//	
	//	public boolean checkDays (int[] days){
	//		if (days.length <= 0) {
	//			return false;
	//		}
	//		for (int i = 0; i < days.length; i++) {
	//			int clients = this.getClientsAtDay(i);
	//			if (clients == this.room_available) {
	//				return false;
	//			}
	//		}
	//		return true;
	//		
	//	}








}
