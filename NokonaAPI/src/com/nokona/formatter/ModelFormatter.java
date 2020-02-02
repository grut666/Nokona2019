package com.nokona.formatter;

import com.nokona.model.ModelHeader;

public class ModelFormatter {

	public static ModelHeader format(ModelHeader modelHeader) {
		modelHeader.setModelId(formatModelId(modelHeader.getModelId()));
		modelHeader.setDescription(formatDescription(modelHeader.getDescription()));
		return modelHeader;
	}

	public static String formatModelId(String modelId) {
		return modelId.trim().toUpperCase();
	}

	public static String formatDescription(String description) {
		return description.trim().toUpperCase();
	}

}
