package com.nokona.enums;

public enum OperationStatus {
	COMPLETE("C"), INCOMPLETE(" "), C("C"), I(" ");
	private String OperationStatus;

	OperationStatus(String OperationStatus) {
		this.OperationStatus = OperationStatus;
	}

	public String getOperationStatus() {
		return OperationStatus;
	}
}
