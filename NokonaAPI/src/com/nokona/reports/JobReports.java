package com.nokona.reports;

import java.util.Map;

import javax.servlet.ServletContext;

public class JobReports {
	public static String construct(ServletContext context, ReportProperties properties, Map<String, Object> parms) {
		String templateFileName;
		String reportName = properties.getReportName();
		if (reportName.startsWith("InProductionCostDetail")) {
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/" + reportName + ".jrxml");
			parms.put("Category", properties.getParameters().get("CATEGORY"));
			parms.put("All", properties.getParameters().get("ALL"));
		} else {
			parms.put("Category", properties.getParameters().get("CATEGORY"));
			parms.put("All", properties.getParameters().get("ALL"));
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/InProductionCostDetail.jrxml");
		}
		return templateFileName;
	}
}
