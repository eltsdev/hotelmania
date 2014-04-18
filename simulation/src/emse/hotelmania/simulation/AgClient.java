package emse.hotelmania.simulation;

import hotelmania.onto.SharedAgentsOntology;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class AgClient extends Agent
{
	private static final long serialVersionUID = 6748170421157254696L;

	// Codec for the SL language used
	private Codec codec = new SLCodec();

	// External communication protocol's ontology
	private Ontology ontology = SharedAgentsOntology.getInstance();

	//------------------------------------------------- 
	// Agent Attributes
	//-------------------------------------------------

	String name;
	AID agHotelmania;
	boolean bookingDone;

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

		//TODO subscribe day event
	
		addBehaviour(new RequestBookingInHotelBehavior(this));
				
		//TODO refuse offer

	}
		
	// --------------------------------------------------------
	// Behaviors
	// --------------------------------------------------------

	private final class RequestBookingInHotelBehavior extends CyclicBehaviour 
	{
		private static final long serialVersionUID = -1417563883440156372L;

		private RequestBookingInHotelBehavior(Agent a) {
			super(a);
		}

		public void action() 
		{
			//TODO request hotel info
			
			//TODO ask room price *
			
			//TODO book room
			
			//TODO pay bank account
			
			//TODO rate hotel
			
			
			// If no message arrives
			block();
		}
	}
}