/**
 * 
 */
package hotelmania.group2.dao;

import java.util.ArrayList;

import org.junit.runner.Computer;

/**
 * @author user
 *
 */
public class RateDAO {

	private ArrayList<Rating> rates;

	/**
	 * 
	 */
	public RateDAO() {
		rates = new ArrayList<Rating>();
	}

	/**
	 * @param hotel
	 * @param ratings
	 * @return
	 */
	public boolean registerNewRating(String hotel, float clean, float cooker,
			float price, float room) {

		Rating rate = new Rating(hotel, clean, cooker, price, room);

		rates.add(rate);
	//	computeRating();
		return true;
	}

	/**
	 * 
	 */
	private void computeRating() {
		for (int i = 0; i < rates.size(); i++) {
			//TODO could be using HashMap?
			
		}
		
	}

}
