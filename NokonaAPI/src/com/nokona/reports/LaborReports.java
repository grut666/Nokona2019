package com.nokona.reports;

import java.util.Map;

import javax.servlet.ServletContext;

public class LaborReports {
	public static String construct(ServletContext context, ReportProperties properties, Map<String, Object> parms) {
		String templateFileName;

		String reportName = properties.getReportName();
		System.out.println("Report name is *********" + reportName);
		if (reportName.startsWith("LaborDetailByDate")) {
			parms.put("START_DATE", properties.getStartDate());
			parms.put("END_DATE", properties.getEndDate());
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/" + reportName + ".jrxml");
		} else if (reportName.startsWith("LaborCostDetail")) {
			System.out.println("Job is ********: "+ properties.getJobId());
			parms.put("JOB_ID", properties.getJobId());
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/" + reportName + ".jrxml");
		} else {
			parms.put("START_DATE", properties.getStartDate());
			parms.put("END_DATE", properties.getEndDate());
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/LaborDetailByDate.jrxml");
		}
		return templateFileName;
	}
}
