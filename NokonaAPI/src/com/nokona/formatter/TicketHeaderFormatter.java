package com.nokona.formatter;

import com.nokona.model.TicketHeader;

public class TicketHeaderFormatter {
	
	public static TicketHeader format(TicketHeader ticketHeader) {
		ticketHeader.setModel(formatTicketModel(ticketHeader.getModel()));
		return ticketHeader;
	}

	public static String formatTicketModel(String ticketModel) {
		return ticketModel.trim().toUpperCase();
	}
	

}
