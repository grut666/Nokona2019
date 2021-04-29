package com.nokona.model;

import java.util.Date;

import com.nokona.enums.TicketStatus;

public class TicketHeader {
	private long key;
	private String jobId;
	private String description;
	private Date dateCreated;
	private TicketStatus ticketStatus;
	private Date dateStatus;
	private int quantity;
	public TicketHeader() {
		this(-1, "", "", new Date(), TicketStatus.NEW, new Date(), 0);
	}

	public TicketHeader(TicketHeader ticketHeader) {
		this(-1, ticketHeader.getJobId(), ticketHeader.getDescription(), new Date(), TicketStatus.NEW, new Date(), ticketHeader.getQuantity());
	}
	public TicketHeader(long key, String jobId, String description, Date dateCreated, TicketStatus ticketStatus, Date dateStatus, int quantity) {
		this.setKey(key);
		this.setJobId(jobId);
		this.setDescription(description);
		this.setDateCreated(dateCreated);
		this.setTicketStatus(ticketStatus);
		this.setDateStatus(dateStatus);
		this.setQuantity(quantity);
	}
	public long getKey() {
		return key;
	}
	public void setKey(long key) {
		this.key = key;
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
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public TicketStatus getTicketStatus() {
		return ticketStatus;
	}
	public void setTicketStatus(TicketStatus ticketStatus) {
		this.ticketStatus = ticketStatus;
	}
	public Date getDateStatus() {
		return dateStatus;
	}
	public void setDateStatus(Date dateStatus) {
		this.dateStatus = dateStatus;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	@Override
	public String toString() {
		return "TicketHeader [key=" + key + ", jobId=" + jobId + ", dateCreated=" + dateCreated + ", ticketStatus="
				+ ticketStatus + ", dateStatus=" + dateStatus + ", quantity=" + quantity + "]";
	}
	
	
	
	
}
