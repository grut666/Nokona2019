package com.nokona.testing.dao;
// Testing for Employees
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.nokona.constants.Constants;
import com.nokona.data.NokonaDatabaseEmp;
import com.nokona.db.NokonaDAOEmployee;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.model.Employee;

class TestEmployeeDB {
	private static Connection conn;
	private static NokonaDatabaseEmp db;

	@BeforeAll
	private static void setupBeforeEach() throws DatabaseException, SQLException {
		db = new NokonaDAOEmployee(Constants.USER_NAME, Constants.PASSWORD);
		conn = db.getConn();
		conn.setAutoCommit(false);
	}

	@AfterEach
	private void tearDown() throws DatabaseException, SQLException {
		db.rollback();
	}

	@Test
	void testGetEmployeeZeroArguments() throws DatabaseException {
		Employee emp = new Employee();
		assertEquals("", emp.getLastName());
		assertEquals("", emp.getFirstName());
		assertEquals(0, emp.getBarCodeID());
		assertEquals(-1, emp.getKey());
		assertEquals("", emp.getEmpId());
		assertEquals(0, emp.getLaborCode());
		assertEquals(false, emp.isActive());
		assertEquals(
				"Employee(key=-1, lastName=, firstName=, barCodeID=0, laborCode=0, empId=, active=false)",
				emp.toString());
	}

	@Test
	void testGetEmployeeFullArguments() throws DatabaseException {
		Employee emp = new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false);
		assertEquals("MOLSBEE", emp.getLastName());
		assertEquals("MARY N.", emp.getFirstName());
		assertEquals(8654, emp.getBarCodeID());
		assertEquals(167, emp.getKey());
		assertEquals("MOL20", emp.getEmpId());
		assertEquals(7, emp.getLaborCode());
		assertEquals(false, emp.isActive());
		assertEquals(
				"Employee(key=167, lastName=MOLSBEE, firstName=MARY N., barCodeID=8654, laborCode=7, empId=MOL20, active=false)",
				emp.toString());
	}
	@Test
	void testGetEmployeeFullArgumentsLowerCase() throws DatabaseException {
		Employee emp = new Employee(6666, "molsbee", "mary n.", 9999, 7, "xxxx20", false);
		db.addEmployee(emp);
		emp = db.getEmployee("XXXX20");
		assertEquals("MOLSBEE", emp.getLastName());
		assertEquals("MARY N.", emp.getFirstName());
		assertEquals(9999, emp.getBarCodeID());
		assertEquals("XXXX20", emp.getEmpId());
		assertEquals(7, emp.getLaborCode());
		assertEquals(false, emp.isActive());
	}

	@Test
	void testGetEmployeeFromDBByID() throws DatabaseException {
		Employee emp = db.getEmployee("MOL10");
		assertEquals("MOLSBEE", emp.getLastName());
		assertEquals("RANDALL", emp.getFirstName());
		assertEquals(3586, emp.getBarCodeID());
		assertEquals(385, emp.getKey());
		assertEquals("MOL10", emp.getEmpId());
		assertEquals(5, emp.getLaborCode());
		assertEquals(true, emp.isActive());
//		assertEquals(
//				"Employee(key=167, lastName=MOLSBEE, firstName=MARY N., barCodeID=8654, laborCode=7, empId=MOL20, active=false)",
//				emp.toString());
	}


	@Test
	void testGetEmployeeFromDBByKey() throws DatabaseException {
		Employee emp = db.getEmployeeByKey(149);
		assertEquals("GOMEZ", emp.getLastName());
		assertEquals("MARTIN", emp.getFirstName());
		assertEquals(5002, emp.getBarCodeID());
		assertEquals(149, emp.getKey());
		assertEquals("GOM30", emp.getEmpId());
		assertEquals(9, emp.getLaborCode());
		assertEquals(true, emp.isActive());
	}

	@Test
	void testGetEmployeeFromDBException() {
		Assertions.assertThrows(NullInputDataException.class, () -> {
			db.getEmployee(null);
		});
		Assertions.assertThrows(DataNotFoundException.class, () -> {
			db.getEmployee("HITLER");
		});
		Assertions.assertThrows(DataNotFoundException.class, () -> {
			db.getEmployeeByKey(666666);
		});
	}

	@Test
	void testGetEmployeesFromDB() throws DatabaseException {
		List<Employee> emps = db.getEmployees();
		assertEquals(45, emps.size());

	}

	@Test
	void testUpdateEmployee() throws DatabaseException {
		Employee emp = db.getEmployeeByKey(149);
		emp.setFirstName("adolph");
		emp.setLastName("hitler");
		emp.setBarCodeID(666);
		emp.setEmpId("HIT666");
		emp.setLaborCode(6);
		emp.setActive(false);
		Employee newEmp = db.updateEmployee(emp);
		assertEquals("HITLER", newEmp.getLastName());
		assertEquals("ADOLPH", newEmp.getFirstName());
		assertEquals(666, newEmp.getBarCodeID());
		assertEquals(149, newEmp.getKey());
		assertEquals("HIT666", newEmp.getEmpId());
		assertEquals(6, newEmp.getLaborCode());
		assertEquals(false, newEmp.isActive());
	}

	@Test
	void testDeleteEmployee() throws DatabaseException {
		List<Employee> employees = db.getEmployees();
		int startingCount = employees.size();
		db.deleteEmployee("BAS10");
		assertEquals(startingCount - 1, db.getEmployees().size());

	}

	@Test
	void testDeleteDataNotFoundException() throws DatabaseException {
		Assertions.assertThrows(DataNotFoundException.class, () -> {
			db.deleteEmployeeByKey(666666);
		});
	}

	@Test
	void testDeleteEmployeeByKey() throws DatabaseException {
		List<Employee> employees = db.getEmployees();
		int startingCount = employees.size();
		db.deleteEmployeeByKey(757);
		assertEquals(startingCount - 1, db.getEmployees().size());

	}

	@Test
	void testAddEmployee() throws DatabaseException {
		Employee emp = new Employee(0, "Wonka", "Willie", 9999, 7, "Z99", true);
		List<Employee> employees = db.getEmployees();
		int startingCount = employees.size();
		db.addEmployee(emp);
		assertEquals(startingCount + 1, db.getEmployees().size());
	}

	@Test
	void testAddEmployeeDupeBarCodeIDException() throws DatabaseException {
		Employee employee = new Employee(-1, "HITLER", "ADOLPH", 6164, 7, "HIT666", true);
		Assertions.assertThrows(DuplicateDataException.class, () -> {
			db.addEmployee(employee);
		});
	}

	@Test
	void testAddEmployeeDupeEmpIDException() throws DatabaseException {
		Employee employee = new Employee(-1, "HITLER", "ADOLPH", 6666, 7, "CER10", true);
		Assertions.assertThrows(DuplicateDataException.class, () -> {
			db.addEmployee(employee);
		});
	}

	@Test
	void testAddEmployeeInvalidLaborCodeException() throws DatabaseException {
		Employee employee = new Employee(-1, "HITLER", "ADOLPH", 6666, 666, "HIT666", true);
		Assertions.assertThrows(DataNotFoundException.class, () -> {
			db.addEmployee(employee);
		});
	}

	@Test
	void testAddEmployeeNullException() throws DatabaseException {

		Assertions.assertThrows(NullInputDataException.class, () -> {
			db.addEmployee(null);
		});
	}

}
