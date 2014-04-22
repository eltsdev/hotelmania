package hotelmania.onto;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: BookRoom
* @author ontology bean generator
* @version 2014/04/20, 19:30:10
*/
public class BookRoom implements AgentAction {

   /**
* Protege name: booking
   */
   private Booking booking;
   public void setBooking(Booking value) { 
    this.booking=value;
   }
   public Booking getBooking() {
     return this.booking;
   }

}
