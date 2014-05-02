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

	private static AccountDAO instance;
	
	private ArrayList<Account> listAccount;

	private AccountDAO() {
		listAccount = new ArrayList<Account>();

	}

	public static AccountDAO getInstance() {
		if (instance==null) {
			instance = new AccountDAO();
		}
		return instance;
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

	/**
	 * @param hotel_name
	 * @param money
	 * @return
	 */
	public boolean registerNewDeposit(String hotel_name, float money) {
		for (int i = 0; i < listAccount.size(); i++) {
			if (listAccount.get(i).getHotel().equals(hotel_name)) {
				listAccount.get(i).setBalance(
						listAccount.get(i).getBalance() + money);
				return true;
			}

		}
		return false;
	}

	/**
	 * @param hotel_name
	 * @param money
	 * @return
	 */
	public boolean chargeMoney(String hotel_name, float money) {
		for (int i = 0; i < listAccount.size(); i++) {
			if (listAccount.get(i).getHotel().equals(hotel_name)) {
				listAccount.get(i).setBalance(
						listAccount.get(i).getBalance() - money);
				return true;
			}

		}
		return false;
	}	
}
