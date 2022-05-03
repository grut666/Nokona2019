package com.nokona.model;

import java.util.Date;

import com.nokona.enums.OperationStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@AllArgsConstructor
@NoArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class TicketDetail {
	private long key;  // this matches TicketHeader key
	private String opCode;
	private String operationDescription;
	private OperationStatus operationStatus;
	private int sequenceOriginal;
	private int sequenceUpdated;
	private Date statusDate;
	private int standardQuantity;
	private int actualQuantity;
	private double hourlyRateSAH;
	private int laborCode;
	private String laborDescription;
	private double laborRate;
	private int employeeBarCodeID;
	public TicketDetail(long key) {
		// This is for the final ticket in the sequence - Job Complete
		this.key = key;
		this.opCode = "ZZZ";
		this.operationDescription = "Job Complete";
		this.operationStatus = OperationStatus.COMPLETE;
		this.sequenceOriginal = 99;
		this.sequenceUpdated = 99;
		this.statusDate = new Date();
	}
		
	}