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
//	private Long ticketNo;
	private Long key;
	private String jobId;
	private String jobDescription;
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
	
	public TicketDetailDtoOut(TicketHeader ticketHeader, TicketDetail ticketDetail) {
		System.out.println("Ticket Header is " + ticketHeader);
		System.out.println("Ticket Detail is " + ticketDetail);
		key = ticketHeader.getKey(); // * 100 + ticketDetail.getSequenceOriginal();
		jobId = ticketHeader.getJobId();
		jobDescription = ticketHeader.getDescription();
		opCode = ticketDetail.getOpCode();
		operationDescription = ticketDetail.getOperationDescription();
		operationStatus = ticketDetail.getOperationStatus();
		sequenceOriginal = ticketDetail.getSequenceOriginal();
		sequenceUpdated = ticketDetail.getSequenceUpdated();
		statusDate = ticketDetail.getStatusDate();
		standardQuantity = ticketDetail.getStandardQuantity();
		hourlyRateSAH = ticketDetail.getHourlyRateSAH();
		levelCode = ticketDetail.getLevelCode();
		levelRate = ticketDetail.getLevelRate();
		laborCode = ticketDetail.getLaborCode();
		laborRate = ticketDetail.getLaborRate();
		employeeBarCodeID1 = ticketDetail.getEmployeeBarCodeID1();
		actualQuantity1 = ticketDetail.getActualQuantity1();
		employeeBarCodeID2 = ticketDetail.getEmployeeBarCodeID2();
		actualQuantity2 = ticketDetail.getActualQuantity2();
	}
}
