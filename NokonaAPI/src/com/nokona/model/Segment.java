package com.nokona.model;

import java.util.ArrayList;
import java.util.List;

public class Segment {
	private SegmentHeader segmentHeader;
	private List<SegmentDetail> segmentDetails;

	public Segment() {
		this(null, null);
	}

	public Segment(SegmentHeader segmentHeader, List<SegmentDetail> segmentDetails) {
		super();
		if (segmentHeader == null) {
			this.setSegmentHeader(new SegmentHeader());
		} else {
			this.setSegmentHeader(segmentHeader);
		}
		if (segmentDetails == null) {
			this.setSegmentDetails(new ArrayList<SegmentDetail>());
		} else {
			this.setSegmentDetails(segmentDetails);
		}
	}

	public SegmentHeader getSegmentHeader() {
		return segmentHeader;
	}

	public void setSegmentHeader(SegmentHeader segmentHeader) {
		this.segmentHeader = segmentHeader;
	}

	public List<SegmentDetail> getSegmentDetails() {
		return segmentDetails;
	}

	public void setSegmentDetails(List<SegmentDetail> segmentDetails) {
		this.segmentDetails = segmentDetails;
	}

	@Override
	public String toString() {
		return "Segment [SegmentHeader=" + segmentHeader + ", SegmentDetails=" + segmentDetails + "]";
	}

}
