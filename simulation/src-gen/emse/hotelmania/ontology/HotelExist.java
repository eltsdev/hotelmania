package emse.hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: HotelExist
* @author ontology bean generator
* @version 2014/04/8, 19:26:06
*/
public class HotelExist implements Predicate {

   /**
   * States if one hotel exists or not in hotelmania
* Protege name: exist
   */
   private boolean exist;
   public void setExist(boolean value) { 
    this.exist=value;
   }
   public boolean getExist() {
     return this.exist;
   }

}
