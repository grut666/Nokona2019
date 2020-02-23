package com.nokona.formatter;

import com.nokona.model.LaborCode;

public class LaborCodeFormatter {

	public static LaborCode format(LaborCode laborCode) {
		laborCode.setLaborCode(formatLaborCode(laborCode.getLaborCode()));
		laborCode.setDescription(formatDescription(laborCode.getDescription()));
		return laborCode;
	}

	public static int formatLaborCode(int laborCode) {
		return laborCode;
	}
	public static String formatDescription(String description) {
		return description.trim().toUpperCase();
	}

}
