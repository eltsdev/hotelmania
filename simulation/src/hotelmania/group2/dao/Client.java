package hotelmania.group2.dao;

import hotelmania.group2.platform.Logger;
import hotelmania.group2.platform.NormUtil;
import hotelmania.ontology.Contract;
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
	
	public HotelInformation getHotelOfBookingDone() {
		return hotelOfBookingDone;
	}
	
	public void setHotelOfBookingDone(HotelInformation hotelOfBookingDone) {
		this.hotelOfBookingDone = hotelOfBookingDone;
	}
	
	public void setOfferPrice(AID hotel, float roomPrice) {
		//Seek the Hotel in BookingOffers and updates the price
		for (BookingOffer bookingOffer : offers) {
			if (bookingOffer.getHotelInformation().getHotel().getAgent().equals(hotel)) {
				bookingOffer.setPrice(roomPrice);
				//updates all previous offers of the hotel...
			}
		}
	}
	
	//
	// Client Strategy for Selecting Hotels
	//
	
	public BookingOffer computeBestBookingOffer() {
		if (offers == null || offers.size() == 0) {
			return null;
		}
		
		double maxValuation = 0;
		BookingOffer bestOffer = null;
		
		double priceValuation = 0, ratingValuation = 0, totalValuation = 0;
		
		for (BookingOffer bookingOffer : offers) {
			priceValuation = getPriceValuation(bookingOffer.getPrice());
			ratingValuation = bookingOffer.getHotelInformation().getRating();
			
			totalValuation = (priceValuation + ratingValuation)/2.0;
			if (totalValuation > maxValuation) {
				maxValuation = totalValuation;
				bestOffer = bookingOffer;
			}
			
			//keep the value
			bookingOffer.setClientValuation(totalValuation);
		}
		
		//return the lowest
		return bestOffer;
	}
	
	private void getMaxAndMinPrice(ArrayList<BookingOffer> data,double[] minMaxPrice) {
		double max=data.get(0).getPrice();
		double min=max;
		
		for (BookingOffer offer : data) {
			double current = offer.getPrice();
			
			if( current < min) min = current;
			
			if( current > max) max = current;
		} 
	
		minMaxPrice[0] = min;
		minMaxPrice[1] = max;
	}
	
	private void getMaxAndMinRating(ArrayList<BookingOffer> data,double[] minMaxRating) {
		double max=data.get(0).getHotelInformation().getRating();
		double min=max;
		for (BookingOffer offer : data) {
			double current = offer.getHotelInformation().getRating();
			
			if( current < min) min = current;
			
			if( current > max) max = current;
		} 
	
		minMaxRating[0] = min;
		minMaxRating[1] = max;
	}
	
	private double getRatingValuation(float rating, double[] minMaxRating) {
		double valuation = NormUtil.normalize(rating, minMaxRating[0], minMaxRating[1]);
		return valuation;
	}
	
	private double getPriceValuation(float price) {
		double valuation = 0;
		if (price > 0 && price < budget) {
			valuation = (budget-price)/5.0;
		}
		return valuation;
	}
	
	//
	// Client methodology for rating new hotels
	//

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
	
	public void addStaffForRating(int day, Contract content) {
		RatingInput ratingInput = ratingData.get(day);

		if (ratingInput == null) {
			ratingInput = new RatingInput();
			this.ratingData.put(day, ratingInput);
		}

		ratingInput.setStaff(new hotelmania.group2.dao.Contract(content));
	}
	
	public void printRatingData() {
		for (Integer day : ratingData.keySet()) {
			String line = "Rating Data: Day = "+day + ": Staff = "+ratingData.get(day).getStaff().toString() + " Occupancy = " + ratingData.get(day).getOccupancy();
			Logger.logDebug(line );
		}
	}
	
	public RatingInput getOccupancyForDay (int day) {
		return this.ratingData.get(day);
	}
	
	public HashMap<Integer, RatingInput> getRatingData() {
		return ratingData;
	}
	
}