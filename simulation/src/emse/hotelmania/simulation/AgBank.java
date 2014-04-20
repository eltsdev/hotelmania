package emse.hotelmania.simulation;

import hotelmania.onto.SharedAgentsOntology;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;

public class AgBank extends Agent 
{
	private static final long serialVersionUID = 2893904717857535232L;

	// Codec for the SL language used
	private Codec codec = new SLCodec();

	// External communication protocol's ontology
	private Ontology ontology = SharedAgentsOntology.getInstance();

	// Agent Attributes

	String name;
	AID agHotel;
	AID agAgency;
	AID agReporter;

	@Override
	protected void setup() 
	{
		System.out.println(getLocalName() + ": HAS ENTERED");

		// Register codec and ontology in ContentManager
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);

		// Create hotel account
		addBehaviour(new CreateAccountBehavior(this));

		//Charge Staff services in hotel account
		addBehaviour(new ChargeAccountBehavior(this));

		// Used by clients to pay to hotels
		addBehaviour(new MakeDepositBehavior(this));
		
		// Provide info account to hotel
		addBehaviour(new ProvideHotelAccountInfoBehavior(this));

	}

	// --------------------------------------------------------
	// BEHAVIOURS
	// --------------------------------------------------------

	private final class CreateAccountBehavior extends SimpleBehaviour 
	{
		private static final long serialVersionUID = 7390814510706022198L;
		
		public CreateAccountBehavior(AgBank agBankWithOntology) {
			super(agBankWithOntology);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private final class ProvideHotelAccountInfoBehavior extends CyclicBehaviour 
	{
		private static final long serialVersionUID = -4414753731149819352L;

		public ProvideHotelAccountInfoBehavior(AgBank agBankWithOntology) {
			super(agBankWithOntology);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
		}

	}

	private final class ChargeAccountBehavior extends CyclicBehaviour 
	{
		private static final long serialVersionUID = 5591566038041266929L;

		public ChargeAccountBehavior(AgBank agBankWithOntology) {
			super(agBankWithOntology);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
		}

	}
	
	private final class MakeDepositBehavior extends CyclicBehaviour 
	{
		private static final long serialVersionUID = 5591566038041266929L;

		public MakeDepositBehavior(AgBank agBankWithOntology) {
			super(agBankWithOntology);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
		}

	}

}