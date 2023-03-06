package com.nokona.enums;


import java.util.HashSet;

import java.util.Set;

public enum LaborType {
	CLICKER("CLICKER"), STITCHER("STITCHER"), LACER("LACER"), MISCELLANEOUS("MISCELLANEOUS"), INDIRECT("INDIRECT"), UNKNOWN("UNKNOWN");

	private String laborType;
	private static final Set<String> values = new HashSet<>();
	static {
		for (LaborType value: LaborType.values()) {
			values.add(value.name());
		}
	}
	LaborType(String laborType) {
		this.laborType = laborType;
	}

	public String getLaborType() {
		return laborType;
	}
	public static boolean contains(String value) {
		return values.contains(value);
	}
}
