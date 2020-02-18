package com.nokona.formatter;

import com.nokona.model.JobHeader;

public class JobFormatter {

	public static JobHeader format(JobHeader jobHeader) {
		jobHeader.setJobId(formatJobId(jobHeader.getJobId()));
		jobHeader.setDescription(formatDescription(jobHeader.getDescription()));
		return jobHeader;
	}

	public static String formatJobId(String jobId) {
		return jobId.trim().toUpperCase();
	}

	public static String formatDescription(String description) {
		return description.trim().toUpperCase();
	}

}
