package hotelmania.group2.platform;

import hotelmania.ontology.SharedAgentsOntology;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class AgReporter extends Agent
{
	private static final long serialVersionUID = -4208905954219155107L;

	// Codec for the SL language used
	private Codec codec = new SLCodec();

	// External communication protocol's ontology
	private Ontology ontology = SharedAgentsOntology.getInstance();

	//------------------------------------------------- 
	// Agent Attributes
	//-------------------------------------------------

	AID agClient;
	AID agHotel;
	AID agSimulator;
	AID agHotelmania;
	AID agBank;
	AID agAgency;

	//------------------------------------------------- 
	// Setup
	//-------------------------------------------------
	
	@Override
	protected void setup() 
	{
		System.out.println(getLocalName() + ": HAS ENTERED");

		// Register codec and ontology in ContentManager
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);

		// Behaviors
		
		addBehaviour(new GenerateSimulationReportBehavior(this));
		addBehaviour(new ObtainInformationFromAgentsBehavior(this));
	}
		
	// --------------------------------------------------------
	// Behaviors
	// --------------------------------------------------------
	
	private final class GenerateSimulationReportBehavior extends CyclicBehaviour 
	{
		private static final long serialVersionUID = 7520884931937975601L;

		private GenerateSimulationReportBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// If no message arrives
			block();
		}
	}

	private final class ObtainInformationFromAgentsBehavior extends CyclicBehaviour 
	{
		private static final long serialVersionUID = -3157976627925663055L;

		private ObtainInformationFromAgentsBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// If no message arrives
			block();
		}
	}

}