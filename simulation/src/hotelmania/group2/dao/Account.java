/**
 * 
 */
package hotelmania.group2.dao;

/**
 * @author user
 *
 */
public class Account {

	private String hotel;
	private float balance;
	private int id;

	public Account() {
	}

	/**
	 * @param hotel2
	 * @param balance2
	 */
	public Account(String hot, float bal, int id) {
		this.hotel = hot;
		this.balance = bal;
		this.id = id;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	

}
