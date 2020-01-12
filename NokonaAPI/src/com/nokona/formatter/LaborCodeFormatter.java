package com.nokona.formatter;

import com.nokona.model.LaborCode;

public class LaborCodeFormatter {
	// So far, this class does nothing.  It just keeps compatibility with other classes
	// where it does do some formatting.
	// May be used for formatting in the future
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
