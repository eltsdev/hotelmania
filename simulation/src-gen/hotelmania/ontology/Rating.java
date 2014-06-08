package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Rating
* @author ontology bean generator
* @version 2014/06/8, 15:02:17
*/
public class Rating implements Concept {

   /**
* Protege name: chef_rating
   */
   private float chef_rating;
   public void setChef_rating(float value) { 
    this.chef_rating=value;
   }
   public float getChef_rating() {
     return this.chef_rating;
   }

   /**
* Protege name: price_rating
   */
   private float price_rating;
   public void setPrice_rating(float value) { 
    this.price_rating=value;
   }
   public float getPrice_rating() {
     return this.price_rating;
   }

   /**
* Protege name: cleanliness_rating
   */
   private float cleanliness_rating;
   public void setCleanliness_rating(float value) { 
    this.cleanliness_rating=value;
   }
   public float getCleanliness_rating() {
     return this.cleanliness_rating;
   }

   /**
* Protege name: room_staff_rating
   */
   private float room_staff_rating;
   public void setRoom_staff_rating(float value) { 
    this.room_staff_rating=value;
   }
   public float getRoom_staff_rating() {
     return this.room_staff_rating;
   }

}
