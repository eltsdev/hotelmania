package hotelmania.onto;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Contract
* @author ontology bean generator
* @version 2014/04/20, 19:30:10
*/
public class Contract implements Concept {

   /**
* Protege name: quantity
   */
   private int quantity;
   public void setQuantity(int value) { 
    this.quantity=value;
   }
   public int getQuantity() {
     return this.quantity;
   }

   /**
* Protege name: category
   */
   private String category;
   public void setCategory(String value) { 
    this.category=value;
   }
   public String getCategory() {
     return this.category;
   }

}
