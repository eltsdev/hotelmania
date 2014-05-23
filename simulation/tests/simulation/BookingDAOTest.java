package simulation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import hotelmania.group2.dao.BookRoom;
import hotelmania.group2.dao.BookingDAO;
import hotelmania.group2.dao.Stay;
import hotelmania.group2.platform.Constants;

import org.junit.Before;
import org.junit.Test;

public class BookingDAOTest {

	private BookingDAO bookingDao;
	
	@Before
	public void initialization() {
		Constants.CLIENTS_PER_DAY = 6;
		Constants.SIMULATION_DAYS = 30;
		bookingDao = new BookingDAO();

	}
	
	@Test
	public void testBook() {
		int numberOfClientsPerDay = Constants.CLIENTS_PER_DAY;
		for (int i = 0; i < numberOfClientsPerDay; i++) {
			BookRoom bookRoom = new BookRoom();
			Stay stay = new Stay(1, 5);
			bookRoom.setStay(stay);
			assertTrue("It should allow to book",bookingDao.book(bookRoom));
		}
		BookRoom bookRoom = new BookRoom();
		Stay stay = new Stay(1, 5);
		bookRoom.setStay(stay);
		assertFalse("Booking is full, it shouldnt allow to book",bookingDao.book(bookRoom));
	}
	
	@Test
	public void testBook2() {
		int numberOfClientsPerDay = Constants.CLIENTS_PER_DAY;
		for (int i = 0; i < numberOfClientsPerDay; i++) {
			BookRoom bookRoom = new BookRoom();
			Stay stay = new Stay(1, 5);
			bookRoom.setStay(stay);
			assertTrue("It should allow to book",bookingDao.book(bookRoom));
		}
		BookRoom bookRoom = new BookRoom();
		Stay stay = new Stay(2, 8);
		bookRoom.setStay(stay);
		assertFalse("Booking is full, it shouldnt allow to book",bookingDao.book(bookRoom));
	}
	
	@Test
	public void testBook3() {
		int numberOfClientsPerDay = Constants.CLIENTS_PER_DAY;
		for (int i = 0; i < numberOfClientsPerDay; i++) {
			BookRoom bookRoom = new BookRoom();
			Stay stay = new Stay(1, 5);
			bookRoom.setStay(stay);
			assertTrue("It should allow to book",bookingDao.book(bookRoom));
		}
		BookRoom bookRoom = new BookRoom();
		Stay stay = new Stay(5, 9);
		bookRoom.setStay(stay);
		assertTrue("It should allow to book",bookingDao.book(bookRoom));
	}
	
	@Test
	public void getClientsAtDayTest1 (){
		for (int i = 0; i < 3; i++) {
			BookRoom bookRoom = new BookRoom();
			Stay stay = new Stay(1, 5);
			bookRoom.setStay(stay);
			assertTrue("It should allow to book",bookingDao.book(bookRoom));
		}
		for (int i = 0; i < 3; i++) {
			BookRoom bookRoom = new BookRoom();
			Stay stay = new Stay(4, 7);
			bookRoom.setStay(stay);
			assertTrue("It should allow to book",bookingDao.book(bookRoom));
		}
		assertTrue("On day 1 there should be 3 clients and there are: " + bookingDao.getClientsAtDay(1), bookingDao.getClientsAtDay(1) == 3);
		assertTrue("On day 2 there should be 3 clients and there are: " + bookingDao.getClientsAtDay(2), bookingDao.getClientsAtDay(2) == 3);
		assertTrue("On day 3 there should be 3 clients and there are: " + bookingDao.getClientsAtDay(3), bookingDao.getClientsAtDay(3) == 3);
		assertTrue("On day 4 there should be 6 clients and there are: " + bookingDao.getClientsAtDay(4), bookingDao.getClientsAtDay(4) == 6);
		assertTrue("On day 5 there should be 3 clients and there are: " + bookingDao.getClientsAtDay(5), bookingDao.getClientsAtDay(5) == 3);
		assertTrue("On day 6 there should be 3 clients and there are: " + bookingDao.getClientsAtDay(6), bookingDao.getClientsAtDay(6) == 3);
		assertTrue("On day 7 there should be 0 clients and there are: " + bookingDao.getClientsAtDay(7), bookingDao.getClientsAtDay(7) == 0);
	}
	
