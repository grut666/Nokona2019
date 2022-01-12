package com.nokona.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class TicketDetailDtoInRecord {
	private long ticketNumber;
	private int quantity;
	private int barCodeID;
	private String operationCode;
	
}
