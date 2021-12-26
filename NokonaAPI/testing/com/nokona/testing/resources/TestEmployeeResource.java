package com.nokona.testing.resources;

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
	private void setupBeforeEach() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testEmployeeGoodGet() throws DatabaseException {
		Mockito.when(db.getEmployee(Mockito.anyString())) 
				.thenReturn(new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false));
		assertEquals("MARY N.", ((Employee) (empResource.getEmployee("MOL20").getEntity())).getFirstName());
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
		Mockito.when(db.getEmployeeByKey(Mockito.anyInt()))
				.thenReturn(new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false));
		assertEquals("MARY N.", ((Employee) (empResource.getEmployeeByKey(167).getEntity())).getFirstName());
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
		assertEquals(200, empResource.getEmployees(null).getStatus());
	}

	@Test
	public void testEmployeesGet500() throws DatabaseException {
		Mockito.when(db.getEmployees()).thenThrow(new DatabaseConnectionException("Broken"));
		assertEquals(500, empResource.getEmployees(null).getStatus());
	}

	@Test
	public void testEmployeesGet503() throws DatabaseException {
		Mockito.when(db.getEmployees()).thenThrow(new DatabaseException("Broken"));
		assertEquals(503, empResource.getEmployees(null).getStatus());
	}

	@Test
	public void testEmployeesGoodPut() throws DatabaseException {
		Mockito.when(db.updateEmployee(Mockito.any(Employee.class)))
				.thenReturn(new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false));
		assertEquals(200, empResource
				.updateEmployee("MOL20", new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false)).getStatus());
	}

	@Test
	public void testEmployeesPut400() throws DatabaseException {
		assertEquals(400, empResource
				.updateEmployee("MOL10", new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false)).getStatus());
	}

	@Test
	public void testEmployeesPut422() throws DatabaseException {
		Mockito.when(db.updateEmployee(Mockito.any(Employee.class))).thenThrow(new DuplicateDataException("Broken"));
		assertEquals(422, empResource
				.updateEmployee("MOL20", new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false)).getStatus());
	}

	@Test
	public void testEmployeesPut500() throws DatabaseException {
		Mockito.when(db.updateEmployee(Mockito.any(Employee.class)))
				.thenThrow(new DatabaseConnectionException("Broken"));
		assertEquals(500, empResource
				.updateEmployee("MOL20", new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false)).getStatus());
	}

	@Test
	public void testEmployeesPut503() throws DatabaseException {
		Mockito.when(db.updateEmployee(Mockito.any(Employee.class))).thenThrow(new DatabaseException("Broken"));
		assertEquals(503, empResource
				.updateEmployee("MOL20", new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false)).getStatus());
	}

	//
	@Test
	public void testEmployeesAdd201() throws DatabaseException {
		assertEquals(201,
				empResource.addEmployee(new Employee(167, "HITLER", "ADOLPH", 6666, 7, "ADO20", true)).getStatus());
	}

	@Test
	public void testEmployeesAdd422() throws DatabaseException {
		Mockito.when(db.addEmployee(Mockito.any(Employee.class))).thenThrow(new DuplicateDataException("Broken"));
		assertEquals(422,
				empResource.addEmployee(new Employee(167, "HITLER", "ADOLPH", 6666, 7, "ADO20", true)).getStatus());
	}

	@Test
	public void testEmployeesAdd500() throws DatabaseException {
		Mockito.when(db.addEmployee(Mockito.any(Employee.class))).thenThrow(new DatabaseConnectionException("Broken"));
		assertEquals(500,
				empResource.addEmployee(new Employee(167, "HITLER", "ADOLPH", 6666, 7, "ADO20", true)).getStatus());
	}

	@Test
	public void testEmployeesAdd503() throws DatabaseException {
		Mockito.when(db.addEmployee(Mockito.any(Employee.class))).thenThrow(new DatabaseException("Broken"));
		assertEquals(503,
				empResource.addEmployee(new Employee(167, "HITLER", "ADOLPH", 6666, 7, "ADO20", true)).getStatus());
	}
	//

	//
	// @DELETE

	@Test
	public void testEmployeesDel200() throws DatabaseException {
		assertEquals(200, empResource.deleteEmployee("ADO20").getStatus());
	}

	@Test
	public void testEmployeesDel404() throws DatabaseException {
//		Mockito.when(db.deleteEmployee(Mockito.anyString())).thenThrow(new DuplicateDataException("Broken"));
		Mockito.doThrow(DataNotFoundException.class).when(db).deleteEmployee(Mockito.anyString());
		assertEquals(404, empResource.deleteEmployee("ADO20").getStatus());
	}

	@Test
	public void testEmployeesDel500() throws DatabaseException {
		Mockito.doThrow(DatabaseConnectionException.class).when(db).deleteEmployee(Mockito.anyString());
		assertEquals(500, empResource.deleteEmployee("ADO20").getStatus());
	}

	@Test
	public void testEmployeesDel503() throws DatabaseException {
		Mockito.doThrow(DatabaseException.class).when(db).deleteEmployee(Mockito.anyString());
		assertEquals(503, empResource.deleteEmployee("ADO20").getStatus());
	}
	//
	
	@Test
	public void testEmployeesDelByKey200() throws DatabaseException {
		assertEquals(200, empResource.deleteEmployeeByKey(7777).getStatus());
	}

	@Test
	public void testEmployeesDelByKey404() throws DatabaseException {
		Mockito.doThrow(DataNotFoundException.class).when(db).deleteEmployeeByKey(Mockito.anyInt());
		assertEquals(404, empResource.deleteEmployeeByKey(7777).getStatus());
	}

	@Test
	public void testEmployeesDelByKey500() throws DatabaseException {
		Mockito.doThrow(DatabaseConnectionException.class).when(db).deleteEmployeeByKey(Mockito.anyInt());
		assertEquals(500, empResource.deleteEmployeeByKey(7777).getStatus());
	}

	@Test
	public void testEmployeesDelByKey503() throws DatabaseException {
		Mockito.doThrow(DatabaseException.class).when(db).deleteEmployeeByKey(Mockito.anyInt());
		assertEquals(503, empResource.deleteEmployeeByKey(7777).getStatus());
	}
	
	//
	
	@Test
	public void testEmployeesPrint200() throws DatabaseException {
		Mockito.when(db.getEmployee(Mockito.anyString())).thenReturn(new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false));
		assertEquals(200, empResource
				.getEmployeeLabels(Mockito.anyString(), 2).getStatus());
	}

	@Test
	public void testEmployeesPrint404() throws DatabaseException {
		Mockito.when(db.getEmployee(Mockito.anyString())).thenThrow(new DataNotFoundException("Broken"));
		assertEquals(404, empResource
				.getEmployeeLabels(Mockito.anyString(), 2).getStatus());
	}

	@Test
	public void testEmployeesPrint500() throws DatabaseException {
		Mockito.when(db.getEmployee(Mockito.anyString()))
				.thenThrow(new DatabaseConnectionException("Broken"));
		assertEquals(500, empResource
				.getEmployeeLabels(Mockito.anyString(), 2).getStatus());
	}

	@Test
	public void testEmployeesPrint503() throws DatabaseException {
		Mockito.when(db.getEmployee(Mockito.anyString())).thenThrow(new DatabaseException("Broken"));
		assertEquals(503, empResource
				.getEmployeeLabels(Mockito.anyString(), 2).getStatus());
	}
	@Test
	public void testEmployeesPrintDefault200() throws DatabaseException {
		Mockito.when(db.getEmployee(Mockito.anyString())).thenReturn(new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false));
		assertEquals(200, empResource
				.getEmployeeLabelsDefaultOnePage(Mockito.anyString()).getStatus());
	}

}
