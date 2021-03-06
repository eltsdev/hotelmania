package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: ChargeAccount
* @author ontology bean generator
* @version 2014/06/8, 15:02:17
*/
public class ChargeAccount implements AgentAction {

   /**
* Protege name: money
   */
   private float money;
   public void setMoney(float value) { 
    this.money=value;
   }
   public float getMoney() {
     return this.money;
   }

   /**
* Protege name: hotel
   */
   private Hotel hotel;
   public void setHotel(Hotel value) { 
    this.hotel=value;
   }
   public Hotel getHotel() {
     return this.hotel;
   }

}
