package com.nokona.enums;

public enum JobType {
	BASEBALL("B"), SOFTBALL("S"), OTHER("O"), UNKNOWN("U"), REPAIR("R"), CUSTOM("C"),
	CUSTOM_BASEBALL("CB"), CUSTOM_SOFTBALL("CS"), CUSTOM_OTHER("CO"), 
			B("B"), S("S"), O("O"), CB("CB"), CS("CS"), CO("CO"), R("R");
	private String jobType;

	JobType(String jobType) {
		this.jobType = jobType;
	}

	public String getJobType() {
		return jobType;
	}
}
