package hotelmania.group2.dao;

import java.util.ArrayList;

public class AccountDAO {

	private ArrayList<Account> listAccount = new ArrayList<Account>();
	private int currentId = 0;
	
	public ArrayList<Account> getListAccount() {
		return listAccount;
	}

	public void setListAccount(ArrayList<Account> listAccount) {
		this.listAccount = listAccount;
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
		listAccount.add(new Account(hotel, balance, this.currentId));
		this.currentId++;
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
