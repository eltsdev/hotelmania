package hotelmania.group2.platform;

import java.util.HashMap;

public class AgentState {

	public enum State {
		LOADED, ENDSIMULATION_SUBSCRIBED, RECEIVED_ENDSIMULATION_NOTIFICATION, DAYEVENT_SUBSCRIBED, RECEIVED_DAY_NOTIFICATION, REGISTERED_HOTELMANIA, ACCOUNT_CREATED
	}

	private HashMap<State, Boolean> states;

	private boolean logEnabled = false;
	
	private String agent;
	
	public AgentState(boolean logEnabled, String agent) {
		super();
		this.logEnabled = logEnabled;
		this.agent = agent;
		this.states = new HashMap<>();
		for (State s : State.values()) {
			this.states.put(s, null);
		}
	}

	public void check(State newState) {
		states.put(newState, true);
		if (logEnabled) {
			Logger.logDebug(toString());
		}
	}

	public void uncheck(State newState) {
		states.put(newState, false);
		if (logEnabled) {
			Logger.logDebug(toString());
		}
	}

	@Override
	public String toString() {
		String output = "";

		for (State s : states.keySet()) {
			output += s.name() + ":" + states.get(s) + "; ";
		}

		return getAgent() + ":\t [AGENTSTATUS] = " + (output == "" ? "UNKNOWN" : output);
	}

	public boolean isLogEnabled() {
		return logEnabled;
	}

	public void setLogEnabled(boolean logEnabled) {
		this.logEnabled = logEnabled;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}
}