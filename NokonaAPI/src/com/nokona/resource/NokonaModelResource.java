package com.nokona.resource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nokona.data.NokonaDatabaseModel;
import com.nokona.exceptions.DatabaseException;

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

}
