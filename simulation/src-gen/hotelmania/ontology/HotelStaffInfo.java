package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: HotelStaffInfo
* @author ontology bean generator
* @version 2014/06/8, 15:02:17
*/
public class HotelStaffInfo implements Predicate {

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
