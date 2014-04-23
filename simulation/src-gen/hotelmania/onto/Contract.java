package hotelmania.onto;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Describes the total employees to be hired (or actually hired) of each staff category.
* Protege name: Contract
* @author ontology bean generator
* @version 2014/04/23, 09:30:58
*/
public class Contract implements Concept {

   /**
* Protege name: recepcionist_experienced
   */
   private int recepcionist_experienced;
   public void setRecepcionist_experienced(int value) { 
    this.recepcionist_experienced=value;
   }
   public int getRecepcionist_experienced() {
     return this.recepcionist_experienced;
   }

   /**
* Protege name: room_service_staff
   */
   private int room_service_staff;
   public void setRoom_service_staff(int value) { 
    this.room_service_staff=value;
   }
   public int getRoom_service_staff() {
     return this.room_service_staff;
   }

   /**
* Protege name: cooker_3stars
   */
   private int cooker_3stars;
   public void setCooker_3stars(int value) { 
    this.cooker_3stars=value;
   }
   public int getCooker_3stars() {
     return this.cooker_3stars;
   }

   /**
* Protege name: cooker_2stars
   */
   private int cooker_2stars;
   public void setCooker_2stars(int value) { 
    this.cooker_2stars=value;
   }
   public int getCooker_2stars() {
     return this.cooker_2stars;
   }

   /**
* Protege name: cooker_1stars
   */
   private int cooker_1stars;
   public void setCooker_1stars(int value) { 
    this.cooker_1stars=value;
   }
   public int getCooker_1stars() {
     return this.cooker_1stars;
   }

   /**
* Protege name: recepcionist_novice
   */
   private int recepcionist_novice;
   public void setRecepcionist_novice(int value) { 
    this.recepcionist_novice=value;
   }
   public int getRecepcionist_novice() {
     return this.recepcionist_novice;
   }

}
