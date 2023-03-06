package com.nokona.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.nokona.data.NokonaDatabaseEmp;
import com.nokona.data.NokonaDatabaseLaborCode;
import com.nokona.db.NokonaDAOEmployee;
import com.nokona.db.NokonaDAOLaborCode;
import com.nokona.enums.LaborType;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.model.Employee;
import com.nokona.model.LaborCode;

class TestLaborCodeDB {

	private static NokonaDatabaseLaborCode db;
	private static Connection conn;

	@BeforeAll
	private static void setup() throws DatabaseException, SQLException {
		db = new NokonaDAOLaborCode("root", "xyz1234!");
		conn = db.getConn();
		conn.setAutoCommit(false);
	}

	@AfterEach
	private void tearDown() throws DatabaseException, SQLException {
		db.rollback();
	}

	@Test
	void testGetEmployeeZeroArguments() throws DatabaseException {
		LaborCode laborCode = new LaborCode();
		assertEquals(0, laborCode.getLaborCode());
		assertEquals("", laborCode.getDescription());
		assertEquals(0, laborCode.getKey());
		assertEquals(0, laborCode.getRate());
	}
	@Test
	void testGetEmployeeFullArguments() throws DatabaseException {
		LaborCode laborCode = new LaborCode(10, 50, "Testing Labor Code", 5.16, LaborType.UNKNOWN );
		assertEquals(50, laborCode.getLaborCode());
		assertEquals("Testing Labor Code", laborCode.getDescription());
		assertEquals(10, laborCode.getKey());
		assertEquals(5.16, laborCode.getRate());
	}


	@Test
	void testGetEmployeeFromDBByID() throws DatabaseException {
		LaborCode laborCode = db.getLaborCode(7);
		assertEquals(7, laborCode.getLaborCode());
		assertEquals("CLICK LEATHER", laborCode.getDescription());
		assertEquals(12, laborCode.getKey());
		assertEquals(10.0, laborCode.getRate());

	}
	@Test
	void testGetEmployeeFromDBByKey() throws DatabaseException {
		LaborCode laborCode = db.getLaborCodeByKey(12);
		assertEquals(7, laborCode.getLaborCode());
		assertEquals("CLICK LEATHER", laborCode.getDescription());
		assertEquals(12, laborCode.getKey());
		assertEquals(10.0, laborCode.getRate());

	}


	@Test
	void testGetEmployeeFromDBException() {

		Assertions.assertThrows(DataNotFoundException.class, () -> {
			db.getLaborCode(12345);
		});
		
		Assertions.assertThrows(DataNotFoundException.class, () -> {
			db.getLaborCodeByKey(666666);
		});
	}

	@Test
	void testGetLaborCodesFromDB() throws DatabaseException {
		List<LaborCode> laborCode = db.getLaborCodes();
		assertEquals(17, laborCode.size());

	}
//
//	@Test
//	void testUpdateEmployee() throws DatabaseException {
//		Employee emp = db.getEmployeeByKey(149);
//		emp.setFirstName("adolph");
//		emp.setLastName("hitler");
//		emp.setBarCodeID(666);
//		emp.setEmpId("HIT666");
//		emp.setLaborCode(6);
//		emp.setActive(false);
//		Employee newEmp = db.updateEmployee(emp);
//		assertEquals("HITLER", newEmp.getLastName());
//		assertEquals("ADOLPH", newEmp.getFirstName());
//		assertEquals(666, newEmp.getBarCodeID());
//		assertEquals(149, newEmp.getKey());
//		assertEquals("HIT666", newEmp.getEmpId());
//		assertEquals(6, newEmp.getLaborCode());
//		assertEquals(false, newEmp.isActive());
//	}
//
//	@Test
//	void testDeleteEmployee() throws DatabaseException {
//		List<Employee> employees = db.getEmployees();
//		int startingCount = employees.size();
//		db.deleteEmployee("BAL10");
//		assertEquals(startingCount - 1, db.getEmployees().size());
//
//	}
//
//	@Test
//	void testDeleteEmployeeNullException() throws DatabaseException {
//		Assertions.assertThrows(DataNotFoundException.class, () -> {
//			db.deleteEmployeeByKey(666666);
//		});
//	}
//
//	@Test
//	void testDeleteEmployeeByKey() throws DatabaseException {
//		List<Employee> employees = db.getEmployees();
//		int startingCount = employees.size();
//		db.deleteEmployeeByKey(562);
//		assertEquals(startingCount - 1, db.getEmployees().size());
//
//	}
//	@Test
//	void testAddEmployee() throws DatabaseException {
//		List<Employee> employees = db.getEmployees();
//		int startingCount = employees.size();
//		db.deleteEmployeeByKey(562);
//		assertEquals(startingCount - 1, db.getEmployees().size());
//
//	}
//	@Test
//	void testAddEmployeeDupeBarCodeIDException() throws DatabaseException {
//		Employee employee = new Employee(-1, "HITLER", "ADOLPH", 587, 7, "HIT666", true);
//		Assertions.assertThrows(DuplicateDataException.class, () -> {
//			db.addEmployee(employee);
//		});
//	}
//	@Test
//	void testAddEmployeeDupeEmpIDException() throws DatabaseException {
//		Employee employee = new Employee(-1, "HITLER", "ADOLPH", 6666, 7, "BOG10", true);
//		Assertions.assertThrows(DuplicateDataException.class, () -> {
//			db.addEmployee(employee);
//		});
//	}
//	@Test
//	void testAddEmployeeInvalidLaborCodeException() throws DatabaseException {
//		Employee employee = new Employee(-1,  "HITLER", "ADOLPH", 6666, 666, "HIT666", true);
//		Assertions.assertThrows(DataNotFoundException.class, () -> {
//			db.addEmployee(employee);
//		});
//	}
//	@Test
//	void testAddEmployeeNullException() throws DatabaseException {
//		
//		Assertions.assertThrows(NullInputDataException.class, () -> {
//			db.addEmployee(null);
//		});
//	}

}
