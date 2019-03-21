package com.nokona.resource;

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
import com.nokona.data.NokonaDatabaseOperation;
import com.nokona.db.NokonaDAOManagerX;
import com.nokona.db.NokonaDAOOperation;
import com.nokona.enums.DAOType;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.model.Operation;

@Path("/operations")
public class NokonaOperationResource {
//	@Inject
//	private NokonaDAOManager dbMgr;
//	@Inject @Named("x")
@Inject
	private NokonaDatabaseOperation db;
public NokonaOperationResource() throws DatabaseException  {
//	db =  (NokonaDatabaseOperation) dbMgr.getDAO(DAOType.OPERATION);
//	db = new NokonaDAOOperation();
	
}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{operation}")
	public Response getOperation(@PathParam("operation") String operation) {
		
		Operation op;
		
		try {
//			getDB();
			
				op = db.getOperation(operation);
	
		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + operation + " not found\"}").build();
		} catch (DatabaseException ex ) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		
		return Response.status(200).entity(op).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response getOperations() {
		try {
//			getDB();
			return Response.status(200).entity(db.getOperations()).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	// @Consumes(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response updateOperation(String opIn) {

		Gson gson;
		Operation op;
		try {
			gson = new Gson();
			op = gson.fromJson(opIn, Operation.class);
		} catch (JsonSyntaxException jse) {
			return Response.status(400).entity(jse.getMessage()).build();
		}
		try {
//			getDB();
			op = db.updateOperation(op);
		} catch (DuplicateDataException e) {
			return Response.status(400).entity(e.getMessage()).build();
		}catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

		return Response.status(200).entity(op).build();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	// @Consumes(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response addOperation(String opIn) {

		
		Gson gson;
		Operation op;
		try {
			gson = new Gson();
			op = gson.fromJson(opIn, Operation.class);
		} catch (JsonSyntaxException jse) {
			return Response.status(400).entity(jse.getMessage()).build();
		}
		try {
//			getDB();
			op = db.addOperation(op);
		} catch (DuplicateDataException e) {
			return Response.status(400).entity(e.getMessage()).build();
		}catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		return Response.status(200).entity(op).build();
	}
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{operation}")
	public Response deleteOperation(@PathParam("operation") String operation) {
		
		
		try {
//			getDB();
				db.deleteOperation(operation);
	
		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + operation + " not found\"}").build();
		} catch (DatabaseException ex ) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		
		return Response.status(200).entity("{\"Success\":\"200\"}").build();
	}
//	private void getDB() throws DatabaseException {
//
//		if (db == null) {
//			db = (NokonaDatabaseOperation) NokonaDAOManager.getDAO(DAOType.OPERATION);
//		}
//	}

}
