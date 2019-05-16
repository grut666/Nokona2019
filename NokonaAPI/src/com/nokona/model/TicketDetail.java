package com.nokona.model;

import java.util.Date;

import com.nokona.enums.TicketStatus;

public class TicketDetail {
	private long key;
	private String operation;
	private int sequenceOriginal;
	private int sequenceUpdated;
	private Date statusDate;
	private TicketStatus operationStatus;
	private int quantity;
	private double rate;
	private int barCodeID;
	private double laborRate;
	
	public TicketDetail() {
		super();
	}
	public TicketDetail(long key, String operation, int sequenceOriginal, int sequenceUpdated, Date statusDate,
			TicketStatus operationStatus, int quantity, double rate, int barCodeID, double laborRate) {
		super();
		this.setKey(key);
		this.setOperation(operation);
		this.setSequenceOriginal(sequenceOriginal);
		this.setSequenceOriginal(sequenceOriginal);
		this.setStatusDate(statusDate);
		this.setOperationStatus(operationStatus);
		this.setQuantity(quantity);
		this.setRate(laborRate);
		this.setBarCodeID(barCodeID);
		this.setLaborRate(laborRate);		
	}
	public long getKey() {
		return key;
	}
	public void setKey(long key) {
		this.key = key;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public int getSequenceOriginal() {
		return sequenceOriginal;
	}
	public void setSequenceOriginal(int sequenceOriginal) {
		this.sequenceOriginal = sequenceOriginal;
	}
	public int getSequenceUpdated() {
		return sequenceUpdated;
	}
	public void setSequenceUpdated(int sequenceUpdated) {
		this.sequenceUpdated = sequenceUpdated;
	}
	public Date getStatusDate() {
		return statusDate;
	}
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}
	public TicketStatus getOperationStatus() {
		return operationStatus;
	}
	public void setOperationStatus(TicketStatus operationStatus) {
		this.operationStatus = operationStatus;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	public int getBarCodeID() {
		return barCodeID;
	}
	public void setBarCodeID(int barCodeID) {
		this.barCodeID = barCodeID;
	}
	public double getLaborRate() {
		return laborRate;
	}
	public void setLaborRate(double laborRate) {
		this.laborRate = laborRate;
	}
	@Override
	public String toString() {
		return "TicketDetail [key=" + key + ", operation=" + operation + ", sequenceOriginal=" + sequenceOriginal
				+ ", sequenceUpdated=" + sequenceUpdated + ", statusDate=" + statusDate + ", operationStatus="
				+ operationStatus + ", quantity=" + quantity + ", rate=" + rate + ", barCodeID=" + barCodeID
				+ ", laborRate=" + laborRate + "]";
	}
	
}
