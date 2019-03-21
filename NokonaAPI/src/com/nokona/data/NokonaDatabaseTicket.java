package com.nokona.data;

import java.util.List;

import com.nokona.exceptions.DatabaseException;
import com.nokona.model.Employee;
import com.nokona.model.Segment;
import com.nokona.model.Ticket;

public interface NokonaDatabaseTicket  {
	List<Segment> getTicketSegment() throws DatabaseException;
	Segment getTicketSegment(long key) throws DatabaseException;
	Segment getTicketSegment(String key) throws DatabaseException;
	Employee getEmployee(String empID) throws DatabaseException;
	Employee updateEmployee(Employee employee) throws DatabaseException;
	Employee addEmployee(Employee employee) throws DatabaseException;
	void deleteEmployee(long key) throws DatabaseException;
	void deleteEmployee(String empID) throws DatabaseException;
	Ticket addTicket(Ticket ticket);
	Ticket getTicket(String ticketIn);	
}
