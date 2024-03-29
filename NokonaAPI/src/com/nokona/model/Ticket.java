package com.nokona.model;

import java.util.ArrayList;
import java.util.List;

public class Ticket {
	private TicketHeader ticketHeader;
	private List<TicketDetail> ticketDetails;

	public Ticket() {
		this(null, null);
	}

	public Ticket(TicketHeader ticketHeader, List<TicketDetail> ticketDetails) {
		super();
		if (ticketHeader == null) {
			this.setTicketHeader(new TicketHeader());
		} else {
			this.setTicketHeader(ticketHeader);
		}
		if (ticketDetails == null) {
			this.setTicketDetails(new ArrayList<TicketDetail>());
		} else {
			this.setTicketDetails(ticketDetails);
		}
	}

	public TicketHeader getTicketHeader() {
		return ticketHeader;
	}

	public void setTicketHeader(TicketHeader ticketHeader) {
		this.ticketHeader = ticketHeader;
	}

	public List<TicketDetail> getTicketDetails() {
		return ticketDetails;
	}

	public void setTicketDetails(List<TicketDetail> ticketDetails) {
		this.ticketDetails = ticketDetails;
	}

	@Override
	public String toString() {
		return "Ticket [ticketHeader=" + ticketHeader + ", ticketDetails=" + ticketDetails + "]";
	}

}
