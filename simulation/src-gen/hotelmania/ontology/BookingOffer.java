package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: BookingOffer
* @author ontology bean generator
* @version 2014/05/23, 16:54:58
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
