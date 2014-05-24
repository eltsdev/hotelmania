package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: RoomHotelInfo
* @author ontology bean generator
* @version 2014/05/24, 18:36:35
*/
public class RoomHotelInfo implements Concept {

   /**
* Protege name: cost
   */
   private float cost;
   public void setCost(float value) { 
    this.cost=value;
   }
   public float getCost() {
     return this.cost;
   }

   /**
* Protege name: roomsAvailable
   */
   private int roomsAvailable;
   public void setRoomsAvailable(int value) { 
    this.roomsAvailable=value;
   }
   public int getRoomsAvailable() {
     return this.roomsAvailable;
   }

}
