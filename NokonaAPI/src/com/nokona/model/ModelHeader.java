package com.nokona.model;

import java.util.Date;

import com.nokona.enums.ModelType;

public class ModelHeader {
	private String modelId;
	private String description;
	private int standardQuantity;
	private ModelType modelType;
	private int key;

	public ModelHeader() {

	}

	public ModelHeader(String modelId, String description, int standardQuantity, ModelType modelType, int key) {
		this.setModelId(modelId);
		this.setDescription(description);
		this.setStandardQuantity(standardQuantity);
		this.setModelType(modelType);
		this.setKey(key);

	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStandardQuantity() {
		return standardQuantity;
	}

	public void setStandardQuantity(int standardQuantity) {
		this.standardQuantity = standardQuantity;
	}

	public ModelType getModelType() {
		return modelType;
	}

	public void setModelType(ModelType modelType) {
		this.modelType = modelType;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return "Model [modelId=" + modelId + ", description=" + description + ", standardQuantity=" + standardQuantity
				+ ", modelType=" + modelType + ", key=" + key + "]";
	}

}
