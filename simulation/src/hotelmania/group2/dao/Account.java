/**
 * 
 */
package hotelmania.group2.dao;

/**
 * @author user
 *
 */
public class Account {

	private Hotel hotel;
	private float balance;
	private int id;

	public Account() {
	}

	/**
	 * @param hotel2
	 * @param balance2
	 */
	public Account(Hotel hotel, float balance, int id) {
		this.hotel = hotel;
		this.balance = balance;
		this.id = id;
	}

	/**
	 * @return the hotel
	 */
	public Hotel getHotel() {
		return hotel;
	}

	/**
	 * @param hotel
	 *            the hotel to set
	 */
	public void setHotel(Hotel hotel) {
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
	
	public hotelmania.ontology.Account getConcept() {
		hotelmania.ontology.Account conceptAccount = new hotelmania.ontology.Account();
		conceptAccount.setBalance(this.balance);
		conceptAccount.setHotel(this.hotel.getConcept());
		conceptAccount.setId_account(this.id);
		return conceptAccount;
	}
	

}
