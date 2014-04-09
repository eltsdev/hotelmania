package emse.hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: RegisterHotel
* @author ontology bean generator
* @version 2014/04/8, 19:26:06
*/
public class RegisterHotel implements AgentAction {

   /**
* Protege name: HotelName
   */
   private String hotelName;
   public void setHotelName(String value) { 
    this.hotelName=value;
   }
   public String getHotelName() {
     return this.hotelName;
   }

}
