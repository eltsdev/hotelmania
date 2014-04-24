package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: HotelInfo
* @author ontology bean generator
* @version 2014/04/23, 10:59:43
*/
public class HotelInfo implements Concept {

   /**
* Protege name: rating
   */
   private float rating;
   public void setRating(float value) { 
    this.rating=value;
   }
   public float getRating() {
     return this.rating;
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