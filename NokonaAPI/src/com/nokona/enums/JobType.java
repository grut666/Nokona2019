package com.nokona.enums;

public enum JobType {
	BASEBALL("B"), SOFTBALL("S"), OTHER("O"), UNKNOWN(" "), B("B"), S("S"), O("O");
	private String jobType;

	JobType(String jobType) {
		this.jobType = jobType;
	}

	public String getJobType() {
		return jobType;
	}
}
