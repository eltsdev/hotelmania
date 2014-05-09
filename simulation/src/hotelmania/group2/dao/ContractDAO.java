package hotelmania.group2.dao;

import java.util.ArrayList;
import java.util.List;

public class ContractDAO {
	
	private List<Contract> contracts = new ArrayList<Contract>();
	
	public void createContract(Contract c) {
		contracts.add(c);
	}

	public List<Contract> getCurrentContractsByHotel(String hotelName, int day) {
		List<Contract> result = new ArrayList<Contract>();
		for (Contract c : contracts) {
			if (c.getHotelName().equals(hotelName) && c.getDate() == day) {
				result.add(c);
			}
		}
		return result;
	}

	public List<Contract> getAllContractsByHotel(String hotelName) {
		List<Contract> result = new ArrayList<Contract>();
		for (Contract c : contracts) {
			if (c.getHotelName().equals(hotelName)) {
				result.add(c);
			}
		}
		return result;
	}

}
