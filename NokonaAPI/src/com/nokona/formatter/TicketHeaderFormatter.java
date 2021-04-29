package com.nokona.formatter;

import com.nokona.model.TicketHeader;

public class TicketHeaderFormatter {
	
	public static TicketHeader format(TicketHeader ticketHeader) {
		ticketHeader.setJobId(formatTicketJob(ticketHeader.getJobId()));
		return ticketHeader;
	}

	public static String formatTicketJob(String ticketJob) {
		return ticketJob.trim().toUpperCase();
	}
	

}
