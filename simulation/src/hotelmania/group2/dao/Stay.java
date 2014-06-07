package hotelmania.group2.dao;

/**
 * @author user
 *
 */
public class Stay {
	 private int checkOut;
	 private int checkIn;
	 
	 
	public Stay() {
	}
	
	
	public Stay(int checkIn, int checkOut) {
		super();
		this.checkIn = checkIn;
		this.checkOut = checkOut;
	}

	/**
	 * @return the checkOut
	 */
	public int getCheckOut() {
		return checkOut;
	}
	/**
	 * @param checkOut the checkOut to set
	 */
	public void setCheckOut(int checkOut) {
		this.checkOut = checkOut;
	}
	/**
	 * @return the checkIn
	 */
	public int getCheckIn() {
		return checkIn;
	}
	/**
	 * @param checkIn the checkIn to set
	 */
	public void setCheckIn(int checkIn) {
		this.checkIn = checkIn;
	}


	public int getDays() {
		return this.checkOut-this.checkIn;
	}
}
