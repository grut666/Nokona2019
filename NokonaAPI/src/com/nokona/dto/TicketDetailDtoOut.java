package com.nokona.dto;

import java.util.Date;

import com.nokona.enums.OperationStatus;
import com.nokona.model.TicketDetail;
import com.nokona.model.TicketHeader;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class TicketDetailDtoOut {
	private String jobId;
	private String jobDescription;
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
	public TicketDetailDtoOut(TicketHeader ticketHeader, TicketDetail ticketDetail) {
		jobId = ticketHeader.getJobId();
		jobDescription = ticketHeader.getDescription();
		opCode = ticketDetail.getOpCode();
		operationDescription = ticketDetail.getOperationDescription();
		operationStatus = ticketDetail.getOperationStatus();
		sequenceOriginal = ticketDetail.getSequenceOriginal();
		sequenceUpdated = ticketDetail.getSequenceUpdated();
		statusDate = ticketDetail.getStatusDate();
		standardQuantity = ticketDetail.getStandardQuantity();
		actualQuantity = ticketDetail.getActualQuantity();
		hourlyRateSAH = ticketDetail.getHourlyRateSAH();
		laborCode = ticketDetail.getLaborCode();
		laborDescription = ticketDetail.getLaborDescription();
		laborRate = ticketDetail.getLaborRate();
		employeeBarCodeID = ticketDetail.getEmployeeBarCodeID();		
	}
}
