package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: BookRoom
* @author ontology bean generator
* @version 2014/05/15, 18:43:21
*/
public class BookRoom implements AgentAction {

   /**
* Protege name: stay
   */
   private Stay stay;
   public void setStay(Stay value) { 
    this.stay=value;
   }
   public Stay getStay() {
     return this.stay;
   }

   /**
* Protege name: bookingOffer
   */
   private BookingOffer bookingOffer;
   public void setBookingOffer(BookingOffer value) { 
    this.bookingOffer=value;
   }
   public BookingOffer getBookingOffer() {
     return this.bookingOffer;
   }

}
