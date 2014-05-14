package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: BookRoom
* @author ontology bean generator
* @version 2014/05/14, 12:09:02
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
