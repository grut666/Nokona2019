package com.nokona.enums;

public enum TicketStatus {
	PRINTED("P"), COMPLETE("C"), NEW(" "), P("P"), C("C"), N("N");
	private String ticketStatus;

	TicketStatus(String ticketStatus) {
		this.ticketStatus = ticketStatus;
	}

	public String getTicketStatus() {
		return ticketStatus;
	}
}
