package com.nokona.testing;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.nokona.db.NokonaDAOEmployee;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseConnectionException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.UnknownDatabaseException;
import com.nokona.model.Employee;
import com.nokona.resource.NokonaEmployeeResource;

class TestEmployeeResource {
	@Mock
	private NokonaDAOEmployee db;

	@InjectMocks
	private NokonaEmployeeResource empResource;

	@BeforeEach
	private void setupBeforeEach()  {
		MockitoAnnotations.initMocks(this);
	}
	@Test
	public void testEmployeeGoodGet() throws DatabaseException {
		Mockito.when(db.getEmployee(Mockito.anyString())).thenReturn(new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false));
		assertEquals("MARY N.", ((Employee)(empResource.getEmployee("MOL20").getEntity())).getFirstName());	
	}
	@Test
	public void testEmployeeGet404() throws DatabaseException {
		Mockito.when(db.getEmployee(Mockito.anyString())).thenThrow(new DataNotFoundException("Broken"));
		assertEquals(404, empResource.getEmployee("aaa").getStatus());	
	}
	@Test
	public void testEmployeeGet500() throws DatabaseException {
		Mockito.when(db.getEmployee(Mockito.anyString())).thenThrow(new DatabaseConnectionException("Broken"));
		assertEquals(500, empResource.getEmployee("aaa").getStatus());
	}
	@Test
	public void testEmployee503() throws DatabaseException {
		Mockito.when(db.getEmployee(Mockito.anyString())).thenThrow(new UnknownDatabaseException("Broken"));
		assertEquals(503, empResource.getEmployee("aaa").getStatus());
	}
	@Test
	public void testEmployeeByKeyGoodGet() throws DatabaseException {
		Mockito.when(db.getEmployeeByKey(Mockito.anyInt())).thenReturn(new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false));
		assertEquals("MARY N.", ((Employee)(empResource.getEmployeeByKey(167).getEntity())).getFirstName());	
		assertEquals(200, empResource.getEmployeeByKey(167).getStatus());
	}
	@Test
	public void testEmployeeByKeyGet404() throws DatabaseException {
		Mockito.when(db.getEmployeeByKey(Mockito.anyInt())).thenThrow(new DataNotFoundException("Broken"));
		assertEquals(404, empResource.getEmployeeByKey(167).getStatus());	
	}
	@Test
	public void testEmployeeByKeyGet500() throws DatabaseException {
		Mockito.when(db.getEmployeeByKey(Mockito.anyInt())).thenThrow(new DatabaseConnectionException("Broken"));
		assertEquals(500, empResource.getEmployeeByKey(167).getStatus());
	}
	@Test
	public void testEmployeeByKey503() throws DatabaseException {
		Mockito.when(db.getEmployeeByKey(Mockito.anyInt())).thenThrow(new UnknownDatabaseException("Broken"));
		assertEquals(503, empResource.getEmployeeByKey(167).getStatus());
	}
	@Test
	public void testEmployeesGoodGet() throws DatabaseException {
		Mockito.when(db.getEmployees()).thenReturn(new ArrayList<Employee>());	
		assertEquals(200, empResource.getEmployees().getStatus());
	}
	@Test
	public void testEmployeesGet500() throws DatabaseException {
		Mockito.when(db.getEmployees()).thenThrow(new DatabaseConnectionException("Broken"));
		assertEquals(500, empResource.getEmployees().getStatus());	
	}
	@Test
	public void testEmployeesGet503() throws DatabaseException {
		Mockito.when(db.getEmployees()).thenThrow(new DatabaseException("Broken"));
		assertEquals(503, empResource.getEmployees().getStatus());
	}
	
	
	@Test
	public void testEmployeesGoodPut() throws DatabaseException {
		Mockito.when(db.updateEmployee( Mockito.any(Employee.class))).thenReturn(new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false));	
		assertEquals(200, empResource.updateEmployee("MOL20", new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false)).getStatus());
	}
	@Test
	public void testEmployeesPut400() throws DatabaseException {
		assertEquals(400, empResource.updateEmployee("MOL10", new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false)).getStatus());	
	}

	@Test
	public void testEmployeesPut422() throws DatabaseException {
		Mockito.when(db.updateEmployee( Mockito.any(Employee.class))).thenThrow(new DuplicateDataException("Broken"));
		assertEquals(422, empResource.updateEmployee("MOL20", new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false)).getStatus());
	}
	@Test
	public void testEmployeesPut500() throws DatabaseException {
		Mockito.when(db.updateEmployee( Mockito.any(Employee.class))).thenThrow(new DatabaseConnectionException("Broken"));
		assertEquals(500, empResource.updateEmployee("MOL20", new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false)).getStatus());
	}
	@Test
	public void testEmployeesPut503() throws DatabaseException {
		Mockito.when(db.updateEmployee( Mockito.any(Employee.class))).thenThrow(new DatabaseException("Broken"));
		assertEquals(503, empResource.updateEmployee("MOL20", new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false)).getStatus());
	}


