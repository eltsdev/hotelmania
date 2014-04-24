package hotelmania.group2.dao;

import java.util.Date;

public class Contract 
{
	// Constants
	//	static final int TYPE_RECEPCIONIST=0;
	//	static final int TYPE_COOKER=1;
	//	static final int TYPE_ROOMSTAFF=2;
	//	
	//	static final int CATEGORY_RECEPCIONIST_NOVICE = 10;
	//	static final int CATEGORY_RECEPCIONIST_EXPERIENCED = 11;
	//	
	//	static final int CATEGORY_COOKER_1STARS = 21;
	//	static final int CATEGORY_COOKER_2STARS = 22;
	//	static final int CATEGORY_COOKER_3STARS = 23;
	//	
	//	static final int CATEGORY_ROOMSERVICE = 30;

	
	//Attributes
	String hotelName;
	
	int recepcionistNovice;
	int recepcionistExperienced;
	int cooker1stars;
	int cooker2stars;
	int cooker3stars;
	int roomservice;
	
	Date date;
	
 	public Contract() 
 	{
		this.date = new Date();
	}
 	
	public Contract(String hotelName, 
			int recepcionistNovice,
			int recepcionistExperienced, 
			int cooker1stars, 
			int cooker2stars,
			int cooker3stars, 
			int roomservice, 
			Date date)
	{
		this.hotelName = hotelName;
		this.recepcionistNovice = recepcionistNovice;
		this.recepcionistExperienced = recepcionistExperienced;
		this.cooker1stars = cooker1stars;
		this.cooker2stars = cooker2stars;
		this.cooker3stars = cooker3stars;
		this.roomservice = roomservice;
		this.date = date;
	}



	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public int getRecepcionistNovice() {
		return recepcionistNovice;
	}

	public void setRecepcionistNovice(int recepcionistNovice) {
		this.recepcionistNovice = recepcionistNovice;
	}

	public int getRecepcionistExperienced() {
		return recepcionistExperienced;
	}

	public void setRecepcionistExperienced(int recepcionistExperienced) {
		this.recepcionistExperienced = recepcionistExperienced;
	}

	public int getCooker1stars() {
		return cooker1stars;
	}

	public void setCooker1stars(int cooker1stars) {
		this.cooker1stars = cooker1stars;
	}

	public int getCooker2stars() {
		return cooker2stars;
	}

	public void setCooker2stars(int cooker2stars) {
		this.cooker2stars = cooker2stars;
	}

	public int getCooker3stars() {
		return cooker3stars;
	}

	public void setCooker3stars(int cooker3stars) {
		this.cooker3stars = cooker3stars;
	}

	public int getRoomservice() {
		return roomservice;
	}

	public void setRoomservice(int roomservice) {
		this.roomservice = roomservice;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
 	

 	
}
