package hotelmania.group2.platform;

import hotelmania.group2.dao.ReportRecord;
import hotelmania.ontology.AccountStatus;
import hotelmania.ontology.GetFinanceReport;
import hotelmania.ontology.HotelInformation;
import jade.content.ContentElement;
import jade.content.ContentElementList;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class AgReporter extends MetaAgent
{
	private static final long serialVersionUID = -4208905954219155107L;

	private AID agHotelmania;
	private AID agBank;
	private boolean ratingsDataReceived;
	private boolean financeDataReceived;
	private HashMap<String, ReportRecord> report;

	//------------------------------------------------- 
	// Setup
	//-------------------------------------------------
	
	@Override
	protected void setup() 
	{
		super.setup();
		// Behaviors
		addBehaviour(new LocateAgentsBehavior(this));
		report = new HashMap<>();
	}
	
	@Override
	protected boolean setRegisterForDayEvents() {
		return false;
	}
		
	// --------------------------------------------------------
	// Behaviors
	// --------------------------------------------------------

	private final class LocateAgentsBehavior extends MetaSimpleBehaviour 
	{
		private static final long serialVersionUID = -3157976627925663055L;

		private LocateAgentsBehavior(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			if (agHotelmania == null) {
				agHotelmania = locateAgent(Constants.CONSULTHOTELSINFO_ACTION, myAgent);
			}

			if (agBank== null) {
				agBank = locateAgent(Constants.CONSULTFINANCEREPORT_ACTION, myAgent);
			}

			if (agBank != null && agHotelmania!= null) {
				this.setDone(true);
			}
		}
	}
	
	@Override
	public boolean doBeforeDie() {
		// RequestReportDataBehavior
		this.consultFinanceReport();
		this.consultHotelInfo(); 
		return false;
	}

	public void consultHotelInfo() {
		sendRequestEmpty(agHotelmania, 
				Constants.CONSULTHOTELSINFO_PROTOCOL, ACLMessage.QUERY_REF);
	}

	private void consultFinanceReport() {
		GetFinanceReport consult_request = new GetFinanceReport();
		sendRequest(agBank, consult_request,
				Constants.CONSULTFINANCEREPORT_PROTOCOL, ACLMessage.QUERY_REF);
	}

	//---------------------------------------------------
	// Reception of responses
	//---------------------------------------------------
	
	@Override
	public void receivedAcceptance(ACLMessage message) {
		
	}

	@Override
	public void receivedReject(ACLMessage message) {
		
	}

	@Override
	public void receivedNotUnderstood(ACLMessage message) {
		
	}

	@Override
	public void receivedInform(ACLMessage message) {
		if (message.getProtocol().equals(Constants.CONSULTFINANCEREPORT_PROTOCOL)) {
			handleConsultFinanceReportInform(message);
			this.financeDataReceived = true;
		}
		
		else if (message.getProtocol().equals(Constants.CONSULTHOTELSINFO_PROTOCOL)) {
			handleConsultHotelsInfoInform(message);
			this.ratingsDataReceived = true;
		}
		
		if (this.ratingsDataReceived && this.financeDataReceived) {
			String reportText = generateSimulationReport();
			printToFile(reportText, Constants.REPORT_FILE);
			System.out.println(reportText);
			
			//Die!
			doDelete();
		}
	}
	
	private void handleConsultFinanceReportInform(ACLMessage message) {
		try {
			ContentElement content = getContentManager().extractContent(message);
			if (content != null) {
				if (content instanceof ContentElementList) {
					ContentElementList list = (ContentElementList) content;
					Logger.logDebug(myName() + ": Number of hotel accounts: " + list.size());
					this.importAccountBalanceList(list);
					
				} else if (content instanceof AccountStatus) {
					AccountStatus financeStatus = (AccountStatus) content;
					addBalanceToReport(financeStatus.getAccount().getHotel().getHotel_name(), financeStatus.getAccount().getBalance());
					Logger.logDebug(myName() + ": Number of hotels accounts: 1 = " + financeStatus.getAccount().getHotel().getHotel_name());
				}
				
			} else {
				Logger.logDebug(myName() + ": Null number of hotels acounts");
			}
		} catch (CodecException | OntologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.logDebug("Message: " + message.getContent());
		}
	}

	private void importAccountBalanceList(ContentElementList list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof AccountStatus) {
				AccountStatus financeStatus = (AccountStatus) list.get(i);
				addBalanceToReport(financeStatus.getAccount().getHotel().getHotel_name(), financeStatus.getAccount().getBalance());
			}

		}
	}

	private void handleConsultHotelsInfoInform(ACLMessage message) {
		try {
			ContentElement content = getContentManager().extractContent(message);
			if (content != null) {
				if (content instanceof ContentElementList) {
					ContentElementList list = (ContentElementList) content;
					Logger.logDebug(myName() + ": Number of hotels ratings: " + list.size());
					this.importRatingsList(list);
				} else if (content instanceof HotelInformation) {
					HotelInformation hotel = (HotelInformation) content;
					addRatingToReport(hotel.getHotel().getHotel_name(), hotel.getRating());
					Logger.logDebug(myName() + ": Number of hotels ratings: 1 = " + hotel.getHotel().getHotel_name());
				}
			} else {
				Logger.logDebug(myName() + ": Null number of hotels ratings");
			}
		} catch (CodecException | OntologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.logDebug("Message: " + message.getContent());
		}
	}

	private void importRatingsList(ContentElementList list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof HotelInformation) {
				HotelInformation hotel = (HotelInformation) list.get(i);
				addRatingToReport(hotel.getHotel().getHotel_name(), hotel.getRating());
			}

		}
	}

	private void addRatingToReport(String hotelName, float rating) {
		ReportRecord record = report.get(hotelName);
		if (record == null) {
			record = new ReportRecord();
			record.setHotel(hotelName);
		}
		record.setRating(rating);
		report.put(hotelName, record);
	}
	
	private void addBalanceToReport(String hotelName, float balance) {
		ReportRecord record = report.get(hotelName);
		if (record == null) {
			record = new ReportRecord();
			record.setHotel(hotelName);
		}
		record.setBalance(balance);
		report.put(hotelName, record);
	}

	public String generateSimulationReport() {
		StringBuilder r = new StringBuilder();
		r.append("SIMULATION RESULTS\n\n");
		r.append("Simulation period: ");
		r.append(Constants.SIMULATION_DAYS);
		r.append("\n");
		r.append("Number of clients generated: ");
		r.append(Constants.CLIENTS_PER_DAY*Constants.SIMULATION_DAYS);
		r.append("\n");
		r.append("Clients budget range: ");
		r.append(Constants.CLIENTS_BUDGET-Constants.CLIENTS_BUDGET_VARIANCE);
		r.append(" - ");
		r.append(Constants.CLIENTS_BUDGET+Constants.CLIENTS_BUDGET_VARIANCE);
		r.append(" (EUR)\n");
		r.append("Participants: "+this.report.size()); //FIXME this is wrong!
		r.append(Constants.SIMULATION_DAYS);
		r.append("\n\n");
		
		r.append("Hotel\t\t\tRating\t\tBalance\t\t# Clients\n"+
				 "------\t\t\t------\t\t--------\t\t----------\n");
		for (ReportRecord record : this.report.values()) {
			r.append(record.toString());
			r.append("\n");
		}
		return r.toString();
	}

	private void printToFile(String data, String fileName) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(fileName, "UTF-8");
			writer.println(data);
			writer.close();	
			Logger.logDebug("SIMULATION REPORT GENERATED: "+fileName);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			Logger.logDebug("SIMULATION REPORT FAILED TO WRITE IN: "+fileName);

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}