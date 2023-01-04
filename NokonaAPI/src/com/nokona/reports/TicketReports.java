package com.nokona.reports;

import java.util.Map;

import javax.servlet.ServletContext;

public class TicketReports {
	public static String construct(ServletContext context, ReportProperties properties, Map<String, Object> parms) {
		String templateFileName;
		if ("TicketByDateRange".equals(properties.getReportName())) {
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/TicketByDateRange.jrxml");
			parms.put("START_DATE", properties.getStartDate());
			parms.put("END_DATE", properties.getEndDate());
			parms.put("STATUS", properties.getParameters().get("STATUS"));
		} else {
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/TicketWIP.jrxml");
		}
		return templateFileName;
	}
}
