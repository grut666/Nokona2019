package com.nokona.enums;

public enum ModelType {
	BASEBALL("B"), SOFTBALL("S"), OTHER("O"), UNKNOWN(" "), B("B"), S("S"), O("O");
	private String modelType;

	ModelType(String modelType) {
		this.modelType = modelType;
	}

	public String getModelType() {
		return modelType;
	}
}
