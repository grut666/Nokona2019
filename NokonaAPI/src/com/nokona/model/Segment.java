package com.nokona.model;

import java.util.List;

public class Segment {
	private long key;
	private String segmentName;
	private String segmentDescription;
	private List<SegmentDetail> details;
	
	public Segment() {
		super();
	}
	public Segment(long key, String segmentName, String segmentDescription, List<SegmentDetail> details) {
		super();
		this.setKey(key);
		this.setSegmentName(segmentName);
		this.setSegmentDescription(segmentDescription);
		this.setDetails(details);
	}
	public long getKey() {
		return key;
	}
	public void setKey(long key) {
		this.key = key;
	}
	public String getSegmentName() {
		return segmentName;
	}
	public void setSegmentName(String segmentName) {
		this.segmentName = segmentName;
	}
	public String getSegmentDescription() {
		return segmentDescription;
	}
	public void setSegmentDescription(String segmentDescription) {
		this.segmentDescription = segmentDescription;
	}
	public List<SegmentDetail> getDetails() {
		return details;
	}
	public void setDetails(List<SegmentDetail> details) {
		this.details = details;
	}
	@Override
	public String toString() {
		return "Segment [key=" + key + ", segmentName=" + segmentName + ", segmentDescription=" + segmentDescription
				+ ", details=" + details + "]";
	}	
}
