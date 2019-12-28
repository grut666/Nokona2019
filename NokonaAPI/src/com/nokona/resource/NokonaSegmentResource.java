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
import com.nokona.data.NokonaDatabaseSegment;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.model.Segment;
import com.nokona.model.SegmentHeader;

@Path("/segments")

public class NokonaSegmentResource {
	@ApplicationScoped

	@Inject
	private NokonaDatabaseSegment db;

	public NokonaSegmentResource() throws DatabaseException {

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{segmentName}")
	public Response getSegmentByKey(@PathParam("segmentName") String segmentName) {

		Segment segment;

		try {
			segment = db.getSegmentByName(segmentName);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + segmentName + " not found\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

		return Response.status(200).entity(segment).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{name}")
	public Response getSegmentByName(@PathParam("name") String name) {

		Segment segment;
		try {
			segment = db.getSegmentByName(name);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + name + " not found\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

		return Response.status(200).entity(segment).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response getTickets() {

		try {
			return Response.status(200).entity(db.getSegments()).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/segmentheaders")
	public Response getTicketHeaders() {

		try {
			return Response.status(200).entity(db.getSegmentHeaders()).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/segmentheaders/{segmentName}")
	public Response getSegmentHeaderByName(@PathParam("segmentName") String segmentName) {

		SegmentHeader segmentHeader;

		try {
			segmentHeader = db.getSegmentHeaderByName(segmentName);
		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + segmentName + " not found\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

		return Response.status(200).entity(segmentHeader).build();
	}

//	@POST
//	@Produces(MediaType.APPLICATION_JSON)
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Path("/")
//	public Response generateTicket(Segment segment) {
//
//		try {
//			segment = db.addSegment(segment);
//		} catch (JsonSyntaxException jse) {
//			return Response.status(400).entity(jse.getMessage()).build();
//		} catch (DatabaseException ex) {
//			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		}
//
//		return Response.status(200).entity(segment).build();
//	}
}
