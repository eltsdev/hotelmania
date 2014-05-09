package hotelmania.group2.platform;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class MetaCyclicBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = 1L;
	
	protected String log = "";

	public MetaCyclicBehaviour(Agent a) {
		super(a);
	}

	@Override
	public void action() { }
	
}
