/**
 * 
 */
package hotelmania.group2.dao;

/**
 * @author user
 *
 */
public class BookingOffer {
	private HotelInformation hotelInformation;
	private float price=-1;
	/**
	 * @param hotelInformation
	 */
	public BookingOffer(HotelInformation hotelInformation) {
		super();
		this.hotelInformation = hotelInformation;
	}
	/**
	 * @return the price
	 */
	public float getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(float price) {
		this.price = price;
	}
	/**
	 * @return the hotels
	 */
	public HotelInformation getHotelInformation() {
		return hotelInformation;
	}
	
	
	
	

}
