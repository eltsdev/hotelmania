package emse.hotelmania.hotel;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AgSimpleHotel extends Agent 
{
	private static final long serialVersionUID = -5700066962738596576L;
	protected static final String HOTELMANIA = "HOTELMANIA";
	protected static final String REGISTER = "REGISTER";

	boolean registered;
	
	@Override
	protected void setup() 
	{
		System.out.println(getLocalName()+": HAS ENTERED");

		registered = false;

		// --------------------------------------------------------------------------
		// BEHAVIOURS
		// --------------------------------------------------------------------------

		// Adds a behavior to register in hotelmania
		// IF yes, continue living
		// ELSE, die
		addBehaviour(new SimpleBehaviour(this)
		{
			private static final long serialVersionUID =1L;
			boolean end = false;
			AID[] painters = new AID[20];
			AID ag;
			boolean ignore = false;
			int last = 0;
			int i, j;

			public void action() 
			{   
				// Creates the description for the type of agent to be searched
				DFAgentDescription dfd = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType(HOTELMANIA);
				dfd.addServices(sd);

				try
				{

					// It finds agents of the required type
					DFAgentDescription[] res = new DFAgentDescription[20];
					res = DFService.search(myAgent, dfd);

					// Gets the first occurrence, if there was success
					if (res.length > 0)
					{
						for (i=0; i < res.length; i++)
						{
							ag = (AID)res[i].getName();

							for (j=0; j<last; j++)
							{
								if (painters[j].compareTo(ag) == 0)
								{
									ignore = true;
								}
							}
							if (!ignore)
							{
								painters[last++] = ag;
								// Asks for registration
								ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
								msg.setContent(REGISTER);
								msg.addReceiver(ag);
								send(msg);
								System.out.println(getLocalName()+": ASKS FOR REGISTRATION");
							}
							ignore = false;
						}
						doWait(5000);
					}
					else
					{
						// If no HOTELMANIA has been found, it waits 5 seconds
						doWait(5000);
					}
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}

			public boolean done ()
			{
				return end;
			}

		});


	}
}
