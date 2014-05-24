package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: DayEvent
* @author ontology bean generator
* @version 2014/05/24, 18:36:35
*/
public class DayEvent implements Concept {

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

}
