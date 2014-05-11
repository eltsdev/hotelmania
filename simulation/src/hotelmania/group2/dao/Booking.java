package hotelmania.group2.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * @author user
 *
 */
public class Booking {

	private int days;
	private String startDate;
	/**
	 * @param days2
	 * @param startDate2
	 */
	public Booking(int days, String startDate) {
		this.days = days;
		this.startDate = startDate;
	}
	/**
	 * @return the days
	 */
	public int getDays() {
		return days;
	}
	/**
	 * @param days the days to set
	 */
	public void setDays(int days) {
		this.days = days;
	}
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	
	
}
