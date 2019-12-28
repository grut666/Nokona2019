package com.nokona.model;

public class SegmentDetail {
	private String segmentName;
	private String opCode;
	private int sequence;
	public SegmentDetail() {
		super();
	}
	public SegmentDetail(String segmentName, String opCode, int sequence) {
		super();
		this.setSegmentName(segmentName);
		this.setOperation(opCode);
		this.setSequence(sequence);
	}

	public String getSegmentName() {
		return segmentName;
	}
	public void setSegmentName(String segmentName) {
		this.segmentName = segmentName;
	}
	public String getOpCode() {
		return opCode;
	}
	public void setOperation(String opCode) {
		this.opCode = opCode;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	@Override
	public String toString() {
		return "SegmentDetail [segmentName=" + segmentName + ", opCode=" + opCode + ", sequence=" + sequence
				+ "]";
	}

	
	
}
