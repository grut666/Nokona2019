package com.nokona.enums;

public enum SegmentStatus {
	PRODUCTION("P"), DELETED("D");
	private String segmentStatus;

	SegmentStatus(String segmentStatus) {
		this.segmentStatus = segmentStatus;
	}

	public String getSegmentStatus() {
		return segmentStatus;
	}
}
