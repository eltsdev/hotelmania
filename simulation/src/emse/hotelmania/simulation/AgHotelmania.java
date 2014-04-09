package emse.hotelmania.simulation;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgHotelmania extends Agent {

	private static final long serialVersionUID = -7762674314086577059L;
	private static final String HOTELMANIA = "HOTELMANIA";
	protected static final String REGISTER = "REGISTER";

	@Override
	protected void setup() {

		System.out.println(getLocalName() + ": has entered into the system");
		try {
			// Creates its own description
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setName(this.getName());
			sd.setType(HOTELMANIA);
			dfd.addServices(sd);
			// Registers its description in the DF
			DFService.register(this, dfd);
			System.out.println(getLocalName() + ": registered in the DF");
			dfd = null;
			sd = null;
			doWait(10000);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		// --------------------------------------------------------------------------
		// BEHAVIOURS
		// --------------------------------------------------------------------------

		// Adds a behavior to answer REGISTER requests
		// Waits for a request and, when it arrives, answers with
		// the ACCEPT/REJECT response and waits again.
		// If arrives a DECISION, it takes it

		addBehaviour(new CyclicBehaviour(this) 
		{
			private static final long serialVersionUID = 1L;

			public void action() {
				// Waits for estimation requests
				ACLMessage msg = receive(MessageTemplate
						.MatchPerformative(ACLMessage.QUERY_IF)); //TODO define
				if (msg != null) {
					if (REGISTER.equalsIgnoreCase(msg.getContent())) {
						// If a REGISTER request arrives, it answers: yes or
						// not.
						System.out.println(myAgent.getLocalName()
								+ ": received REGISTER request from "
								+ (msg.getSender()).getLocalName());
						ACLMessage reply = msg.createReply();

						String response = Boolean.TRUE.toString();


						reply.setContent(response);
						reply.setPerformative(ACLMessage.AGREE);  //TODO define
						myAgent.send(reply);
						System.out.println(myAgent.getLocalName()
								+ ": answer sent -> " + reply.getContent());
					}
				} else {
					// If no message arrives
					block();
				}

			}

		});

	}
}
