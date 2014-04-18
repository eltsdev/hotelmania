package emse.hotelmania.simulation;

import hotelmania.onto.SharedAgentsOntology;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;

public class AgSimulator extends Agent
{
	private static final long serialVersionUID = -4208905954219155107L;

	// Codec for the SL language used
	private Codec codec = new SLCodec();

	// External communication protocol's ontology
	private Ontology ontology = SharedAgentsOntology.getInstance();

	//------------------------------------------------- 
	// Agent Attributes
	//-------------------------------------------------

	private long dayLenght;

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
		
		addBehaviour(new SetTimeSpeedBehavior(this));
		addBehaviour(new ControlTimeBehavior(this));
		addBehaviour(new GeneratePlatformAgentsBehavior(this));
		addBehaviour(new GenerateClientsBehavior(this));
	}
		
	// --------------------------------------------------------
	// Behaviors
	// --------------------------------------------------------
	
	private final class SetTimeSpeedBehavior extends CyclicBehaviour 
	{
		private static final long serialVersionUID = -9078033789982364795L;

		private SetTimeSpeedBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// If no message arrives
			block();
		}
	}
	
	private final class ControlTimeBehavior extends CyclicBehaviour 
	{
		private static final long serialVersionUID = -9078033789982364796L;

		private ControlTimeBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// If no message arrives
			block();
		}
	}

	private final class GeneratePlatformAgentsBehavior extends SimpleBehaviour 
	{
		private static final long serialVersionUID = -9078033789982364797L;

		private GeneratePlatformAgentsBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// If no message arrives
			block();
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private final class GenerateClientsBehavior extends CyclicBehaviour 
	{
		private static final long serialVersionUID = 7520884931937975601L;

		private GenerateClientsBehavior(Agent a) {
			super(a);
		}

		public void action() {
			// If no message arrives
			block();
		}
	}

}
