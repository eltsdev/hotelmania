package hotelmania.ontology;


import jade.content.AgentAction;

/**
* Protege name: ChargeAccount
* @author ontology bean generator
* @version 2014/04/23, 10:59:43
*/
public class ChargeAccount implements AgentAction {

   /**
	 * 
	 */
	private static final long serialVersionUID = 7119722549930410293L;
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
