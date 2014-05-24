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
	public Account registerNewAccount(Hotel hotel, int balance) {
		for (Account account : this.listAccount) {
			if (account.getHotel().equals(hotel)) {
				return account;
			}
		}
		Account account = new Account(hotel, balance, this.currentId);
		this.listAccount.add(account);
		this.currentId++;
		return account;
	}

	/**
	 * @param hotel_name
	 * @param quantity
	 * @return
	 */
	public boolean registerNewDeposit(String hotel_name, float quantity) {
		for (Account account : this.listAccount) {
			if (account.getHotel().getName().equals(hotel_name)) {
				account.deposit(quantity);
				return true;
			}
		}
		return false;
	}

	/**
	 * @param hotel_name
	 * @param quantity
	 * @return
	 */
	public boolean chargeMoney(String hotel_name, float quantity) {
		for (Account account : this.listAccount) {
			if (account.getHotel().getName().equals(hotel_name)) {
				account.charge(quantity);
				return true;
			}
		}
		return false;
	}

	public Account getAcountWithId(int accountId) {
		for (hotelmania.group2.dao.Account account : this.listAccount) {
			if (account.getId() == accountId) {
				return account;
			}
		}
		return null;
	}
}
