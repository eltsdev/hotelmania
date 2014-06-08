package hotelmania.group2.dao;

import java.util.ArrayList;
import java.util.List;

public class ContractDAO {
	
	private List<Contract> contracts = new ArrayList<Contract>();
	
	public void createContract(Contract contract) {
		this.contracts.add(contract);
	}

	/**
	 * @param hotelName
	 * @param day
	 * @return Return the actual contract for the day asking for
	 */
	public Contract getCurrentContractsByHotel(String hotelName, int day) {
		for (Contract contract : contracts) {
			if (contract.getHotelName().equals(hotelName) && contract.getDate() == day) {
				return contract;
			}
		}
		return null;
	}

	public List<Contract> getAllContractsByHotel(String hotelName) {
		List<Contract> result = new ArrayList<Contract>();
		for (Contract contract : contracts) {
			if (contract.getHotelName().equals(hotelName)) {
				result.add(contract);
			}
		}
		return result;
	}

}
