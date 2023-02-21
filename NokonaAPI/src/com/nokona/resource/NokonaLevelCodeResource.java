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

import com.nokona.data.NokonaDatabaseLevelCode;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseConnectionException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;

import com.nokona.model.LevelCode;
import com.nokona.utilities.TransferToAccess;

@Path("/levelcodes")
@ApplicationScoped
public class NokonaLevelCodeResource {

	@Inject
	private NokonaDatabaseLevelCode db;

	public NokonaLevelCodeResource() {
		super();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{levelcode}")
	public Response getLevelCode(@PathParam("levelcode") int levelCodeIn) {

		LevelCode levelCode;

		try {
			// getDB();

			levelCode = db.getLevelCode(levelCodeIn);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + levelCodeIn + " not found\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

		return Response.status(200).entity(levelCode).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bykey/{key}")
	public Response getLevelCodeByKey(@PathParam("key") int key) {

		LevelCode levelCode;

		try {
			// getDB();

			levelCode = db.getLevelCodeByKey(key);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

		return Response.status(200).entity(levelCode).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response getLevelCodes() {
		try {
			// getDB();
			return Response.status(200).entity(db.getLevelCodes()).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	// @Consumes(MediaType.APPLICATION_JSON)
	@Path("/{levelCode}")
	public Response updateLevelCode(@PathParam("levelCode") int levelCode, LevelCode levelCodeIn) {

		if (levelCode != levelCodeIn.getLevelCode()) {
			return Response.status(400).entity("{\"error\":\" Mismatch between body and URL\"}").build();
		}
		try {
			LevelCode fetchedLevelCode = db.updateLevelCode(levelCodeIn);
			TransferToAccess.transfer("LABOR_U");
			return Response.status(200).entity(fetchedLevelCode).build();
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
	public Response addLevelCode(LevelCode levelCodeIn) {

		LevelCode levelCode;
		try {
			levelCode = db.addLevelCode(levelCodeIn);
			TransferToAccess.transfer("LABOR_C");
		} catch (DuplicateDataException e) {
			return Response.status(422).entity(e.getMessage()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		return Response.status(201).entity(levelCode).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{levelCode}")
	public Response deleteLevelCode(@PathParam("levelCode") int levelCode) {

		try {
			// getDB();
			db.deleteLevelCode(levelCode);
			TransferToAccess.transfer("LABOR_D");
		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + levelCode + " not found\"}").build();
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
	public Response deleteLevelCodeByKey(@PathParam("key") int key) {

		try {
			// getDB();
			db.deleteLevelCodeByKey(key);
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
