/**
 * 
 */
package emse.hotelmania.dao;


/**
 * @author user
 *
 */
public class Account {
	

	
	
	private String hotel;
	private float balance;

	/**
	 * 
	 */
	public Account() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param hotel2
	 * @param balance2
	 */
	public Account(String hot, float bal) {
		hotel = hot;
		balance = bal;
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

	/**
	 * @return the balance
	 */
	public float getBalance() {
		return balance;
	}

	/**
	 * @param balance
	 *            the balance to set
	 */
	public void setBalance(float balance) {
		this.balance = balance;
	}


}
