package hotelmania.onto;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: ConsultHotelStaff
* @author ontology bean generator
* @version 2014/04/20, 19:30:10
*/
public class ConsultHotelStaff implements AgentAction {

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
