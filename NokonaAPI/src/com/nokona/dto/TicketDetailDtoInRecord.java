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
	private int quantity1;
	private int barCodeID1;
	private int quantity2;
	private int barCodeID2;
	
}
