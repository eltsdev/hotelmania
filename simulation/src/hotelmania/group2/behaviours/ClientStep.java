package hotelmania.group2.behaviours;

/**
 * The steps of a generic request-receive behavior
 */
public enum ClientStep {
	PREPARE, SEND, RECEIVE_RESPONSES, DONE, RESEND;
}