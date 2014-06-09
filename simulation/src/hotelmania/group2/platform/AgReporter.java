package hotelmania.group2.platform;

import hotelmania.group2.behaviours.SendReceiveBehaviour;
import hotelmania.group2.dao.RatingInput;
import hotelmania.group2.dao.ReportRecord;
import hotelmania.ontology.AccountStatus;
import hotelmania.ontology.HotelInformation;
import jade.content.ContentElement;
import jade.content.ContentElementList;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.lang.acl.ACLMessage;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class AgReporter extends AbstractAgent
{
	private static final long serialVersionUID = -4208905954219155107L;
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
		report = new HashMap<>();
	}
	
	@Override
	protected boolean setRegisterForDayEvents() {
		return false;
	}
	
	@Override
	public boolean doBeforeDie() {
		addBehaviour(new GetHotelsFromHotelmaniaBehavior(this));
		addBehaviour(new GetFinanceReportBehavior(this));
		return false;
	}
	
	// --------------------------------------------------------
	// Behaviors
	// --------------------------------------------------------
	
	private final class GetHotelsFromHotelmaniaBehavior extends SendReceiveBehaviour {
		private static final long serialVersionUID = -3455121495945698416L;

		public GetHotelsFromHotelmaniaBehavior(AbstractAgent agClient) {
			super(agClient, Constants.CONSULTHOTELSINFO_PROTOCOL, Constants.CONSULTHOTELSINFO_ACTION, ACLMessage.QUERY_REF);
		}
		
		@Override
		protected void receiveInform(ACLMessage msg) {
			handleConsultHotelsInfoInform(msg);
			ratingsDataReceived = true;
			checkIfDone();
		}
		
		private void handleConsultHotelsInfoInform(ACLMessage message) {
			try {
				ContentElement content = getContentManager().extractContent(message);
				if (content != null) {
					if (content instanceof ContentElementList) {
						ContentElementList list = (ContentElementList) content;
						Logger.logDebug(myName() + ": Number of hotels ratings: " + list.size());
						importRatingsList(list);
					} else if (content instanceof HotelInformation) {
						HotelInformation hotel = (HotelInformation) content;
						addRatingToReport(hotel.getHotel().getHotel_name(), hotel.getRating());
						Logger.logDebug(myName() + ": Number of hotels ratings: 1 = " + hotel.getHotel().getHotel_name());
					}
				} else {
					Logger.logDebug(myName() + ": Null number of hotels ratings");
				}
			} catch (CodecException | OntologyException e) {
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

		@Override
		protected boolean finishOrResend(int performativeReceived) {
			return performativeReceived==ACLMessage.INFORM;
		}
	}
	
	private final class GetFinanceReportBehavior extends SendReceiveBehaviour {
		private static final long serialVersionUID = -897415302551294781L;

		public GetFinanceReportBehavior(AbstractAgent agClient) {
			super(agClient, Constants.CONSULTFINANCEREPORT_PROTOCOL, Constants.CONSULTFINANCEREPORT_ACTION, ACLMessage.QUERY_REF);
		}
		
		@Override
		protected void receiveInform(ACLMessage msg) {
			handleConsultFinanceReportInform(msg);
			financeDataReceived = true;
			checkIfDone();
		}
		
		private void handleConsultFinanceReportInform(ACLMessage message) {
			try {
				ContentElement content = getContentManager().extractContent(message);
				if (content != null) {
					if (content instanceof ContentElementList) {
						ContentElementList list = (ContentElementList) content;
						Logger.logDebug(myName() + ": Number of hotel accounts: " + list.size());
						importAccountBalanceList(list);
						
					} else if (content instanceof AccountStatus) {
						AccountStatus financeStatus = (AccountStatus) content;
						addBalanceToReport(financeStatus.getAccount().getHotel().getHotel_name(), financeStatus.getAccount().getBalance());
						Logger.logDebug(myName() + ": Number of hotels accounts: 1 = " + financeStatus.getAccount().getHotel().getHotel_name());
					}
					
				} else {
					Logger.logDebug(myName() + ": Null number of hotels acounts");
				}
			} catch (CodecException | OntologyException e) {
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

		private void addBalanceToReport(String hotelName, float balance) {
			ReportRecord record = report.get(hotelName);
			if (record == null) {
				record = new ReportRecord();
				record.setHotel(hotelName);
			}
			record.setBalance(balance);
			report.put(hotelName, record);
		}

		@Override
		protected boolean finishOrResend(int performativeReceived) {
			return performativeReceived==ACLMessage.INFORM;
		}
	}
	
	public void checkIfDone() {
		if (this.ratingsDataReceived && this.financeDataReceived) {
			this.addCustomersToReport();
			
			String reportText = generateSimulationReport();
			printToFile(reportText, Constants.REPORT_FILE);
			System.out.println(reportText);
			
			Logger.printFile(Constants.LOG_FILE);
			
			//Die!
			doDelete();
		}
	}
	
	private void addCustomersToReport() {
		for (String hotel : this.report.keySet()) {
			//TODO Get clients from each hotel.
		}
		
	}

	public String generateSimulationReport() {
		double simulationTime = Constants.DAY*Constants.DAY_IN_MILLISECONDS/1000.0/60.0;
		
		StringBuilder r = new StringBuilder();
		r.append("SIMULATION RESULTS\n\n");
		r.append("Simulation period (minutes): ");
		r.append(simulationTime);
		r.append("\n");
		r.append("Number of clients generated: ");
		r.append(Constants.CLIENTS_GENERATED);
		r.append("\n");
		r.append("Clients budget range: ");
		r.append(Constants.CLIENTS_BUDGET-Constants.CLIENTS_BUDGET_VARIANCE);
		r.append(" - ");
		r.append(Constants.CLIENTS_BUDGET+Constants.CLIENTS_BUDGET_VARIANCE);
		r.append(" (EUR)\n");
		r.append("Participants: "+this.report.size());
		r.append("\n");
		r.append("Simulation days: ");
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
			e.printStackTrace();
		}
	}

}
