package com.nokona.reports;

import java.util.Map;

import javax.servlet.ServletContext;

public class JobReports {
	public static String construct(ServletContext context, ReportProperties properties, Map<String, Object> parms) {
		String templateFileName;
		if ("InProductionCostDetail".equals(properties.getReportName())) {
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/InProductionCostDetail.jrxml");
		
		} else {
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/InProductionCostDetail.jrxml");
		}
		return templateFileName;
	}
}
