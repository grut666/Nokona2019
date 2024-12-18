package com.nokona.resource;

import java.util.List;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nokona.data.NokonaDatabaseEmp;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseConnectionException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.model.Employee;
import com.nokona.model.Labels;
import com.nokona.utilities.BarCodeUtilities;

@Path("/employees")
@ApplicationScoped
public class NokonaEmployeeResource {


	@Inject
	private NokonaDatabaseEmp db;

	public NokonaEmployeeResource() {
		super();
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
	@Path("/byBarCodeId/{barCodeId}")
	public Response getEmployeeByBarCodeId(@PathParam("barCodeId") int barCodeId) {

		Employee emp;

		try {
			emp = db.getEmployeeByBarCodeId(barCodeId);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + barCodeId + " not found\"}").build();
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
	public Response getEmployees(@QueryParam ("active") String active) {

		try {
//			MySqlToAccess.main(null);
			List<Employee> allEmployees = db.getEmployees();
			if (active != null) {
				for (int x = allEmployees.size() - 1; x >= 0; x--) {
					if (active.startsWith("f") || active.startsWith("F")) {
						if (allEmployees.get(x).isActive()) {
							allEmployees.remove(x);
						}
					} else {
						if (! allEmployees.get(x).isActive()) {
							allEmployees.remove(x);
						}
					}
				}
			}
			return Response.status(200).entity(allEmployees).build();
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
//			TransferToAccess.transfer("EMP_D");
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
	@Path("/labels/{empId}/{quantity}")
	public Response getEmployeeLabels(@PathParam("empId") String user, @PathParam("quantity") int quantity) {
		// Will now always print
		Labels labels;
		try {
			Employee emp = db.getEmployee(user);
			labels = new Labels();
			labels.setLabels(BarCodeUtilities.generateEmployeeLabels(emp, quantity));
			new NokonaLabelsResource().printLabels(labels);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + user + " not found\"}").build();
		} catch (NullInputDataException ex) {
			return Response.status(422).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();

		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

//		return Response.status(200).entity(labels).build();
		return Response.status(200).entity("Success").build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/labels/{user}")
	public Response getEmployeeLabelsDefaultOnePage(@PathParam("user") String user) {
		return getEmployeeLabels(user, 1);
	}
	

	}


