package hotelmania.group2.dao;

import hotelmania.group2.platform.ClientGenerator;
import hotelmania.group2.platform.Constants;

public class Contract {
	String hotelName;
	int date;
	int chef1stars = 0;
	int chef2stars = 0;
	int chef3stars = 0;
	int recepcionistExperienced = 0;
	int recepcionistNovice = 0;
	int roomService = 0;

	public Contract() {
		this.date = 0;
	}

	public Contract(String hotelName, int date, int chef1stars, int chef2stars,
			int chef3stars, int recepcionistExperienced,
			int recepcionistNovice, int roomService) {
		super();
		this.hotelName = hotelName;
		this.date = date;
		this.chef1stars = chef1stars;
		this.chef2stars = chef2stars;
		this.chef3stars = chef3stars;
		this.recepcionistExperienced = recepcionistExperienced;
		this.recepcionistNovice = recepcionistNovice;
		this.roomService = roomService;
	}

	public Contract(hotelmania.ontology.Contract contract) {
		super();
		// this.hotelName = hotelName;
		this.date = contract.getDay();
		this.chef1stars = contract.getChef_1stars();
		this.chef2stars = contract.getChef_2stars();
		this.chef3stars = contract.getChef_3stars();
		this.recepcionistExperienced = contract.getRecepcionist_experienced();
		this.recepcionistNovice = contract.getRecepcionist_novice();
		this.roomService = contract.getRoom_service_staff();
	}

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public int getchef1stars() {
		return chef1stars;
	}

	public void setchef1stars(int chef1stars) {
		this.chef1stars = chef1stars;
	}

	public int getchef2stars() {
		return chef2stars;
	}

	public void setchef2stars(int chef2stars) {
		this.chef2stars = chef2stars;
	}

	public int getchef3stars() {
		return chef3stars;
	}

	public void setchef3stars(int chef3stars) {
		this.chef3stars = chef3stars;
	}

	public int getRecepcionistExperienced() {
		return recepcionistExperienced;
	}

	public void setRecepcionistExperienced(int recepcionistExperienced) {
		this.recepcionistExperienced = recepcionistExperienced;
	}

	public int getRecepcionistNovice() {
		return recepcionistNovice;
	}

	public void setRecepcionistNovice(int recepcionistNovice) {
		this.recepcionistNovice = recepcionistNovice;
	}

	public int getRoomService() {
		return roomService;
	}

	public void setRoomService(int roomService) {
		this.roomService = roomService;
	}

	public hotelmania.ontology.Contract getConcept() {
		hotelmania.ontology.Contract contract = new hotelmania.ontology.Contract();
		contract.setChef_1stars(this.chef1stars);
		contract.setChef_2stars(this.chef2stars);
		contract.setChef_3stars(this.chef3stars);
		contract.setRecepcionist_experienced(this.recepcionistExperienced);
		contract.setRecepcionist_novice(this.recepcionistNovice);
		contract.setRoom_service_staff(this.roomService);
		contract.setDay(this.date);
		return contract;
	}

	public float getCost() {
		float cost = 0;
		cost += this.chef1stars * Constants.chef1StarCost;
		cost += this.chef2stars * Constants.chef2StarCost;
		cost += this.chef3stars * Constants.chef3StarCost;
		cost += this.recepcionistExperienced
				* Constants.recepcionistExperiencedCost;
		cost += this.recepcionistNovice * Constants.recepcionistNoviceCost;
		cost += this.roomService * Constants.roomServiceCost;
		return cost;
	}

	public void increaseQuality(float budget) {
		int random;
		if (this.chef3stars > 0) {
			random = ClientGenerator.randomBetween(1, 2);

		} else {
			random = ClientGenerator.randomBetween(1, 3);
		}

		switch (random) {
		case 1: // Recepcionist
			this.increaseCurrentRecepcionist(budget);
			break;
		case 2: // Room Services
			this.increaseCurrentRoomServices(budget);
			break;
		case 3:// Cheff
			this.increaseCurrentChef(budget);
			break;
		default:
			break;
		}

	}

