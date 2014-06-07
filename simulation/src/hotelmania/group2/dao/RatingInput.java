package hotelmania.group2.dao;

public class RatingInput {
	private int occupancy;
	private Contract staff;

	public int getOccupancy() {
		return occupancy;
	}

	public void setOccupancy(int occupancy) {
		this.occupancy = occupancy;
	}

	public Contract getStaff() {
		return staff;
	}

	public void setStaff(Contract staff) {
		this.staff = staff;
	}
	@Override
	public String toString() {
		if( staff!= null){
			return "Occupancy: " + this.occupancy + " day:" + this.staff.getDate();	
		}
		return "RatingInput with Null Staff";
	
	}

}
