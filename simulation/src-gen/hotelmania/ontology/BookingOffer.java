package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: BookingOffer
* @author ontology bean generator
* @version 2014/06/8, 15:02:17
*/
public class BookingOffer implements Predicate {

   /**
* Protege name: roomPrice
   */
   private Price roomPrice;
   public void setRoomPrice(Price value) { 
    this.roomPrice=value;
   }
   public Price getRoomPrice() {
     return this.roomPrice;
   }

}