//
//	@PUT
//	@Produces(MediaType.APPLICATION_JSON)
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Path("/{empId}")
//	public Response updateEmployee(@PathParam("empId") String empId, Employee empIn) {
//
//		if (!empId.equals(empIn.getEmpId())) {
//			return Response.status(400).entity("{\"error\":\" Mismatch between body and URL\"}").build();
//		}
//		try {
//			return Response.status(200).entity(db.updateEmployee(empIn)).build();
//		} catch (DuplicateDataException e) {
//			return Response.status(422).entity(e.getMessage()).build();
//		} catch (DatabaseConnectionException ex) {
//			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		} catch (DatabaseException ex) {
//			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		}
//	}
//
//	@POST
//	@Produces(MediaType.APPLICATION_JSON)
//	@Consumes(MediaType.APPLICATION_JSON)
//
//	@Path("/")
//	public Response addEmployee(Employee empIn) {
//		Employee emp;
//		try {
//			emp = db.addEmployee(empIn);
//		} catch (DuplicateDataException e) {
//			return Response.status(422).entity(e.getMessage()).build();
//		} catch (DatabaseConnectionException ex) {
//			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		} catch (DatabaseException ex) {
//			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		}
//		return Response.status(201).entity(emp).build();
//	}
//
//	@DELETE
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/{user}")
//	public Response deleteEmployee(@PathParam("user") String user) {
//
//		try {
//			db.deleteEmployee(user);
//			return Response.status(200).entity("{\"Success\":\"200\"}").build();
//		} catch (DataNotFoundException ex) {
//			return Response.status(404).entity("{\"error\":\"" + user + " not found\"}").build();
//		} catch (DatabaseConnectionException ex) {
//			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		} catch (DatabaseException ex) {
//			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		}
//
//	}
//	@DELETE
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/bykey/{key}")
//	public Response deleteEmployeeByKey(@PathParam("key") int key) {
//
//		try {
//			db.deleteEmployeeByKey(key);
//			return Response.status(200).entity("{\"Success\":\"200\"}").build();
//		} catch (DataNotFoundException ex) {
//			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
//		} catch (DatabaseConnectionException ex) {
//			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		} catch (DatabaseException ex) {
//			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		}
//
//	}
//
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/labels/{user}/{quantity}")
//	public Response getEmployeeLabels(@PathParam("user") String user, @PathParam("quantity") int quantity) {
//
//		Labels labels;
//		try {
//			Employee emp = db.getEmployee(user);
//			labels = new Labels();
//			labels.setLabels(BarCodeUtilities.generateEmployeeLabels(emp, quantity));
//
//		} catch (DataNotFoundException ex) {
//			return Response.status(404).entity("{\"error\":\"" + user + " not found\"}").build();
//		} catch (DatabaseConnectionException ex) {
//			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		} catch (DatabaseException ex) {
//			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
//		}
//
//		return Response.status(200).entity(labels).build();
//	}
//
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/labels/{user}")
//	public Response getEmployeeLabelsDefaultOnePage(@PathParam("user") String user) {
//		return getEmployeeLabels(user, 1);
//	}

}
