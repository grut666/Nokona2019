package com.nokona.resource;

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

import com.nokona.data.NokonaDatabaseModel;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseConnectionException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.model.Employee;
import com.nokona.model.Model;

@Path("/models")
public class NokonaModelResource {

	@Inject
	private NokonaDatabaseModel db;

	public NokonaModelResource() throws DatabaseException {

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response getModels() {
		try {

			return Response.status(200).entity(db.getModels()).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{model}")
	public Response getModelsByModel(@PathParam("model") String model) {
		try {

			return Response.status(200).entity(db.getModel(model)).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bykey/{model}")
	public Response getModelsByKey(@PathParam("model") long key ) {
		try {

			return Response.status(200).entity(db.getModelByKey(key)).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{modelId}")
	public Response updateModel(@PathParam("modelId") String modelId, Model modelIn) {

		if (!modelId.equals(modelIn.getModelId())) {
			return Response.status(400).entity("{\"error\":\" Mismatch between body and URL\"}").build();
		}
		try {
			return Response.status(200).entity(db.updateModel(modelIn)).build();
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
	public Response addEmployee(Model modelIn) {
		Model model;
		try {
			model = db.addModel(modelIn);
		} catch (DuplicateDataException e) {
			return Response.status(422).entity(e.getMessage()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		return Response.status(201).entity(model).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{modelId}")
	public Response deleteModel(@PathParam("modelId") String modelId) {

		try {
			db.deleteModel(modelId);
			return Response.status(200).entity("{\"Success\":\"200\"}").build();
		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + modelId + " not found\"}").build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

	}
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bymodel/{key}")
	public Response deleteModelByKey(@PathParam("key") Long key) {

		try {
			db.deleteModelByKey(key);
			return Response.status(200).entity("{\"Success\":\"200\"}").build();
		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

	}

}
