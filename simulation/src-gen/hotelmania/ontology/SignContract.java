package hotelmania.ontology;

import jade.content.AgentAction;

/**
 * Protege name: SignContract
 * 
 * @author ontology bean generator
 * @version 2014/04/23, 10:59:43
 */
public class SignContract implements AgentAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6017469199905185058L;
	private Hotel hotel;
	private Contract contract;


	/**
	 * @param value
	 */
	public void setHotel(Hotel value) {
		this.hotel = value;
	}

	/**
	 * @return
	 */
	public Hotel getHotel() {
		return this.hotel;
	}

	/**
	 * @param value
	 */
	public void setContract(Contract value) {
		this.contract = value;
	}

	/**
	 * @return
	 */
	public Contract getContract() {
		return this.contract;
	}

}
