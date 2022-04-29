package com.nokona.enums;


import java.util.HashSet;

import java.util.Set;

public enum JobType {
	BASEBALL("B"), SOFTBALL("S"), OTHER("O"), UNKNOWN("U"), REPAIR("R"), CUSTOM("C"),
	CUSTOM_BASEBALL("CB"), CUSTOM_SOFTBALL("CS"), CUSTOM_OTHER("CO"), 
			C("C"), B("B"), S("S"), O("O"), CB("CB"), CS("CS"), CO("CO"), R("R"), U("U");
	private String jobType;
	private static final Set<String> values = new HashSet<>();
	static {
		for (JobType value: JobType.values()) {
			values.add(value.name());
		}
	}
	JobType(String jobType) {
		this.jobType = jobType;
	}

	public String getJobType() {
		return jobType;
	}
	public static boolean contains(String value) {
		return values.contains(value);
	}
}
