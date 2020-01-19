package com.nokona.formatter;

import com.nokona.model.Model;

public class ModelFormatter {

	public static Model format(Model model) {
		model.setModelId(formatModelId(model.getModelId()));
		model.setDescription(formatDescription(model.getDescription()));
		return model;
	}

	public static String formatModelId(String modelId) {
		return modelId.trim().toUpperCase();
	}

	public static String formatDescription(String description) {
		return description.trim().toUpperCase();
	}

}
