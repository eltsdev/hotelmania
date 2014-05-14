package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: RateHotel
* @author ontology bean generator
* @version 2014/05/14, 12:09:02
*/
public class RateHotel implements AgentAction {

   /**
* Protege name: ratings
   */
   private Rating ratings;
   public void setRatings(Rating value) { 
    this.ratings=value;
   }
   public Rating getRatings() {
     return this.ratings;
   }

   /**
* Protege name: hotel
   */
   private Hotel hotel;
   public void setHotel(Hotel value) { 
    this.hotel=value;
   }
   public Hotel getHotel() {
     return this.hotel;
   }

}
