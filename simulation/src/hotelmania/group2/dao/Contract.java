package hotelmania.group2.dao;

public class Contract {
	String hotelName;
	int date;
	int chef1stars;
	int chef2stars;
	int chef3stars;
	int recepcionistExperienced;
	int recepcionistNovice;
	int roomService;

	public Contract() {
		this.date = 0;
	}

	public Contract(String hotelName, int date, int chef1stars,
			int chef2stars, int chef3stars, int recepcionistExperienced,
			int recepcionistNovice, int roomService) {
		super();
		this.hotelName = hotelName;
		this.date = date;
		this.chef1stars = chef1stars;
		this.chef2stars = chef2stars;
		this.chef3stars = chef3stars;
		this.recepcionistExperienced = recepcionistExperienced;
		this.recepcionistNovice = recepcionistNovice;
		this.roomService = roomService;
	}
	
	public Contract(hotelmania.ontology.Contract contract) {
		super();
		//this.hotelName = hotelName;
		this.date = contract.getDay();
		this.chef1stars = contract.getChef_1stars();
		this.chef2stars = contract.getChef_2stars();
		this.chef3stars = contract.getChef_3stars();
		this.recepcionistExperienced = contract.getRecepcionist_experienced();
		this.recepcionistNovice = contract.getRecepcionist_novice();
		this.roomService = contract.getRoom_service_staff();
	}

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public int getchef1stars() {
		return chef1stars;
	}

	public void setchef1stars(int chef1stars) {
		this.chef1stars = chef1stars;
	}

	public int getchef2stars() {
		return chef2stars;
	}

	public void setchef2stars(int chef2stars) {
		this.chef2stars = chef2stars;
	}

	public int getchef3stars() {
		return chef3stars;
	}

	public void setchef3stars(int chef3stars) {
		this.chef3stars = chef3stars;
	}

	public int getRecepcionistExperienced() {
		return recepcionistExperienced;
	}

	public void setRecepcionistExperienced(int recepcionistExperienced) {
		this.recepcionistExperienced = recepcionistExperienced;
	}

	public int getRecepcionistNovice() {
		return recepcionistNovice;
	}

	public void setRecepcionistNovice(int recepcionistNovice) {
		this.recepcionistNovice = recepcionistNovice;
	}

	public int getRoomService() {
		return roomService;
	}

	public void setRoomService(int roomService) {
		this.roomService = roomService;
	}

}
