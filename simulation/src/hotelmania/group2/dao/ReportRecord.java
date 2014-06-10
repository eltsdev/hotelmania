package hotelmania.group2.dao;

public class ReportRecord {
	
	private String hotel;
	private double rating;
	private double balance;
	private int customers;
	
	public String getHotel() {
		return hotel;
	}
	public void setHotel(String hotel) {
		this.hotel = hotel;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	@Override
	public String toString() {
		String balanceStr = String.format("%.1f", balance);
		String ratingStr = String.format("%.1f", rating);

		return hotel + "\t\t" + ratingStr + "\t\t" + balanceStr + "\t\t"+ customers;
	}
	public int getCustomers() {
		return customers;
	}
	public void setCustomers(int customers) {
		this.customers = customers;
	}
}
