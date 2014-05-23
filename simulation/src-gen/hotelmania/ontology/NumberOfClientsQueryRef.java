package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: NumberOfClientsQueryRef
* @author ontology bean generator
* @version 2014/05/23, 16:31:32
*/
public class NumberOfClientsQueryRef implements AgentAction {

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
* Protege name: hotel_name
   */
   private String hotel_name;
   public void setHotel_name(String value) { 
    this.hotel_name=value;
   }
   public String getHotel_name() {
     return this.hotel_name;
   }

}
