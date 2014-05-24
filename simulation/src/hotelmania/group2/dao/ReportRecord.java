package hotelmania.group2.dao;

public class ReportRecord {
	String hotel;
	double rating;
	double balance;
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
		return hotel+"\t\t"+rating+"\t\t$ "+balance;
	}
}
