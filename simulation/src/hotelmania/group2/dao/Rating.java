package hotelmania.group2.dao;

/**
 * @author user
 *
 */
public class Rating {

	private float price_rating;
	private float cleanliness_rating;
	private float room_staff_rating;
	private float chef_rating;
	private String hotel;

	/**
	 * @param hotel_name
	 * @param cleanliness_rating2
	 * @param cookers_raring2
	 * @param price_rating2
	 * @param room_staff_rating2
	 */
	public Rating(String hotel_name, float cleanliness_rating2,
			float cookers_raring2, float price_rating2, float room_staff_rating2) {
		price_rating = price_rating2;
		cleanliness_rating = cleanliness_rating2;
		room_staff_rating = room_staff_rating2;
		setChef_rating(cookers_raring2);
		hotel = hotel_name;

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
		this.price_rating = value;
	}

	public float getPrice_rating() {
		return this.price_rating;
	}

	public void setCleanliness_rating(float value) {
		this.cleanliness_rating = value;
	}

	public float getCleanliness_rating() {
		return this.cleanliness_rating;
	}

	public void setRoom_staff_rating(float value) {
		this.room_staff_rating = value;
	}

	public float getRoom_staff_rating() {
		return this.room_staff_rating;
	}

	public float getChef_rating() {
		return chef_rating;
	}

	public void setChef_rating(float chef_rating) {
		this.chef_rating = chef_rating;
	}


}
