package hotelmania.ontology;


import jade.content.AgentAction;

/**
* Protege name: NumberOfClientsQueryRef
* @author ontology bean generator
* @version 2014/05/10, 18:26:54
*/
public class NumberOfClientsQueryRef implements AgentAction {

   /**
	 * 
	 */
	private static final long serialVersionUID = -5242818852065398394L;
/**
* Protege name: hotel_name
   */
   private String hotel_name;
   public void setHotel_name(String value) { 
    this.hotel_name=value;
   }
   public String getHotel_name() {
     return this.hotel_name;
   }

}
