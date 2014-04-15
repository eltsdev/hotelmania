package emse.hotelmania.hotel;

import hotelmania.onto.Hotel;
import hotelmania.onto.RegistrationRequest;
import hotelmania.onto.SharedAgentsOntology;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgHotelWithOntology extends Agent 
{
	private static final long serialVersionUID = 2893904717857535232L;
	static final String HOTELMANIA = "HOTELMANIA";

	// Codec for the SL language used
    private Codec codec = new SLCodec();
    
    // External communication protocol's ontology
    private Ontology ontology = SharedAgentsOntology.getInstance();

    // Agent Attributes
	
	String name;
	AID agHotelmania;
	boolean registered;

	@Override
	protected void setup() 
	{
		System.out.println(getLocalName() + ": HAS ENTERED");

		// Register of the codec and the ontology to be used in the ContentManager
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);

		/**
		 * Registration in hotelmania
		 */
		addBehaviour(new SimpleBehaviour(this) 
		{
			private static final long serialVersionUID = 1256090117313507535L;
			private boolean hotelmaniaFound = false;
			
			@Override
			public void action() 
			{
				// Generate hotel name
				name = getName();
				
				// Search hotelmania agent
				agHotelmania = locateHotelmaniaAgent();

				// Register hotel
				if (hotelmaniaFound) {
					registerHotel();
				}
			}

			private void registerHotel() 
			{
				ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);  //TODO define pre.
				msg.addReceiver(agHotelmania);
				msg.setLanguage(codec.getName());
				msg.setOntology(ontology.getName());
				
				RegistrationRequest register = new RegistrationRequest();
				Hotel hotel = new Hotel();
				hotel.setHotel_name(name);
				register.setHotel(hotel);
				
				// As it is an action and the encoding language the SL, 
				// it must be wrapped into an Action
				Action agAction = new Action(agHotelmania, register);
				try
				{
					// The ContentManager transforms the java objects into strings
					getContentManager().fillContent(msg, agAction);
					send(msg);
					System.out.println(getLocalName()+": REQUESTS REGISTRATION");
				}
				catch (CodecException ce)
				{
					ce.printStackTrace();
				}
				catch (OntologyException oe)
				{
					oe.printStackTrace();
				}
			}

			/**
			 * Search hotelmania agent with the Directory Facilitator
			 * @return 
			 */
			private AID locateHotelmaniaAgent() 
			{
				DFAgentDescription dfd = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType(HOTELMANIA);//TODO define common type.
				dfd.addServices(sd);

				try {
					// It finds agents of the required type
					DFAgentDescription[] agents = DFService.search(myAgent, dfd);
					
					if (agents != null && agents.length > 0) {
						
						for (DFAgentDescription description : agents) {
							hotelmaniaFound = true;
							return (AID) description.getName(); // only expects 1 agent...
						}
					}
				} catch (Exception e) {
					hotelmaniaFound = false;
				}
				return null;
			}

			@Override
			public boolean done() {
				return hotelmaniaFound ;
			}
			
			private String getName()
			{
				return "myHOTEL-"+(int)Math.random()*10;
			}
		});

		/**
		 * Process acceptance messages 
		 */
		addBehaviour(new CyclicBehaviour(this)
		{
			private static final long serialVersionUID = -4878774871721189228L;

			public void action()
			{
				// Waits for acceptance messages
				ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.AGREE));
				if (msg != null)
				{
					// If an acceptance arrives...
					registered = true;
					System.out.println(myAgent.getLocalName()+": received registration acceptance from "+(msg.getSender()).getLocalName());
				}
				else
				{
					// If no message arrives
					block();
				}

			}

		});
		

		/**
		 * Process rejection messages 
		 */
		addBehaviour(new CyclicBehaviour(this)
		{
			private static final long serialVersionUID =1L;
			public void action()
			{
				// Waits for estimation rejections
				ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL));
				if (msg != null)
				{
					// If a rejection arrives...
					registered = false;
					System.out.println(myAgent.getLocalName()+": received work rejection from "+(msg.getSender()).getLocalName());
				}
				else
				{
					// If no message arrives
					block();
				}

			}

		});

		/**
		 * Process NOT UNDERSTOOD messages
		 */
		addBehaviour(new CyclicBehaviour(this)
		{
			private static final long serialVersionUID =1L;
			public void action()
			{
				// Waits for estimations not understood
				ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.NOT_UNDERSTOOD));
				if (msg != null)
				{
					// If a not understood message arrives...
					registered = false;
					System.out.println(myAgent.getLocalName()+": received NOT_UNDERSTOOD from "+(msg.getSender()).getLocalName());
				}
				else
				{
					// If no message arrives
					block();
				}

			}

		});
		
		
	}

}
