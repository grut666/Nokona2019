package com.nokona.reports;

public class ReportProcesser {
	private ReportProperties rp;

	public ReportProcesser(ReportProperties rp) {
		super();
		this.rp = rp;
	}

	public static String EmployeeReports(ReportProperties rp) {
		return "c:/codebase/Reports/output.pdf";
	}
	public static String LaborCodeReports(ReportProperties rp) {
		return "c:/codebase/Reports/output.pdf";
	}
	public static String OperationReports(ReportProperties rp) {
		return "c:/codebase/Reports/output.pdf";
	}
	public static String JobReports(ReportProperties rp) {
		return "c:/codebase/Reports/output.pdf";
	}
	public static String TicketReports(ReportProperties rp) {
		return "c:/codebase/Reports/output.pdf";
	}
}
