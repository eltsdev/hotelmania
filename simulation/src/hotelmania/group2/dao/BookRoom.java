package hotelmania.group2.dao;


/**
 * @author user
 *
 */
public class BookRoom {

 private Stay stay;
 
 private Price roomPrice;
 /**
 * 
 */
public BookRoom() {
	// TODO Auto-generated constructor stub
}
/**
 * @param stay2
 * @param price2
 */
public BookRoom(Stay stay2, Price price2) {
	this.stay = stay2;
	this.roomPrice = price2;
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
