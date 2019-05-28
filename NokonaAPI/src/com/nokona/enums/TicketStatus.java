package com.nokona.enums;

public enum TicketStatus {
	PRODUCTION("P"), COMPLETE("C"), NEW(" "), P("P"), C("C");
	private String ticketStatus;

	TicketStatus(String ticketStatus) {
		this.ticketStatus = ticketStatus;
	}

	public String getTicketStatus() {
		return ticketStatus;
	}
}
