package hotelmania.group2.platform;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;

public class MetaSimpleBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = 8351134354988629861L;
	private boolean done;
	protected String log = "";

	public MetaSimpleBehaviour(Agent a) {
		super(a);
	}

	@Override
	public void action() { }

	@Override
	public boolean done() {
		return this.done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

}
