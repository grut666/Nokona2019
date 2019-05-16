package com.nokona.enums;

public enum TicketStatus {
	PRODUCTION("P"), COMPLETE("C"), UNKNOWN(" "), P("P"), C("C"), U(""),;
	private String ticketStatus;

	TicketStatus(String ticketStatus) {
		this.ticketStatus = ticketStatus;
	}

	public String getTicketStatus() {
		return ticketStatus;
	}
}
