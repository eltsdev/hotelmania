package hotelmania.onto;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: Rating
* @author ontology bean generator
* @version 2014/04/20, 19:30:10
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
* Protege name: room_staff_rating
   */
   private float room_staff_rating;
   public void setRoom_staff_rating(float value) { 
    this.room_staff_rating=value;
   }
   public float getRoom_staff_rating() {
     return this.room_staff_rating;
   }

   /**
* Protege name: cookers_raring
   */
   private float cookers_raring;
   public void setCookers_raring(float value) { 
    this.cookers_raring=value;
   }
   public float getCookers_raring() {
     return this.cookers_raring;
   }

}
