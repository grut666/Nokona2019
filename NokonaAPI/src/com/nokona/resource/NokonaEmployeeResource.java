package com.nokona.resource;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.annotation.security.PermitAll;
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

import com.nokona.constants.Constants;
import com.nokona.data.NokonaDatabaseEmp;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseConnectionException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.model.Employee;
import com.nokona.model.Labels;
import com.nokona.utilities.BarCodeUtilities;
import com.nokona.utilities.TransferToAccess;


@Path("/employees")
@PermitAll
public class NokonaEmployeeResource {
	@ApplicationScoped

	@Inject
	private NokonaDatabaseEmp db;

	public NokonaEmployeeResource() {

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{empId}")
	public Response getEmployee(@PathParam("empId") String empId) {
		
		Employee emp;

		try {
			emp = db.getEmployee(empId);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + empId + " not found\"}").build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

		return Response.status(200).entity(emp).build();
	}

	@GET

	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bykey/{key}")
	public Response getEmployeeByKey(@PathParam("key") int key) {

		Employee emp;

		try {
			emp = db.getEmployeeByKey(key);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

		return Response.status(200).entity(emp).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response getEmployees() {

		try {
//			MySqlToAccess.main(null);
			return Response.status(200).entity(db.getEmployees()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		} 

	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{empId}")
	public Response updateEmployee(@PathParam("empId") String empId, Employee empIn) {

		if (!empId.equals(empIn.getEmpId())) {
			return Response.status(400).entity("{\"error\":\" Mismatch between body and URL\"}").build();
		}
		try {
			Employee emp = db.updateEmployee(empIn);
			TransferToAccess.transfer("EMP_U");
			return Response.status(200).entity(emp).build();
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
	public Response addEmployee(Employee empIn) {
		Employee emp;
		try {
			emp = db.addEmployee(empIn);
			TransferToAccess.transfer("EMP_C");
		} catch (DuplicateDataException e) {
			return Response.status(422).entity(e.getMessage()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		return Response.status(201).entity(emp).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{user}")
	public Response deleteEmployee(@PathParam("user") String user) {

		try {
			db.deleteEmployee(user);
			TransferToAccess.transfer("EMP_D");
			return Response.status(200).entity("{\"Success\":\"200\"}").build();
		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + user + " not found\"}").build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bykey/{key}")
	public Response deleteEmployeeByKey(@PathParam("key") int key) {

		try {
			db.deleteEmployeeByKey(key);
			TransferToAccess.transfer("EMP_D");
			return Response.status(200).entity("{\"Success\":\"200\"}").build();
		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/labels/{user}/{quantity}")
	public Response getEmployeeLabels(@PathParam("user") String user, @PathParam("quantity") int quantity) {

		Labels labels;
		try {
			Employee emp = db.getEmployee(user);
			labels = new Labels();
			labels.setLabels(BarCodeUtilities.generateEmployeeLabels(emp, quantity));

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + user + " not found\"}").build();
		} catch (NullInputDataException ex) {
			return Response.status(422).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();

		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

		return Response.status(200).entity(labels).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/labels/{user}")
	public Response getEmployeeLabelsDefaultOnePage(@PathParam("user") String user) {
		return getEmployeeLabels(user, 1);
	}
	

	}