	public void increaseCurrentRecepcionist(float budget) {
		if(this.recepcionistExperienced<this.recepcionistNovice){
			if (budget>=Constants.recepcionistExperiencedCost) {
				int numberOfNewExperencedRecepcionist = (int) Math.floor(budget/Constants.recepcionistExperiencedCost);
				this.recepcionistExperienced += numberOfNewExperencedRecepcionist;
			}
		} else {
			if (budget>=Constants.recepcionistNoviceCost) {
				int numberOfNewNoviceRecepcionist = (int) Math.floor(budget/Constants.recepcionistNoviceCost);
				this.recepcionistNovice += numberOfNewNoviceRecepcionist;
			}
		}
	}

	public void increaseCurrentRoomServices(float budget) {
		int numberOfNewRoomServices = (int) Math.floor(budget/Constants.roomServiceCost);
		this.roomService += numberOfNewRoomServices;
	}

	public void increaseCurrentChef(float budget) {
		float cost;
		if(this.chef1stars>0){
			cost = Constants.chef2StarCost - Constants.chef1StarCost;
			if (cost <= budget) {
				this.chef1stars = 0;
				this.chef2stars = 1;
			}
		}else if(this.chef2stars>0){
			cost = Constants.chef3StarCost - Constants.chef2StarCost;
			if (cost <= budget) {
				this.chef2stars = 0;
				this.chef3stars = 1;
			}
		}
	}


	public void decreaseQuality(float budget) {
		if (this.canBeDecreased()) {
			boolean decrementFound = false;
			while (!decrementFound) {
				int maxRandom = 3;
				if (this.chef1stars > 1) {
					maxRandom = 2;
				}
				if (this.roomService <= 1) {
					maxRandom = 1;
				}
				int random = ClientGenerator.randomBetween(1, maxRandom);
				switch (random) {
				case 1: // Recepcionist
					this.decreaseCurrentRecepcionist(budget);
					decrementFound = true;
					break;
				case 2: // Room Services
					this.decreaseCurrentRoomServices(budget);
					decrementFound = true;
					break;
				case 3:// Cheff
					this.decreaseCurrentChef(budget);
					decrementFound = true;
					break;
				default:
					break;
				}
			}
		}
		
	}
	
	private boolean canBeDecreased() {
		if (this.chef3stars > 0 || this.chef2stars > 0) {
			return true;
		}
		
		if (this.roomService > 1) {
			return true;
		}
		
		if (this.recepcionistExperienced > 0 || this.recepcionistNovice > 1) {
			return true;
		}
		return false;
	}
	
	public void decreaseCurrentRecepcionist(float budget) {
		if (this.recepcionistExperienced > 0) {
			if (budget>=Constants.recepcionistExperiencedCost) {
				int numberOfNewExperencedRecepcionist = (int) Math.floor(budget/Constants.recepcionistExperiencedCost);
				this.recepcionistExperienced -= numberOfNewExperencedRecepcionist;
			}
		} else if (this.recepcionistNovice > 1) {
			int maxNumberOfRecepcionist = this.recepcionistNovice - 1;
			if (budget>=Constants.recepcionistNoviceCost) {
				int numberOfNewNoviceRecepcionist = (int) Math.floor(budget/Constants.recepcionistNoviceCost);
				this.recepcionistNovice -= Math.min(maxNumberOfRecepcionist, numberOfNewNoviceRecepcionist);
			}
		}
	}

	public void decreaseCurrentRoomServices(float budget) {
		int maxNumberOfRoomServices = this.roomService - 1;
		int numberOfNewRoomServices = (int) Math.floor(budget/Constants.roomServiceCost);
		this.roomService -= Math.min(numberOfNewRoomServices, maxNumberOfRoomServices);
	}

	public void decreaseCurrentChef(float budget) {
		float cost;
		if(this.chef3stars>0){
			cost = Constants.chef3StarCost - Constants.chef2StarCost;
			if (cost <= budget) {
				this.chef3stars = 0;
				this.chef2stars = 1;
			}
		}else if(this.chef2stars>0){
			cost = Constants.chef2StarCost - Constants.chef1StarCost;
			if (cost <= budget) {
				this.chef2stars = 0;
				this.chef1stars = 1;
			}
		}
	}
	
	@Override
	public String toString() {
		String c = "chef1stars:" +chef1stars +"; "
				+"chef2stars:" +chef2stars +"; "
				+"chef3stars:" +chef3stars +"; "
				+"roomService:" +roomService +"; "
				+"recepcionistExperienced: " + recepcionistExperienced +";" 
				+"recepcionistNovice: " + recepcionistNovice +";";
		return c;
	}

}
