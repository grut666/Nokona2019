package com.nokona.resource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonSyntaxException;
import com.nokona.data.NokonaDatabaseTicket;
import com.nokona.dto.TicketDTOIn;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.model.Labels;
import com.nokona.model.Ticket;
import com.nokona.model.TicketHeader;
import com.nokona.utilities.BarCodeUtilities;

@Path("/tickets")

public class NokonaTicketResource {
	@ApplicationScoped

	@Inject
	private NokonaDatabaseTicket db;

	public NokonaTicketResource() throws DatabaseException {

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{key}")
	public Response getTicket(@PathParam("key") int key) {

		Ticket ticket;

		try {
			ticket = db.getTicketByKey(key);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

		return Response.status(200).entity(ticket).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response getTickets() {

		try {
			return Response.status(200).entity(db.getTickets()).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/ticketheaders")
	public Response getTicketHeaders() {

		try {
			return Response.status(200).entity(db.getTicketHeaders()).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/ticketheaders/{key}")
	public Response getTicketHeaderByKey(@PathParam("key") long key) {

		TicketHeader ticketHeader;

		try {
			ticketHeader = db.getTicketHeaderByKey(key);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}
		// String year = ticketHeader.getDateCreated().toString();
		return Response.status(200).entity(ticketHeader).build();
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{key}")
	public Response getTicketByKey(@PathParam("key") long key) {

		Ticket ticket;

		try {
			ticket = db.getTicketByKey(key);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}
		// String year = ticketHeader.getDateCreated().toString();
		return Response.status(200).entity(ticket).build();
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/labels/{ticket}")
	public Response getTicketLabels(@PathParam("ticket") Ticket ticketIn) {

		Labels labels;
		try {
			labels = new Labels();
			labels.setLabels(BarCodeUtilities.generateTicketLabels(ticketIn));

		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

		return Response.status(200).entity(labels).build();
	}

	// @PUT
	// @Produces(MediaType.APPLICATION_JSON)
	// @Path("/")
	// public Response updateTicket(String ticketIn) {
	//
	//
	// Gson gson;
	// Ticket ticket;
	// try {
	//
	// ticket = gson.fromJson(ticketIn, Ticket.class);
	// } catch (JsonSyntaxException jse) {
	// return Response.status(400).entity(jse.getMessage()).build();
	// }
	// try {
	// ticket = db.updateTicket(ticket);
	// } catch (DuplicateDataException e) {
	// return Response.status(400).entity(e.getMessage()).build();
	// }catch (DatabaseException ex) {
	// return Response.status(404).entity("{\"error\":\"" + ex.getMessage() +
	// "\"}").build();
	// }
	// catch (Exception ex) {
	// return Response.status(404).entity("{\"error\":\"" + ex.getMessage() +
	// "\"}").build();
	// }
	//
	// return Response.status(200).entity(ticket).build();
	// }
	// @POST
	// @Produces(MediaType.APPLICATION_JSON)
	//
	// @Path("/")
	// public Response addTicket(String ticketIn) {
	//
	// Gson gson;
	// Ticket ticket;
	// try {
	// gson = new Gson();
	// ticket = gson.fromJson(ticketIn, Ticket.class);
	// } catch (JsonSyntaxException jse) {
	// return Response.status(400).entity(jse.getMessage()).build();
	// }
	// try {
	// ticket = db.addTicket(ticket);
	// } catch (DuplicateDataException e) {
	// return Response.status(400).entity(e.getMessage()).build();
	// }catch (DatabaseException ex) {
	// return Response.status(404).entity("{\"error\":\"" + ex.getMessage() +
	// "\"}").build();
	// }
	// catch (Exception ex) {
	// return Response.status(404).entity("{\"error\":\"" + ex.getMessage() +
	// "\"}").build();
	// }
	// return Response.status(200).entity(ticket).build();
	// }
	// @DELETE
	// @Produces(MediaType.APPLICATION_JSON)
	// @Path("/{ticket}")
	// public Response deleteTicket(@PathParam("ticket") String ticket) {
	//
	// try {
	// db.deleteTicket(ticket);
	// } catch (DataNotFoundException ex) {
	// return Response.status(404).entity("{\"error\":\"" + ticket + " not
	// found\"}").build();
	// } catch (DatabaseException ex ) {
	// return Response.status(404).entity("{\"error\":\"" + ex.getMessage() +
	// "\"}").build();
	// }
	// catch (Exception ex) {
	// return Response.status(404).entity("{\"error\":\"" + ex.getMessage() +
	// "\"}").build();
	// }
	//
	// return Response.status(200).entity("{\"Success\":\"200\"}").build();
	// }

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response generateTicket(TicketDTOIn ticketDTO) {

		Ticket ticket;
		try {
			ticket = db.addTicket(ticketDTO);
		} catch (JsonSyntaxException jse) {
			return Response.status(400).entity(jse.getMessage()).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

		return Response.status(200).entity(ticket).build();
	}
}
