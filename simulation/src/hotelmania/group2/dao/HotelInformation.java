package hotelmania.group2.dao;


public class HotelInformation {

	private Hotel hotel;
	private float rating;

	
	public HotelInformation() {
		// TODO Auto-generated constructor stub
	}
	public HotelInformation(hotelmania.ontology.HotelInformation hotelInformation) {
		hotel = new Hotel(hotelInformation.getHotel());
		rating = hotelInformation.getRating();
	}
	public void setRating(float value) {
		this.rating = value;
	}

	public float getRating() {
		return this.rating;
	}


	public void setHotel(Hotel value) {
		this.hotel = value;
	}

	public Hotel getHotel() {
		return this.hotel;
	}

	public hotelmania.ontology.HotelInformation getConcept() {
		hotelmania.ontology.HotelInformation concept = new hotelmania.ontology.HotelInformation();
		concept.setHotel(this.hotel.getConcept());
		concept.setRating(this.rating);
		return concept;
	}


}
