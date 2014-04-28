package hotelmania.ontology;


import jade.content.Concept;

/**
* Protege name: AccountStatus
* @author ontology bean generator
* @version 2014/04/23, 10:59:43
*/
public class AccountStatus implements Concept {

   /**
	 * 
	 */
	private static final long serialVersionUID = 1805036929737451355L;
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
