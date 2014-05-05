package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Rating
* @author ontology bean generator
* @version 2014/05/5, 12:27:35
*/
public class Rating implements Concept {

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
* Protege name: cookers_rating
   */
   private float cookers_rating;
   public void setCookers_rating(float value) { 
    this.cookers_rating=value;
   }
   public float getCookers_rating() {
     return this.cookers_rating;
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
