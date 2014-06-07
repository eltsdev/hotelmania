package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Price
* @author ontology bean generator
* @version 2014/06/7, 20:28:10
*/
public class Price implements Concept {

   /**
* Protege name: amount
   */
   private float amount;
   public void setAmount(float value) { 
    this.amount=value;
   }
   public float getAmount() {
     return this.amount;
   }

}
