package com.nokona.dto;

public class TicketDTOIn {
	private String jobId;
	private String description;
	private int quantity;

	public TicketDTOIn() {

	}

	public TicketDTOIn(String jobId, String description, int quantity) {
		this.setJobId(jobId);
		this.setDescription(description);
		this.setQuantity(quantity);
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
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
		return "TicketDTOIn [jobId=" + jobId + ", quantity=" + quantity + "]";
	}
}
