package com.nokona.reports;

import java.util.Map;

import javax.servlet.ServletContext;

public class LaborReports {
	public static String construct(ServletContext context, ReportProperties properties, Map<String, Object> parms) {
		String templateFileName;

		String reportName = properties.getReportName();
		if (reportName.startsWith("LaborDetailByDate")) {
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/" + reportName + ".jrxml");
			parms.put("START_DATE", properties.getStartDate());
			parms.put("END_DATE", properties.getEndDate());
		} else {
			parms.put("START_DATE", properties.getStartDate());
			parms.put("END_DATE", properties.getEndDate());
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/LaborDetailByDate.jrxml");
		}
		return templateFileName;
	}
}
