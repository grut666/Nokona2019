package com.nokona.dto;

public class TicketDTOIn {
	private String modelId;
	private String description;
	private int quantity;
	public TicketDTOIn() {
		
	}
	public TicketDTOIn(String modelId, String description, int quantity) {
		this.setModelId(modelId);
		this.setDescription(description);
		this.setQuantity(quantity);
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
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	@Override
	public String toString() {
		return "ModelDTOIn [modelId=" + modelId + ", quantity=" + quantity + "]";
	}
}
