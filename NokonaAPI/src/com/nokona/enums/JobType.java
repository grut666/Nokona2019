package com.nokona.enums;

public enum JobType {
	BASEBALL("B"), SOFTBALL("S"), OTHER("O"), UNKNOWN(" "), 
	CUSTOM_BASEBALL("BZ"), CUSTOM_SOFTBALL("SZ"), CUSTOM_OTHER("OZ"), 
			B("B"), S("S"), O("O"), BZ("BZ"), SZ("SZ"), OZ("OZ");
	private String jobType;

	JobType(String jobType) {
		this.jobType = jobType;
	}

	public String getJobType() {
		return jobType;
	}
}
