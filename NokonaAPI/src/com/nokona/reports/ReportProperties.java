package com.nokona.reports;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.nokona.enums.ReportCategory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@ToString(includeFieldNames=true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReportProperties {
	private ReportCategory category;  // EMPLOYEE, JOB, LABEL, OPERATION, TICKET
	private Map<String, String> parameters;
	private String reportName;
	private String startDate;
	private String endDate;
	private List<OrderBy> orderBys;	
	private String operatorId;
	private String jobId;
	private String operationId;
	private boolean csvNotHtml;
	private boolean summaryNotDetail;
}
