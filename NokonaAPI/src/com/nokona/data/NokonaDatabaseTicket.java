package com.nokona.data;

import java.util.List;

import com.nokona.dto.TicketDTOIn;
import com.nokona.exceptions.DatabaseException;
import com.nokona.model.Ticket;
import com.nokona.model.TicketDetail;
import com.nokona.model.TicketHeader;

public interface NokonaDatabaseTicket  {
	
	List<Ticket> getTickets() throws DatabaseException;
	Ticket getTicketByKey(long headerKey) throws DatabaseException;
	List<Ticket> getTicketsByModel(String model) throws DatabaseException;
	Ticket addTicket(TicketDTOIn dtoIn)  throws DatabaseException;
	Ticket updateTicket (Ticket ticket) throws DatabaseException;

	Ticket deleteTicketByKey (Ticket ticket) throws DatabaseException;
	
	List<TicketHeader> getTicketHeaders() throws DatabaseException;
	TicketHeader getTicketHeaderByKey(long headerKey) throws DatabaseException;
	
	List<TicketDetail> getTicketDetailsByKey(long headerKey) throws DatabaseException;

}
