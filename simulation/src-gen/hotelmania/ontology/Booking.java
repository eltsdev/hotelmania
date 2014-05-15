package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Booking
* @author ontology bean generator
* @version 2014/05/15, 13:46:02
*/
public class Booking implements Concept {

   /**
* Protege name: startDay
   */
   private String startDay;
   public void setStartDay(String value) { 
    this.startDay=value;
   }
   public String getStartDay() {
     return this.startDay;
   }

   /**
* Protege name: days
   */
   private int days;
   public void setDays(int value) { 
    this.days=value;
   }
   public int getDays() {
     return this.days;
   }

}