	@Test
	public void getClientsAtDayTest2 (){
		for (int i = 0; i < 3; i++) {
			BookRoom bookRoom = new BookRoom();
			Stay stay = new Stay(1, 5);
			bookRoom.setStay(stay);
			assertTrue("It should allow to book",bookingDao.book(bookRoom));
		}
		for (int i = 0; i < 3; i++) {
			BookRoom bookRoom = new BookRoom();
			Stay stay = new Stay(4, 7);
			bookRoom.setStay(stay);
			assertTrue("It should allow to book",bookingDao.book(bookRoom));
		}
		for (int i = 0; i < 2; i++) {
			BookRoom bookRoom = new BookRoom();
			Stay stay = new Stay(1, 4);
			bookRoom.setStay(stay);
			assertTrue("It should allow to book",bookingDao.book(bookRoom));
		}
		
		assertTrue("On day 1 there should be 5 clients and there are: " + bookingDao.getClientsAtDay(1), bookingDao.getClientsAtDay(1) == 5);
		assertTrue("On day 2 there should be 5 clients and there are: " + bookingDao.getClientsAtDay(2), bookingDao.getClientsAtDay(2) == 5);
		assertTrue("On day 3 there should be 5 clients and there are: " + bookingDao.getClientsAtDay(3), bookingDao.getClientsAtDay(3) == 5);
		assertTrue("On day 4 there should be 6 clients and there are: " + bookingDao.getClientsAtDay(4), bookingDao.getClientsAtDay(4) == 6);
		assertTrue("On day 5 there should be 3 clients and there are: " + bookingDao.getClientsAtDay(5), bookingDao.getClientsAtDay(5) == 3);
		assertTrue("On day 6 there should be 3 clients and there are: " + bookingDao.getClientsAtDay(6), bookingDao.getClientsAtDay(6) == 3);
		assertTrue("On day 7 there should be 0 clients and there are: " + bookingDao.getClientsAtDay(7), bookingDao.getClientsAtDay(7) == 0);
	}
	
	@Test
	public void isThereRoomAvailableAtDaysTest1() {
		for (int i = 0; i < 3; i++) {
			BookRoom bookRoom = new BookRoom();
			Stay stay = new Stay(1, 5);
			bookRoom.setStay(stay);
			assertTrue("It should allow to book",bookingDao.book(bookRoom));
		}
		assertTrue("There should be rooms availables",bookingDao.isThereRoomAvailableAtDays(1, 5));
		for (int i = 0; i < 2; i++) {
			BookRoom bookRoom = new BookRoom();
			Stay stay = new Stay(1, 5);
			bookRoom.setStay(stay);
			assertTrue("It should allow to book",bookingDao.book(bookRoom));
		}
		assertTrue("There should be rooms availables",bookingDao.isThereRoomAvailableAtDays(1, 5));
		assertTrue("There should be rooms availables",bookingDao.isThereRoomAvailableAtDays(4, 8));
		for (int i = 0; i < 1; i++) {
			BookRoom bookRoom = new BookRoom();
			Stay stay = new Stay(1, 5);
			bookRoom.setStay(stay);
			assertTrue("It should allow to book",bookingDao.book(bookRoom));
		}
		assertFalse("There should not be rooms availables",bookingDao.isThereRoomAvailableAtDays(1, 5));
		assertFalse("There should not be rooms availables",bookingDao.isThereRoomAvailableAtDays(2, 8));
		assertFalse("There should not be rooms availables",bookingDao.isThereRoomAvailableAtDays(4, 8));
		assertTrue("There should be rooms availables",bookingDao.isThereRoomAvailableAtDays(5, 7));
		
		
	}

}
