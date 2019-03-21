package com.nokona.resource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nokona.data.NokonaDatabaseEmp;
import com.nokona.data.NokonaDatabaseTicket;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.model.Employee;
import com.nokona.model.Ticket;

@Path("/tickets")

public class NokonaTicketResource {
@ApplicationScoped


@Inject	
private NokonaDatabaseTicket db;

	public NokonaTicketResource() throws DatabaseException  {

		
	}

//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/{ticket}")
//	public Response getTicket(@PathParam("ticket") String ticketIn) {
//		
//		Ticket ticket;
//		
//		try {
//				ticket = db.getTicket(ticketIn);
//	
//		} catch (DataNotFoundException ex) {
//			return Response.status(404).entity("{\"error\":\"" + ticketIn + " not found\"}").build();
//		} catch (DatabaseException ex ) {
//			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		}
//		catch (Exception ex) {
//			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
//		}
//		
//		return Response.status(200).entity(ticket).build();
//	}

//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/")
//	public Response getTickets() {
//
//		try {
//			return Response.status(200).entity(db.getTickets()).build();
//		} catch (DatabaseException ex) {
//			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		}
//		catch (Exception ex) {
//			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() +  db +"\"}").build();
//		}
//		
//		
//	}

//	@PUT
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/")
//	public Response updateTicket(String ticketIn) {
//
//		
//		Gson gson;
//		Ticket ticket;
//		try {
//
//			ticket = gson.fromJson(ticketIn, Ticket.class);
//		} catch (JsonSyntaxException jse) {
//			return Response.status(400).entity(jse.getMessage()).build();
//		}
//		try {
//			ticket = db.updateTicket(ticket);
//		} catch (DuplicateDataException e) {
//			return Response.status(400).entity(e.getMessage()).build();
//		}catch (DatabaseException ex) {
//			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		}
//		catch (Exception ex) {
//			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		}
//
//		return Response.status(200).entity(ticket).build();
//	}
//	@POST
//	@Produces(MediaType.APPLICATION_JSON)
//
//	@Path("/")
//	public Response addTicket(String ticketIn) {
//
//		Gson gson;
//		Ticket ticket;
//		try {
//			gson = new Gson();
//			ticket = gson.fromJson(ticketIn, Ticket.class);
//		} catch (JsonSyntaxException jse) {
//			return Response.status(400).entity(jse.getMessage()).build();
//		}
//		try {
//			ticket = db.addTicket(ticket);
//		} catch (DuplicateDataException e) {
//			return Response.status(400).entity(e.getMessage()).build();
//		}catch (DatabaseException ex) {
//			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		}
//		catch (Exception ex) {
//			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		}
//		return Response.status(200).entity(ticket).build();
//	}
//	@DELETE
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/{ticket}")
//	public Response deleteTicket(@PathParam("ticket") String ticket) {
//		
//		try {			
//				db.deleteTicket(ticket);
//		} catch (DataNotFoundException ex) {
//			return Response.status(404).entity("{\"error\":\"" + ticket + " not found\"}").build();
//		} catch (DatabaseException ex ) {
//			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		}
//		catch (Exception ex) {
//			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		}
//		
//		return Response.status(200).entity("{\"Success\":\"200\"}").build();
//	}


}
