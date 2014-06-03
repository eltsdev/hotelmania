package hotelmania.group2.dao;

import jade.core.AID;

import java.util.ArrayList;
import java.util.HashMap;

public class Client {
	
	// Attribute of client
	
	private Stay stay;
	/**
	 * Total budget for all the days
	 */
	private double budget;

	// Business logic fields
	
	private ArrayList<BookingOffer> offers = new ArrayList<>();
	private BookRoom bookingDone;
	private HotelInformation hotelOfBookingDone;
	private HashMap<Integer, RatingInput> ratingData = new HashMap<>();

	public Stay getStay() {
		return stay;
	}
	public void setStay(Stay stay) {
		this.stay = stay;
	}
	/**
	 * @return budget Total budget for all the days
	 */
	public double getBudget() {
		return budget;
	}
	public void setBudget(double budget) {
		this.budget = budget;
	}
	public ArrayList<BookingOffer> getOffers() {
		return offers;
	}
	public void setOffers(ArrayList<BookingOffer> offers) {
		this.offers = offers;
	}
	public BookRoom getBookingDone() {
		return bookingDone;
	}
	public void setBookingDone(BookRoom booking) {
		this.bookingDone = booking;
	}
	public int getCheckInDate() {
		return this.stay.getCheckIn();
	}
	public int getCheckOutDate() {
		return this.stay.getCheckOut();
	}
	
	
	public void setOfferPrice(AID hotel, float roomPrice) {
		//Seek the Hotel in BookingOffers and updates the price
		for (BookingOffer bookingOffer : offers) {
			if (bookingOffer.getHotelInformation().getHotel().getAgent().equals(hotel)) {
				bookingOffer.setPrice(roomPrice);
			}
		}
	}
	
	public BookingOffer computeBestRoomPrice() {
		float minimunPrice = 0;
		BookingOffer lowestPriceBooking = null;
		
		for (BookingOffer bookingOffer : offers) {
			float actual_price = bookingOffer.getPrice();
			if (actual_price != -1) {
				if (minimunPrice < actual_price) {
					minimunPrice = actual_price;
					lowestPriceBooking = bookingOffer;
				}
			}
		}
		return lowestPriceBooking;
	}
	
	public boolean acceptOffer(BookingOffer offer) {
		if (offer.getPrice() <= getMaximumPrice()) {
			return true;
		}
		return false;
	}
	
	/**
	 * The maximum able to pay
	 */
	//TODO refine strategy
	private float getMaximumPrice(){
		return (float) this.budget;
	}

	public HotelInformation getHotelOfBookingDone() {
		return hotelOfBookingDone;
	}
	public void setHotelOfBookingDone(HotelInformation hotelOfBookingDone) {
		this.hotelOfBookingDone = hotelOfBookingDone;
	}
	public boolean isDataForRatingComplete() {
		return this.ratingData.size() == this.stay.getDays();
	}
	public void addOccupancyForRating(int day, int num_clients) {
		RatingInput ratingInput = ratingData.get(day);
		
		if (ratingInput == null) {
			ratingInput = new RatingInput();
			this.ratingData.put(day, ratingInput);
		}
		
		ratingInput.setOccupancy(num_clients);
	}
	
	public RatingInput getOccupancyForDay (int day) {
		return this.ratingData.get(day);
	}
	
	public HashMap<Integer, RatingInput> getRatingData() {
		return ratingData;
	}
}