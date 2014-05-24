package hotelmania.group2.dao;

import java.util.ArrayList;

public class RatingDAO {

	private ArrayList<Rating> rates;

	public RatingDAO() {
		rates = new ArrayList<Rating>();
	}

	/**
	 * @param hotel
	 * @param ratings
	 * @return
	 */
	public boolean addRating(String hotel, float cleanliness, float chefs, float price, float roomStaff) {
		boolean ratingFound = false;
		for (Rating rating : this.rates) {
			if (rating.getHotel().equals(hotel)) {
				rating.addRating(cleanliness, chefs, price, roomStaff);
				ratingFound = true;
			}
		}
		if (!ratingFound) {
			Rating rate = new Rating(hotel, cleanliness, chefs, price, roomStaff);
			rates.add(rate);
		}
		return true;
	}
	
	public Rating getRatingOfHotel(String hotel) {
		for (Rating rating : this.rates) {
			if (rating.getHotel().equals(hotel)) {
				return rating;
			}
		}
		return null;//TODO return empty or default rating?
	}
}
