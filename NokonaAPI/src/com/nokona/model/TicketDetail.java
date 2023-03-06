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
	private double hourlyRateSAH;
	private int levelCode;
	private double levelRate;
	private int laborCode;
	private double laborRate;
	private int employeeBarCodeID1;
	private int actualQuantity1;
	private int employeeBarCodeID2;
	private int actualQuantity2;
	
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