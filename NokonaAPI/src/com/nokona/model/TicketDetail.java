package com.nokona.model;

import java.util.Date;

import com.nokona.enums.OperationStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
@AllArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class TicketDetail {
	private long key;
	private String opCode;
	private String operationDescription;
	private OperationStatus operationStatus;
	private int sequenceOriginal;
	private int sequenceUpdated;
	private Date statusDate;
	private int quantity;
	private double hourlyRateSAH;
	private int barCodeID;
	
//	public TicketDetail() {
//		super();
//	}
//	public TicketDetail(long key, String operation, String operationDescription, OperationStatus operationStatus, int sequenceOriginal, int sequenceUpdated, Date statusDate,
//			int quantity, double hourlyRateSAH, int barCodeID) {
//		super();
//		this.setKey(key);
//		this.setOperation(operation);
//		this.setSequenceOriginal(sequenceOriginal);
//		this.setOperationDescription(operationDescription);
//		this.setSequenceUpdated(sequenceUpdated);
//		this.setStatusDate(statusDate);
//		this.setOperationStatus(operationStatus);
//		this.setQuantity(quantity);
//		this.setHourlyRateSAH(hourlyRateSAH);
//		this.setBarCodeID(barCodeID);		
//	}
//	public long getKey() {
//		return key;
//	}
//	public void setKey(long key) {
//		this.key = key;
//	}
//	public String getOperation() {
//		return operation;
//	}
//	public void setOperation(String operation) {
//		this.operation = operation;
//	}
//	
//	public String getOperationDescription() {
//		return operationDescription;
//	}
//	public void setOperationDescription(String operationDescription) {
//		this.operationDescription = operationDescription;
//	}
//	public double getHourlyRateSAH() {
//		return hourlyRateSAH;
//	}
//	public void setHourlyRateSAH(double hourlyRateSAH) {
//		this.hourlyRateSAH = hourlyRateSAH;
//	}
//	public int getSequenceOriginal() {
//		return sequenceOriginal;
//	}
//	public void setSequenceOriginal(int sequenceOriginal) {
//		this.sequenceOriginal = sequenceOriginal;
//	}
//	public int getSequenceUpdated() {
//		return sequenceUpdated;
//	}
//	public void setSequenceUpdated(int sequenceUpdated) {
//		this.sequenceUpdated = sequenceUpdated;
//	}
//	public Date getStatusDate() {
//		return statusDate;
//	}
//	public void setStatusDate(Date statusDate) {
//		this.statusDate = statusDate;
//	}
//	public OperationStatus getOperationStatus() {
//		return operationStatus;
//	}
//	public void setOperationStatus(OperationStatus operationStatus) {
//		this.operationStatus = operationStatus;
//	}
//	public int getQuantity() {
//		return quantity;
//	}
//	public void setQuantity(int quantity) {
//		this.quantity = quantity;
//	}
//	
//	public int getBarCodeID() {
//		return barCodeID;
//	}
//	public void setBarCodeID(int barCodeID) {
//		this.barCodeID = barCodeID;
//	}
//	@Override
//	public String toString() {
//		return "TicketDetail [key=" + key + ", operation=" + operation + ", operationDescription="
//				+ operationDescription + ", sequenceOriginal=" + sequenceOriginal + ", sequenceUpdated="
//				+ sequenceUpdated + ", statusDate=" + statusDate + ", operationStatus=" + operationStatus
//				+ ", quantity=" + quantity + ", hourlyRateSAH=" + hourlyRateSAH + ", barCodeID=" + barCodeID + "]";
//	}
//	


	
}
