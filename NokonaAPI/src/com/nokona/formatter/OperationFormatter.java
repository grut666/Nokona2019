package com.nokona.formatter;

import com.nokona.model.Operation;

public class OperationFormatter {

	public static Operation format(Operation operation) {
		operation.setOpCode(formatOpCode(operation.getOpCode()));
		return operation;
	}

	public static String formatOpCode(String opCode) {
		return opCode.trim().toUpperCase();
	}

}
