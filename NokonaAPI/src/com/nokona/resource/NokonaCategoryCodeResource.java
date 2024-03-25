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

import com.nokona.data.NokonaDatabaseCategoryCode;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseConnectionException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;

import com.nokona.model.CategoryCode;

@Path("/categorycodes")
@ApplicationScoped
public class NokonaCategoryCodeResource {

	@Inject
	private NokonaDatabaseCategoryCode db;

	public NokonaCategoryCodeResource() {
		super();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{categorycode}")
	public Response getCategoryCode(@PathParam("categorycode") String categoryCodeIn) {

		CategoryCode categoryCode;

		try {
			// getDB();

			categoryCode = db.getCategoryCode(categoryCodeIn);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + categoryCodeIn + " not found\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

		return Response.status(200).entity(categoryCode).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bykey/{key}")
	public Response getLevelCodeByKey(@PathParam("key") int key) {

		CategoryCode categoryCode;

		try {
			// getDB();

			categoryCode = db.getCategoryCodeByKey(key);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

		return Response.status(200).entity(categoryCode).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response Category() {
		try {
			System.out.println("Getting category codes");
			return Response.status(200).entity(db.getCategoryCodes()).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	// @Consumes(MediaType.APPLICATION_JSON)
	@Path("/{categorycode}")
	public Response updateCategoryCode(@PathParam("categorycode") String categoryCode, CategoryCode categoryCodeIn) {

		if (! categoryCode.equals(categoryCodeIn.getCategoryCode())) {
			return Response.status(400).entity("{\"error\":\" Mismatch between body and URL\"}").build();
		}
		try {
			CategoryCode fetchedCategoryCode = db.updateCategoryCode(categoryCodeIn);
			return Response.status(200).entity(fetchedCategoryCode).build();
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
	public Response addCategoryCode(CategoryCode categoryCodeIn) {

		CategoryCode categoryCode;
		try {
			categoryCode = db.addCategoryCode(categoryCodeIn);
		} catch (DuplicateDataException e) {
			return Response.status(422).entity(e.getMessage()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		return Response.status(201).entity(categoryCode).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{categorycode}")
	public Response deleteCategoryCode(@PathParam("categorycode") String categoryCode) {

		try {
			db.deleteCategoryCode(categoryCode);
		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + categoryCode + " not found\"}").build();
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
	public Response deleteCategoryCodeByKey(@PathParam("key") int key) {

		try {
			db.deleteCategoryCodeByKey(key);
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
