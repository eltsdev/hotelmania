package hotelmania.group2.dao;


/**
 * @author user
 *
 */
public class Booking {

 private Stay stay;
 
 private Price roomPrice;
 /**
 * 
 */
public Booking() {
	// TODO Auto-generated constructor stub
}
/**
 * @param stay2
 * @param price
 */
public Booking(Stay stay2, Price price) {
	this.stay = stay2;
	this.roomPrice = price;
}
/**
 * @return the roomPrice
 */
public Price getRoomPrice() {
	return roomPrice;
}
/**
 * @param roomPrice the roomPrice to set
 */
public void setRoomPrice(Price roomPrice) {
	this.roomPrice = roomPrice;
}
/**
 * @return the stay
 */
public Stay getStay() {
	return stay;
}
/**
 * @param stay the stay to set
 */
public void setStay(Stay stay) {
	this.stay = stay;
}
	
}
