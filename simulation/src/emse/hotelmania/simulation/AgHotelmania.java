package emse.hotelmania.simulation;

import hotelmania.onto.Hotel;
import hotelmania.onto.RegistrationRequest;
import hotelmania.onto.SharedAgentsOntology;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgHotelmania extends Agent 
{
	static final long serialVersionUID = -7762674314086577059L;
	static final String HOTELMANIA = "HOTELMANIA";
	static final String REGISTRATION = "Registration";

	// Codec for the SL language used
    private Codec codec = new SLCodec();
    
    // External communication protocol's ontology
    private Ontology ontology = SharedAgentsOntology.getInstance();


	@Override
	protected void setup() 
	{
		System.out.println(getLocalName() + ": HAS ENTERED");

		// Register of codec and ontology in the ContentManager
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);

		// Creates its own description
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setName(this.getName());
		sd.setType(HOTELMANIA);
		dfd.addServices(sd);
		
		try {	
			// Registers its description in the DF
			DFService.register(this, dfd);
			System.out.println(getLocalName() + ": registered in the DF");
			dfd = null;
			sd = null;
			doWait(10000);

			addBehaviour(new RegisterHotelBehavior(this));

			addBehaviour(new ReceiveNotUnderstoodMsgBehavior(this));

		} catch (FIPAException e) {
			// TODO handle
			e.printStackTrace();
		}

	}

	// --------------------------------------------------------
	// BEHAVIOURS
	// --------------------------------------------------------


	/**
	 * Adds a behavior to answer REGISTER requests
	 * Waits for a request and, when it arrives, answers with
	 * the ACCEPT/REJECT response and waits again.
	 * @author elts
	 */
	private final class RegisterHotelBehavior extends CyclicBehaviour 
	{
		private static final long serialVersionUID = 8713963422079295068L;
		
		private static final int VALID_REQ = 0;
		private static final int REJECT_REQ = -1;
		private static final int NOT_UNDERSTOOD_REQ = 1;

		private RegisterHotelBehavior(Agent a) {
			super(a);
		}
	
		public void action() 
		{
			// Look for messages
			ACLMessage msg = receive(
					MessageTemplate.and(
							MessageTemplate.and(
									MessageTemplate.MatchLanguage(codec.getName()), 
									MessageTemplate.MatchOntology(ontology.getName())),
									MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF))
					);
	
			// If no message arrives
			if (msg == null) 
			{
				block();					
				return;					
			}
	
			// The ContentManager transforms the message content (string) in java objects
			try {
				ContentElement ce = getContentManager().extractContent(msg);
				
				// We expect an action inside the message
				if (ce instanceof Action)
				{
					Action agAction = (Action) ce;
					Concept conc = agAction.getAction();
					
					// If the action is Registration Request...
					if (conc instanceof RegistrationRequest)
					{
						//execute request
						int answer = answerRegistrationRequest(msg, (RegistrationRequest) conc);
						
						//send reply
						ACLMessage reply = msg.createReply();
						String body = "";
						switch (answer) 
						{
						case VALID_REQ:
							body = Boolean.TRUE.toString();
							reply.setContent(body);
							//TODO define Agree or confirm?
							reply.setPerformative(ACLMessage.CONFIRM); 
							break;

						case REJECT_REQ:
							body = Boolean.FALSE.toString();
							reply.setContent(body);
							//TODO refuse ? disconfirm ?
							reply.setPerformative(ACLMessage.DISCONFIRM);  
							break;

						case NOT_UNDERSTOOD_REQ:
						default:
							body = Boolean.FALSE.toString();
							reply.setContent(body);
							//TODO refuse ? disconfirm ?
							reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);  
							break;
						}
						
						myAgent.send(reply);
						
						System.out.println(myAgent.getLocalName() + ": answer sent -> " + body);
					}
				}
				
			} catch (CodecException | OntologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
		/**
		 * Creates the response to registration request
		 * @param msg
		 * @param registrationRequestData 
		 * @return Answer type
		 */
		private int answerRegistrationRequest(ACLMessage msg, RegistrationRequest registrationRequestData) 
		{
			System.out.println(myAgent.getLocalName()+": received Registration Request from "+(msg.getSender()).getLocalName());

			if (registrationRequestData != null && registrationRequestData.getHotel() != null ) {
				if(registerNewHotel(registrationRequestData.getHotel()))
				{
					return 	VALID_REQ;
				}else {
					return REJECT_REQ;
				}
			}else {
				return NOT_UNDERSTOOD_REQ;
				
			}
		}

		private boolean registerNewHotel(Hotel hotel) {
			// TODO complete...
			return true;
		}

		
	}


	private final class ReceiveNotUnderstoodMsgBehavior extends CyclicBehaviour 
	{
		private static final long serialVersionUID =1L;
	
		private ReceiveNotUnderstoodMsgBehavior(Agent a) {
			super(a);
		}
	
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
	}
}
