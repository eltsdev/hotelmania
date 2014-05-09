package hotelmania.group2.platform;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class AgReporter extends MetaAgent
{
	private static final long serialVersionUID = -4208905954219155107L;

	//------------------------------------------------- 
	// Setup
	//-------------------------------------------------
	
	@Override
	protected void setup() 
	{
		// Behaviors
		addBehaviour(new GenerateSimulationReportBehavior(this));
		addBehaviour(new ObtainInformationFromAgentsBehavior(this));
	}
	
	/**
	 * //TODO to decide
	 */
	@Override
	protected boolean setRegisterForDayEvents() {
		return false;
	}
	
	@Override
	protected void doOnNewDay() {
		//TODO blank.
	}

		
	// --------------------------------------------------------
	// Behaviors
	// --------------------------------------------------------
	
	private final class GenerateSimulationReportBehavior extends MetaCyclicBehaviour 
	{
		private static final long serialVersionUID = 7520884931937975601L;

		private GenerateSimulationReportBehavior(Agent a) {
			super(a);
		}

		public void action() {
			//TODO blank.
			
			//If no message arrives
			block();
		}
	}

	private final class ObtainInformationFromAgentsBehavior extends MetaCyclicBehaviour 
	{
		private static final long serialVersionUID = -3157976627925663055L;

		private AID agClient; //TODO many...
		private AID agHotel; //TODO many...
		private AID agSimulator;
		private AID agHotelmania;
		private AID agBank;
		private AID agAgency;

		private ObtainInformationFromAgentsBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// If no message arrives
			block();
		}
	}

	@Override
	public void receivedAcceptance(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receivedReject(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receivedNotUnderstood(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}

}