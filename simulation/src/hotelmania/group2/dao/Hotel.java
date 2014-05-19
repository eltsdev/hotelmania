package hotelmania.group2.dao;

import jade.core.AID;

/**
 * @author user
 *
 */
public class Hotel {
	
	private String name;
	private AID agent;

	/**
	 * 
	 */
	public Hotel(hotelmania.ontology.Hotel hotel) {
		this.name = hotel.getHotel_name();
		this.agent = hotel.getHotelAgent();
	}
	
	/**
	 * @param hotelAgent 
	 * @param name
	 */
	public Hotel(String name, AID hotelAgent) {
		this.name = name ;
		this.agent = hotelAgent;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
		
	public AID getAgent() {
		return agent;
	}

	public void setAgent(AID agent) {
		this.agent = agent;
	}
	
	public hotelmania.ontology.Hotel getConcept() {
		hotelmania.ontology.Hotel concept = new hotelmania.ontology.Hotel();
		concept.setHotel_name(this.name);
		concept.setHotelAgent(this.getAgent());
		return concept;
	}
	

}
