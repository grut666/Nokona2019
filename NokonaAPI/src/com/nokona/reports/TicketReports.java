package com.nokona.reports;

import java.util.Map;

import javax.servlet.ServletContext;

public class TicketReports {
	public static String construct(ServletContext context, ReportProperties properties, Map<String, Object> parms) {
		String templateFileName;
		String reportName = properties.getReportName();
		if (reportName.startsWith("TicketByDateRange")) {
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/" + reportName + ".jrxml");
			
			parms.put("START_DATE", properties.getStartDate());
			parms.put("END_DATE", properties.getEndDate());
			parms.put("STATUS", properties.getParameters().get("STATUS"));
			
		} else if (reportName.startsWith("TicketsByEmployee")) {
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/" + reportName + ".jrxml");
			System.err.println("***************** TicketsByEmployee ***************");
//			System.err.println("End Date: "  + properties.getEndDate());
//			System.err.println("Start Date: " +properties.getStartDate());
//			System.err.println("Bar Code ID: " + properties.getParameters().get("BAR_CODE_ID"));
			if (properties.getBarCodeId().equals("All")) {
				 parms.put("ALL", "All");
				 parms.put("BAR_CODE_ID", 0);
			} else {
				parms.put("ALL", "One");
				parms.put("BAR_CODE_ID", Integer.parseInt(properties.getParameters().get("BAR_CODE_ID")));
			}
			parms.put("START_DATE", properties.getStartDate());
			parms.put("END_DATE", properties.getEndDate());
			
		} else {
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/TicketWIP.jrxml");
		}
		return templateFileName;
	}
}
