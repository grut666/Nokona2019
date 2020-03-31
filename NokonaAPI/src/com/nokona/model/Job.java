package com.nokona.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@AllArgsConstructor
@NoArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class Job {
	private JobHeader header;
	private List<JobDetail> details;
//	public Job(JobHeader header, List<JobDetail> details ) {
//		this.header = header;
//		this.details=details;
//	}
//	public JobHeader getHeader() {
//		return header;
//	}
//	public void setHeader(JobHeader header) {
//		this.header = header;
//	}
//	public List<JobDetail> getDetails() {
//		return details;
//	}
//	public void setDetails(List<JobDetail> details) {
//		this.details = details;
//	}
//	@Override
//	public String toString() {
//		return "Job [header=" + header + ", details=" + details + "]";
//	}
//
//
//	
}

