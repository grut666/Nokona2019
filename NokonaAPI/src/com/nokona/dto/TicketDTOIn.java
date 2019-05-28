package com.nokona.dto;

public class TicketDTOIn {
	private String modelId;
	private int quantity;
	public TicketDTOIn() {
		
	}
	public TicketDTOIn(String modelId, int quantity) {
		this.setModelId(modelId);
		this.setQuantity(quantity);
	}
	public String getModelId() {
		return modelId;
	}
	public void setModelId(String modelId) {
		this.modelId = modelId;
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
