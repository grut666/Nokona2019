package com.nokona.data;

import java.util.List;

import com.nokona.exceptions.DatabaseException;
import com.nokona.model.Ticket;
import com.nokona.model.TicketDetail;
import com.nokona.model.TicketHeader;

public interface NokonaDatabaseTicket extends NokonaDatabase {
	
	List<Ticket> getTickets(int offset) throws DatabaseException;
	Ticket getTicketByKey(long key) throws DatabaseException;
	List<Ticket> getTicketsByJob(String jobId, String status) throws DatabaseException;
	List<Ticket> getTicketsByStatus(String status, int offset) throws DatabaseException;
	Ticket addTicket(TicketHeader ticketHeader)  throws DatabaseException;
	Ticket updateTicket (Ticket ticket) throws DatabaseException;
	TicketHeader updateTicketHeader (TicketHeader ticketHeader) throws DatabaseException;
	TicketDetail updateTicketDetail (TicketDetail ticketDetail) throws DatabaseException;
	void deleteTicketByKey (long key) throws DatabaseException;
	
	List<TicketHeader> getTicketHeaders(int offset) throws DatabaseException;
	List<TicketHeader> getTicketHeadersByStatus(String status, int offset) throws DatabaseException;
	TicketHeader getTicketHeaderByKey(long headerKey) throws DatabaseException;
	List<TicketDetail> getTicketDetailsByHeaderKey(long headerKey) throws DatabaseException;
	TicketDetail getTicketDetailByDetailKey(long detailKey) throws DatabaseException;

}
