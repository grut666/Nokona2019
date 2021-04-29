package com.nokona.formatter;

import com.nokona.model.Operation;

public class OperationFormatter {

	public static Operation format(Operation operation) {
		operation.setOpCode(formatOpCode(operation.getOpCode()));
		operation.setDescription(formatDescription(operation.getDescription()));
		return operation;
	}

	public static String formatOpCode(String opCode) {
		return opCode.trim().toUpperCase();
	}
	public static String formatDescription(String description) {
		return description.trim().toUpperCase();
	}



}
