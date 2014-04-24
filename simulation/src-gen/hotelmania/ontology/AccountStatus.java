package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: AccountStatus
* @author ontology bean generator
* @version 2014/04/23, 10:59:43
*/
public class AccountStatus implements Concept {

   /**
* Protege name: balance
   */
   private float balance;
   public void setBalance(float value) { 
    this.balance=value;
   }
   public float getBalance() {
     return this.balance;
   }

}
