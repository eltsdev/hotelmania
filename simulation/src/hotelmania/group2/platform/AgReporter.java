package hotelmania.group2.platform;

import hotelmania.ontology.QueryHotelmaniaHotel;
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

	private final class ObtainInformationFromAgentsBehavior extends MetaSimpleBehaviour 
	{
		private static final long serialVersionUID = -3157976627925663055L;

		private AID agHotelmania;
		private AID agBank;

		private ObtainInformationFromAgentsBehavior(Agent a) {
			super(a);
		}

		public void action() {
			block();
//			TODO askForReportData();
//			generateSimulationReport();			
		}


		public void askForReportData() {
		QueryHotelmaniaHotel consult_request = new QueryHotelmaniaHotel();
		sendRequest(agHotelmania, consult_request,
				Constants.CONSULTHOTELSINFO_PROTOCOL, ACLMessage.QUERY_REF);
		}


		public void generateSimulationReport() {
			System.out.println("==================================================================");
			System.out.println("SIMULATION RESULTS");
			System.out.println("==================================================================");
			
			
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

	/* (non-Javadoc)
	 * @see hotelmania.group2.platform.MetaAgent#receiveInform()
	 */
	@Override
	public void receivedInform(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}
}