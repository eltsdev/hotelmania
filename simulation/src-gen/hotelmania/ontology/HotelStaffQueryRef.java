package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: HotelStaffQueryRef
* @author ontology bean generator
* @version 2014/06/8, 15:02:17
*/
public class HotelStaffQueryRef implements Predicate {

   /**
* Protege name: day
   */
   private int day;
   public void setDay(int value) { 
    this.day=value;
   }
   public int getDay() {
     return this.day;
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
