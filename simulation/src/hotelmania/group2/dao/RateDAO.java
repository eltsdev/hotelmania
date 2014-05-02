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

	private static RateDAO instance;
	
	private ArrayList<Rating> rates;

	private RateDAO() {
		rates = new ArrayList<Rating>();
	}

	public static RateDAO getInstance() {
		if (instance==null) {
			instance = new RateDAO();
		}
		return instance;
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
