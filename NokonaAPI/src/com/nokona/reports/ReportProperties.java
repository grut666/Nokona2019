package com.nokona.reports;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@ToString(includeFieldNames=true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReportProperties {
	private Date startDate;
	private Date endDate;
	private List<OrderBy> orderBys;
	private boolean isSummaryNotDetail;
	private boolean isPdfNotExcel;
	private String operatorId;
	private String jobId;
	
}
