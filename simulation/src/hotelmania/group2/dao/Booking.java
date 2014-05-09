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
	private Date startDate;
	/**
	 * @param days2
	 * @param startDate2
	 */
	public Booking(int days2, String startDate2) {
		days = days2;
		DateFormat day = null;
		
		try {
			startDate = day.parse(startDate2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
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
	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
