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
	private long key;
	private String opCode;
	private String operationDescription;
	private OperationStatus operationStatus;
	private int sequenceOriginal;
	private int sequenceUpdated;
	private Date statusDate;
	private int quantity;
	private double hourlyRateSAH;
	private int employeeBarCodeID;
		
}
