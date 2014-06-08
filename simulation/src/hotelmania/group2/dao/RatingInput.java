package hotelmania.group2.dao;

public class RatingInput {
	private Integer occupancy;
	private Contract staff;

	public Integer getOccupancy() {
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
			return "Occupancy: " + this.occupancy + "; staff hired in day:" + this.staff.getDate();	
		}
		return "RatingInput with Null Staff";
	
	}

}
