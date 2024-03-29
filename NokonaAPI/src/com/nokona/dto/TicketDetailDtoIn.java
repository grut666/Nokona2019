package com.nokona.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@AllArgsConstructor
@NoArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class TicketDetailDtoIn {
	private String dateOfTicket;
	List<TicketDetailDtoInRecord> detailRecords;


}
