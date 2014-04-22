package hotelmania.onto;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: SignContract
* @author ontology bean generator
* @version 2014/04/20, 19:30:10
*/
public class SignContract implements AgentAction {

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

   /**
* Protege name: cookerStaff
   */
   private List cookerStaff = new ArrayList();
   public void addCookerStaff(Contract elem) { 
     List oldList = this.cookerStaff;
     cookerStaff.add(elem);
   }
   public boolean removeCookerStaff(Contract elem) {
     List oldList = this.cookerStaff;
     boolean result = cookerStaff.remove(elem);
     return result;
   }
   public void clearAllCookerStaff() {
     List oldList = this.cookerStaff;
     cookerStaff.clear();
   }
   public Iterator getAllCookerStaff() {return cookerStaff.iterator(); }
   public List getCookerStaff() {return cookerStaff; }
   public void setCookerStaff(List l) {cookerStaff = l; }

   /**
* Protege name: recepcionistStaff
   */
   private List recepcionistStaff = new ArrayList();
   public void addRecepcionistStaff(Contract elem) { 
     List oldList = this.recepcionistStaff;
     recepcionistStaff.add(elem);
   }
   public boolean removeRecepcionistStaff(Contract elem) {
     List oldList = this.recepcionistStaff;
     boolean result = recepcionistStaff.remove(elem);
     return result;
   }
   public void clearAllRecepcionistStaff() {
     List oldList = this.recepcionistStaff;
     recepcionistStaff.clear();
   }
   public Iterator getAllRecepcionistStaff() {return recepcionistStaff.iterator(); }
   public List getRecepcionistStaff() {return recepcionistStaff; }
   public void setRecepcionistStaff(List l) {recepcionistStaff = l; }

   /**
* Protege name: roomStaff
   */
   private Contract roomStaff;
   public void setRoomStaff(Contract value) { 
    this.roomStaff=value;
   }
   public Contract getRoomStaff() {
     return this.roomStaff;
   }

}
