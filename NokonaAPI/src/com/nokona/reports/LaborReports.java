package com.nokona.reports;

import java.util.Map;

import javax.servlet.ServletContext;

public class LaborReports {
	public static String construct(ServletContext context, ReportProperties properties, Map<String, Object> parms) {
		String templateFileName;
		if ("LaborDetailByDate".equals(properties.getReportName())) {
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/LaborDetailByDate.jrxml");
			parms.put("START_DATE", properties.getStartDate());
			parms.put("END_DATE", properties.getEndDate());
		} else {
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/TicketWIP.jrxml");
		}
		return templateFileName;
	}
}
