package com.nokona.model;

import java.util.Date;

import com.nokona.dto.TicketDTOIn;
import com.nokona.enums.TicketStatus;

public class TicketHeader {
	private long key;
	private String model;
	private String description;
	private Date dateCreated;
	private TicketStatus ticketStatus;
	private Date dateStatus;
	private int quantity;
	public TicketHeader() {
		this(-1, "", "", new Date(), TicketStatus.NEW, new Date(), 0);
	}

	public TicketHeader(TicketDTOIn ticketDTOIn) {
		this(-1, ticketDTOIn.getModelId(), ticketDTOIn.getDescription(), new Date(), TicketStatus.NEW, new Date(), ticketDTOIn.getQuantity());
	}
	public TicketHeader(long key, String model, String description, Date dateCreated, TicketStatus ticketStatus, Date dateStatus, int quantity) {
		this.setKey(key);
		this.setModel(model);
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
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
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
		return "TicketHeader [key=" + key + ", model=" + model + ", dateCreated=" + dateCreated + ", ticketStatus="
				+ ticketStatus + ", dateStatus=" + dateStatus + ", quantity=" + quantity + "]";
	}
	
	
	
	
}
