package com.nokona.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class JobDetail {
	private String jobId;
	private String opCode;
	private int sequence;
//	public JobDetail() {
//		super();
//	}
//	public JobDetail(String jobId, String opCode, int sequence) {
//		super();
//		this.setJobId(jobId);
//		this.setOpCode(opCode);
//		this.setSequence(sequence);
//	}
//
//	public String getJobId() {
//		return jobId;
//	}
//	public void setJobId(String jobId) {
//		this.jobId = jobId;
//	}
//	public void setOpCode(String opCode) {
//		this.opCode = opCode;
//	}
//	public String getOpCode() {
//		return opCode;
//	}
//	public int getSequence() {
//		return sequence;
//	}
//	public void setSequence(int sequence) {
//		this.sequence = sequence;
//	}
//	@Override
//	public String toString() {
//		return "JobDetail [opCode=" + opCode + ", sequence=" + sequence
//				+ "]";
//	}

	
	
}
