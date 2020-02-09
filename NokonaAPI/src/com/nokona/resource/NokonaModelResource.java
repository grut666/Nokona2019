package com.nokona.resource;

import java.util.List;

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
import com.nokona.model.ModelDetail;
import com.nokona.model.ModelHeader;

@Path("/models")
public class NokonaModelResource {

	@Inject
	private NokonaDatabaseModel db;

	public NokonaModelResource() throws DatabaseException {

	}
	
//  HEADERS
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/modelheaders")
	public Response getModelHeaders() {
		try {
			return Response.status(200).entity(db.getModelHeaders()).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/modelheaders/{modelId}")
	public Response getModelHeadersByModel(@PathParam("modelId") String model) {
		try {

			return Response.status(200).entity(db.getModelHeader(model)).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/modelheaders/bykey/{key}")
	public Response getModelHeadersByKey(@PathParam("key") long key ) {
		try {

			return Response.status(200).entity(db.getModelHeaderByKey(key)).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/modelheaders/{modelId}")
	public Response updateModelHeader(@PathParam("modelId") String modelId, ModelHeader modelHeaderIn) {

		if (!modelId.equals(modelHeaderIn.getModelId())) {
			return Response.status(400).entity("{\"error\":\" Mismatch between body and URL\"}").build();
		}
		try {
			return Response.status(200).entity(db.updateModelHeader(modelHeaderIn)).build();
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

	@Path("/modelheaders")
	public Response addModelHeader(ModelHeader modelHeaderIn) {
		ModelHeader modelHeader;
		try {
			modelHeader = db.addModelHeader(modelHeaderIn);
		} catch (DuplicateDataException e) {
			return Response.status(422).entity(e.getMessage()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		return Response.status(201).entity(modelHeader).build();
	}

// DETAILS	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/modeldetails/{model}")
	public Response getModelDetailsByModel(@PathParam("model") String model) {
		try {

			return Response.status(200).entity(db.getModelDetails(model)).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}
	
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/modeldetails/{modelId}")

	public Response updateModelDetail(@PathParam("modelId") String modelId, ModelDetail modelDetailIn) {

		if (!modelId.equals(modelDetailIn.getModelId())) {
			return Response.status(400).entity("{\"error\":\" Mismatch between body and URL\"}").build();
		}
		try {
			return Response.status(200).entity(db.updateModelDetails(modelDetailIn)).build();
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

	@Path("/modeldetails")
	public Response addModelDetail(ModelDetail modelDetailIn) {
		List<ModelDetail> modelDetails;
		try {
			modelDetails = db.addModelDetails(modelDetailIn);
		} catch (DuplicateDataException e) {
			return Response.status(422).entity(e.getMessage()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		return Response.status(201).entity(modelDetails).build();
	}
	

// Model in its entirety
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{model}")
	public Response getModelByModel(@PathParam("model") String model) {
		try {

			return Response.status(200).entity(db.getModel(model)).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
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
