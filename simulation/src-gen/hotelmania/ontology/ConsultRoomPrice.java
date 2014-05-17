package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: ConsultRoomPrice
* @author ontology bean generator
* @version 2014/05/17, 11:25:36
*/
public class ConsultRoomPrice implements AgentAction {

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
