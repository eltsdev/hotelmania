package hotelmania.group2.platform;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgentsFinder {
	
	private static AgentsFinder INSTANCE = new AgentsFinder();
	
	private AgentsFinder() {}

	public static AgentsFinder getInstance () {
		return INSTANCE;
	}
	
	public AID locateAgent(String type, Agent myAgent) {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		dfd.addServices(sd);

		try {
			// It finds agents of the required type
			DFAgentDescription[] agents = DFService.search(myAgent, dfd);

			if (agents != null && agents.length > 0) {

				for (DFAgentDescription description : agents) {
					//bankFound = true;
					return description.getName(); // only expects 1
					// agent...
				}
			}
		} catch (Exception e) {
			//bankFound = false;
		}
		return null;

	}
	
	/**
	 * 
	 */
	/*public AID locateBank(String type, Agent myAgent) {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		dfd.addServices(sd);

		try {
			// It finds agents of the required type
			DFAgentDescription[] agents = DFService.search(myAgent, dfd);

			if (agents != null && agents.length > 0) {

				for (DFAgentDescription description : agents) {
					//bankFound = true;
					return description.getName(); // only expects 1
					// agent...
				}
			}
		} catch (Exception e) {
			//bankFound = false;
		}
		return null;

	}*/

	/**
	 * @return
	 * 
	 * 
	 */
	/*public AID locateHotel(String type, Agent myAgent) {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		dfd.addServices(sd);

		try {
			// It finds agents of the required type
			DFAgentDescription[] agents = DFService.search(myAgent, dfd);

			if (agents != null && agents.length > 0) {

				for (DFAgentDescription description : agents) {
					//hotelFound = true;
					return description.getName(); // only expects 1
					// agent...
				}
			}
		} catch (Exception e) {
			//hotelFound = false;
		}
		return null;

	}*/

	/**
	 * @return
	 */
	/*public AID locateHotelMania(String type, Agent myAgent) {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		dfd.addServices(sd);

		try {
			// It finds agents of the required type
			DFAgentDescription[] agents = DFService.search(myAgent, dfd);

			if (agents != null && agents.length > 0) {

				for (DFAgentDescription description : agents) {
					return (AID) description.getName(); // only expects 1
														// agent...
				}
			}
		} catch (Exception e) {
		}
		return null;

	}*/

}
