package hotelmania.onto;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: SignContract
* @author ontology bean generator
* @version 2014/04/23, 09:30:58
*/
public class SignContract implements AgentAction {

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

   /**
* Protege name: contract
   */
   private Contract contract;
   public void setContract(Contract value) { 
    this.contract=value;
   }
   public Contract getContract() {
     return this.contract;
   }

}
