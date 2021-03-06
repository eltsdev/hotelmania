package hotelmania.group2.dao;

import hotelmania.group2.platform.Constants;
import hotelmania.group2.platform.Logger;

import java.util.ArrayList;

public class BookingDAO {
	private ArrayList<hotelmania.group2.dao.BookRoom> bookings = new ArrayList<hotelmania.group2.dao.BookRoom>();
	private int roomsAvailable = Constants.ROOMS_PER_HOTEL;
	private boolean isThereAnyBooking = false;
	
	public BookingDAO() {
	}

	
	public boolean isThereAnyBooking() {
		return isThereAnyBooking;
	}

	public boolean book(hotelmania.group2.dao.BookRoom bookRoom) {
		if (this.isThereRoomAvailableAtDays(bookRoom.getStay().getCheckIn(), bookRoom.getStay().getCheckOut())) {
			this.bookings.add(bookRoom);
			this.isThereAnyBooking = true;
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
		if (checkin < checkout && checkin > 0 && checkout <= Constants.SIMULATION_DAYS) {
			int[] daysClientsArray = this.generateArrayOfClients(this.bookings);
			int numberOfClients;
			for (int i = checkin; i < checkout; i++) {
				numberOfClients = daysClientsArray[i];
				if (numberOfClients >= this.roomsAvailable) {
					return false;
				}
			}
			return true;
		} else {
			if (checkin >= checkout) {
				Logger.logError("BookingDAO.isThereRoomAvailableAtDays checkin should be lower than checkout: checkin->" + checkin + " checkout->" + checkout);
			}
			if (checkin < 1) {
				Logger.logError("BookingDAO.isThereRoomAvailableAtDays checkin should be grater than 0: checkin->" + checkin + " checkout->" + checkout);
			}
			if (checkout > Constants.SIMULATION_DAYS) {
				Logger.logError("BookingDAO.isThereRoomAvailableAtDays checkout should not be grater than simulation days: checkin->" + checkin + " checkout->" + checkout + " SimulationDays->" + Constants.SIMULATION_DAYS);
			}
			return false;
		}
		
	}
}
