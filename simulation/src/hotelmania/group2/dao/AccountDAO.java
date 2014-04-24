/**
 * 
 */
package hotelmania.group2.dao;

import java.util.ArrayList;

/**
 * @author user
 *
 */
public class AccountDAO {
	
	
	
	
	
	private ArrayList<Account> listAccount;

	public AccountDAO() {
		listAccount = new ArrayList<Account>();

	}

	/**
	 * @param hotel_name
	 * @return
	 */
	public boolean registerNewAccount(String hotel, float balance) {
		for (int i = 0; i < listAccount.size(); i++) {
			if (listAccount.get(i).getHotel().equals(hotel)) {
				return false;
			}

		}
		listAccount.add(new Account(hotel, balance));
		return true;
	}
	
	
	
	

}
