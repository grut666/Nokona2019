package com.nokona.model;

public class ModelDetail {
	private String opCode;
	private int sequence;
	public ModelDetail() {
		super();
	}
	public ModelDetail(String opCode, int sequence) {
		super();

		this.setOperation(opCode);
		this.setSequence(sequence);
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
		return "ModelDetail [opCode=" + opCode + ", sequence=" + sequence
				+ "]";
	}

	
	
}
