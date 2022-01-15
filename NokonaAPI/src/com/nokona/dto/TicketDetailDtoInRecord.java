package com.nokona.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class TicketDetailDtoInRecord {
	private long ticketNumber;
	private int quantity;
	private int barCodeID;
	
}
