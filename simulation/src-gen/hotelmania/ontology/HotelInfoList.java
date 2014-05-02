package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: HotelInfoList
* @author ontology bean generator
* @version 2014/05/2, 15:37:02
*/
public class HotelInfoList implements Concept {

   /**
* Protege name: hotels
   */
   private List hotels = new ArrayList();
   public void addHotels(HotelInfo elem) { 
     List oldList = this.hotels;
     hotels.add(elem);
   }
   public boolean removeHotels(HotelInfo elem) {
     List oldList = this.hotels;
     boolean result = hotels.remove(elem);
     return result;
   }
   public void clearAllHotels() {
     List oldList = this.hotels;
     hotels.clear();
   }
   public Iterator getAllHotels() {return hotels.iterator(); }
   public List getHotels() {return hotels; }
   public void setHotels(List l) {hotels = l; }

}
