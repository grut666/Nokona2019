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

import com.nokona.data.NokonaDatabaseLaborCode;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseConnectionException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;

import com.nokona.model.LaborCode;
import com.nokona.utilities.TransferToAccess;

@Path("/laborcodes")
@ApplicationScoped
public class NokonaLaborCodeResource {

	@Inject
	private NokonaDatabaseLaborCode db;

	public NokonaLaborCodeResource() {
		super();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{laborcode}")
	public Response getLaborCode(@PathParam("laborcode") int laborCodeIn) {

		LaborCode laborCode;

		try {
			// getDB();

			laborCode = db.getLaborCode(laborCodeIn);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + laborCodeIn + " not found\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

		return Response.status(200).entity(laborCode).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bykey/{key}")
	public Response getLaborCodeByKey(@PathParam("key") int key) {

		LaborCode laborCode;

		try {
			// getDB();

			laborCode = db.getLaborCodeByKey(key);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

		return Response.status(200).entity(laborCode).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response getLaborCodes() {
		try {
			// getDB();
			return Response.status(200).entity(db.getLaborCodes()).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	// @Consumes(MediaType.APPLICATION_JSON)
	@Path("/{laborCode}")
	public Response updateLaborCode(@PathParam("laborCode") int laborCode, LaborCode laborCodeIn) {

		if (laborCode != laborCodeIn.getLaborCode()) {
			return Response.status(400).entity("{\"error\":\" Mismatch between body and URL\"}").build();
		}
		try {
			LaborCode fetchedLaborCode = db.updateLaborCode(laborCodeIn);
			TransferToAccess.transfer("LABOR_U");
			return Response.status(200).entity(fetchedLaborCode).build();
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
	// @Consumes(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response addLaborCode(LaborCode laborCodeIn) {

		LaborCode laborCode;
		try {
			laborCode = db.addLaborCode(laborCodeIn);
			TransferToAccess.transfer("LABOR_C");
		} catch (DuplicateDataException e) {
			return Response.status(422).entity(e.getMessage()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		return Response.status(201).entity(laborCode).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{laborCode}")
	public Response deleteLaborCode(@PathParam("laborCode") int laborCode) {

		try {
			// getDB();
			db.deleteLaborCode(laborCode);
			TransferToAccess.transfer("LABOR_D");
		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + laborCode + " not found\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

		return Response.status(200).entity("{\"Success\":\"200\"}").build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bykey/{key}")
	public Response deleteLaborCodeByKey(@PathParam("key") int key) {

		try {
			// getDB();
			db.deleteLaborCodeByKey(key);
			TransferToAccess.transfer("LABOR_D");

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

		return Response.status(200).entity("{\"Success\":\"200\"}").build();
	}

}
