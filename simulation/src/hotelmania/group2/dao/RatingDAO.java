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
	public void addRating(String hotel, float cleanliness, float chefs, float price, float roomStaff) {
		Rating rating = new Rating(hotel, cleanliness, chefs, price, roomStaff);
		this.rates.add(rating);
	}
	
	public ArrayList<Rating> getRatingsOfHotel(String hotel) {
		ArrayList<Rating> list = new ArrayList<>();
		for (Rating rating : this.rates) {
			if (rating.getHotel().equals(hotel)) {
				list.add(rating);
			}
		}
		return list;
	}
}
