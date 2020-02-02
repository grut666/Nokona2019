package com.nokona.model;

public class ModelDetail {
	private String modelId;
	private String opCode;
	private int sequence;
	public ModelDetail() {
		super();
	}
	public ModelDetail(String modelId, String opCode, int sequence) {
		super();
		this.setModelId(modelId);
		this.setOpCode(opCode);
		this.setSequence(sequence);
	}

	public String getModelId() {
		return modelId;
	}
	public void setModelId(String modelId) {
		this.modelId = modelId;
	}
	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}
	public String getOpCode() {
		return opCode;
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
