package hotelmania.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: HotelStaffInfo
* @author ontology bean generator
* @version 2014/04/23, 10:59:43
*/
public class HotelStaffInfo implements Concept {

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
* Protege name: contract
   */
   private Contract contract;
   public void setContract(Contract value) { 
    this.contract=value;
   }
   public Contract getContract() {
     return this.contract;
   }

}
