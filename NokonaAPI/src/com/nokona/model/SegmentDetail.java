package com.nokona.model;

public class SegmentDetail {
	private long segmentKey;
	private String operation;
	private int sequence;
	public SegmentDetail() {
		super();
	}
	public SegmentDetail(long segmentKey, String operation, int sequence) {
		super();
		this.setSegmentKey(segmentKey);
		this.setOperation(operation);
		this.setSequence(sequence);
	}
	public long getSegmentKey() {
		return segmentKey;
	}
	public void setSegmentKey(long segmentKey) {
		this.segmentKey = segmentKey;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	@Override
	public String toString() {
		return "SegmentDetail [segmentKey=" + segmentKey + ", operation=" + operation + ", sequence=" + sequence + "]";
	}
	
	
}
