package emse.hotelmania.simulation;

import hotelmania.onto.SharedAgentsOntology;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;

public class AgAgencyWithOntology extends Agent {
	private static final long serialVersionUID = 2893904717857535232L;

	static final String REGISTRATION = "Registration";

	// Codec for the SL language used
	private Codec codec = new SLCodec();

	// External communication protocol's ontology
	private Ontology ontology = SharedAgentsOntology.getInstance();

	// Agent Attributes

	String name;
	AID agHotel;
	AID agReporter;

	@Override
	protected void setup() {
		System.out.println(getLocalName() + ": HAS ENTERED");

		// Register codec and ontology in ContentManager
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);

		// Registration in hotelmania
		addBehaviour(new SignStaffContractWithHotelBehavior(this));

		// Process acceptance messages
		addBehaviour(new ProvideHotelStaffInfoToClientBehavior(this));

	}

	// --------------------------------------------------------
	// BEHAVIOURS
	// --------------------------------------------------------

	private final class SignStaffContractWithHotelBehavior extends SimpleBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7390814510706022198L;

		public SignStaffContractWithHotelBehavior(
				AgAgencyWithOntology agBankWithOntology) {
			super(agBankWithOntology);
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

	private final class ProvideHotelStaffInfoToClientBehavior extends
			CyclicBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4414753731149819352L;

		public ProvideHotelStaffInfoToClientBehavior(
				AgAgencyWithOntology agBankWithOntology) {
			super(agBankWithOntology);
		}

		@Override
		public void action() {
			// TODO Auto-generated method stub

		}

	}

}