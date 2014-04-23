package emse.hotelmania.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContractDAO 
{
	List<Contract> contracts;

	public ContractDAO() 
	{
		this.contracts = new ArrayList<Contract>();
	}
	
	public void createContract(Contract c)
	{
		contracts.add(c);
	}

	public List<Contract> getCurrentContractsByHotel(String hotelName) 
	{
		List<Contract> result = new ArrayList<Contract>();
		Date targetDate = new Date();
		for (Contract c : contracts) {
			if (c.getHotelName().equals(hotelName) && c.getDate().equals(targetDate)) {
				result.add(c);
			}
		}
		return result;
	}
	
	public List<Contract> getAllContractsByHotel(String hotelName) 
	{
		List<Contract> result = new ArrayList<Contract>();
		for (Contract c : contracts) {
			if (c.getHotelName().equals(hotelName)) {
				result.add(c);
			}
		}
		return result;
	}

}
