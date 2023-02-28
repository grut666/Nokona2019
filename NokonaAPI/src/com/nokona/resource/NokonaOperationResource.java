package com.nokona.resource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nokona.data.NokonaDatabaseOperation;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseConnectionException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.model.Operation;
import com.nokona.utilities.TransferToAccess;

@Path("/operations")
@ApplicationScoped
public class NokonaOperationResource {

	@Inject
	private NokonaDatabaseOperation db;

	public NokonaOperationResource() {
		super();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{operation}")
	public Response getOperation(@PathParam("operation") String operation) {
		Operation op;
		try {

			op = db.getOperation(operation);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + operation + " not found\"}").build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

		return Response.status(200).entity(op).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bykey/{operation}")
	public Response getOperationByKey(@PathParam("operation") long key) {

		Operation op;

		try {

			op = db.getOperationByKey(key);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}
		return Response.status(200).entity(op).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOperations() {
		try {
			return Response.status(200).entity(db.getOperations()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/byJobId/{jobId}")
	public Response getOperationsByJobId(@PathParam("jobId") String jobId) {
		try {

			return Response.status(200).entity(db.getOperationsByJobId(jobId)).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{opCode}")
	public Response updateOperation(@PathParam("opCode") String opCode, Operation opIn) {

		if (!opCode.equals(opIn.getOpCode())) {
			return Response.status(400).entity("{\"error\":\" Mismatch between body and URL\"}").build();
		}
		try {
			Operation operation = db.updateOperation(opIn);
			return Response.status(200).entity(operation).build();
		} catch (DuplicateDataException e) {
			return Response.status(422).entity(e.getMessage()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response addOperation(Operation opIn) {
		Operation op;
		try {
			op = db.addOperation(opIn);
		} catch (DuplicateDataException e) {
			return Response.status(422).entity(e.getMessage()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		return Response.status(201).entity(op).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{operation}")
	public Response deleteOperation(@PathParam("operation") String operation) {
		try {
			db.deleteOperation(operation);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + operation + " not found\"}").build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

		return Response.status(200).entity("{\"Success\":\"200\"}").build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bykey/{key}")
	public Response deleteOperationByKey(@PathParam("key") long key) {

		try {

			db.deleteOperationByKey(key);
		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

		return Response.status(200).entity("{\"Success\":\"200\"}").build();
	}

}
