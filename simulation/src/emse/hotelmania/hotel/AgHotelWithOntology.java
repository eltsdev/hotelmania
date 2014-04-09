package emse.hotelmania.hotel;

import jade.content.onto.Ontology;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import emse.hotelmania.ontology.HotelmaniaOntology;

public class AgHotelWithOntology extends Agent {
	private static final long serialVersionUID = 2893904717857535232L;
	protected static final String HOTELMANIA = "HOTELMANIA";

	@Override
	protected void setup() {

		System.out.println(getLocalName() + ": HAS ENTERED");

		Ontology ontology = HotelmaniaOntology.getInstance();

		
		
//		for (Object p : ontology.getPredicateNames()) {
//			System.out.println("My predicate is: " + p);
//		}

		addBehaviour(new CyclicBehaviour(this) 
		{
			private static final long serialVersionUID = 1256090117313507535L;

			@Override
			public void action() {
				// Creates the description for the type of agent to be searched
				DFAgentDescription dfd = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType(HOTELMANIA);
				dfd.addServices(sd);

				try {

					// It finds agents of the required type
					DFAgentDescription[] res = new DFAgentDescription[20];
					res = DFService.search(myAgent, dfd);

				} catch (Exception e) {
				}

			}
		});
		
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
					System.out.println(myAgent.getLocalName()+": received work rejection from "+(msg.getSender()).getLocalName());
				}
				else
				{
					// If no message arrives
					block();
				}

			}

		});

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
