package hotelmania.group2.dao;

/**
 * @author user
 *
 */
public class Rating {

	private float priceRating;
	private float cleanlinessRating;
	private float roomStaffRating;
	private float chefRating;
	private String hotel;

	/**
	 * @param hotelName
	 * @param cleanlinessRating
	 * @param chefRating
	 * @param priceRating
	 * @param roomStaffRating
	 */
	public Rating(String hotelName, float cleanlinessRating, float chefRating, float priceRating, float roomStaffRating) {
		this.priceRating = priceRating;
		this.cleanlinessRating = cleanlinessRating;
		this.roomStaffRating = roomStaffRating;
		this.chefRating = chefRating;
		this.hotel = hotelName;

	}

	/**
	 * @return the hotel
	 */
	public String getHotel() {
		return hotel;
	}

	/**
	 * @param hotel
	 *            the hotel to set
	 */
	public void setHotel(String hotel) {
		this.hotel = hotel;
	}

	public void setPrice_rating(float value) {
		this.priceRating = value;
	}

	public float getPrice_rating() {
		return this.priceRating;
	}

	public void setCleanliness_rating(float value) {
		this.cleanlinessRating = value;
	}

	public float getCleanliness_rating() {
		return this.cleanlinessRating;
	}

	public void setRoom_staff_rating(float value) {
		this.roomStaffRating = value;
	}

	public float getRoom_staff_rating() {
		return this.roomStaffRating;
	}

	public float getChef_rating() {
		return chefRating;
	}

	public void setChef_rating(float chef_rating) {
		this.chefRating = chef_rating;
	}
	
	public void addRating(float cleanliness, float chefs, float price, float roomStaff) {
		this.cleanlinessRating = (cleanliness + this.cleanlinessRating)/2;
		this.chefRating = (chefs + this.chefRating)/2;
		this.priceRating = (price + this.priceRating)/2;
		this.roomStaffRating = (roomStaff + this.roomStaffRating)/2;
	}

}
