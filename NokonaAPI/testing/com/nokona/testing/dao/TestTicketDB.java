package com.nokona.testing.dao;

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
import com.nokona.data.NokonaDatabaseTicket;
import com.nokona.db.NokonaDAOEmployee;
import com.nokona.db.NokonaDAOTicket;
import com.nokona.enums.TicketStatus;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.model.Employee;
import com.nokona.model.Ticket;

class TestTicketDB {

	private static NokonaDatabaseTicket db;
	private static Connection conn;

	@BeforeAll
	private static void setup() throws DatabaseException, SQLException {
		db = new NokonaDAOTicket(Constants.USER_NAME, Constants.PASSWORD);
		conn = db.getConn();
		conn.setAutoCommit(false);
	}

	@AfterEach
	private void tearDown() throws DatabaseException, SQLException {
		db.rollback();
	}

	@Test
	void testGetTicketZeroArguments() throws DatabaseException {
		Ticket ticket = new Ticket();
		assertEquals(0, ticket.getTicketDetails().size());
		assertEquals(-1, ticket.getTicketHeader().getKey());
		
	}
	@Test
	void testGetTicketNullArguments() throws DatabaseException {
		Ticket ticket = new Ticket(null, null);
		assertEquals(0, ticket.getTicketDetails().size());
		assertEquals(-1, ticket.getTicketHeader().getKey());
		
	}
	@Test
	void testGetTicketByKey() throws DatabaseException {
		Ticket ticket = db.getTicketByKey(35514);
		assertEquals(35514, ticket.getTicketHeader().getKey());
		assertEquals("WALNUT 33.5\", CLOSED WEB - LEFT", ticket.getTicketHeader().getDescription());
		assertEquals("Tue Jan 01 18:00:00 CST 2019", ticket.getTicketHeader().getDateCreated().toString());
		assertEquals("Tue Jan 01 18:00:00 CST 2019", ticket.getTicketHeader().getDateStatus().toString());
		assertEquals("C", ticket.getTicketHeader().getTicketStatus().getTicketStatus());
		assertEquals("W-3350C-LH", ticket.getTicketHeader().getJobId());
		assertEquals(6, ticket.getTicketHeader().getQuantity());
		
	}

//	@Test
//	void testGetEmployeeFullArguments() throws DatabaseException {
//		Employee emp = new Employee(167, "MOLSBEE", "MARY N.", 8654, 7, "MOL20", false);
//		assertEquals("MOLSBEE", emp.getLastName());
//		assertEquals("MARY N.", emp.getFirstName());
//		assertEquals(8654, emp.getBarCodeID());
//		assertEquals(167, emp.getKey());
//		assertEquals("MOL20", emp.getEmpId());
//		assertEquals(7, emp.getLaborCode());
//		assertEquals(false, emp.isActive());
//		assertEquals(
//				"Employee [key=167, lastName=MOLSBEE, firstName=MARY N., barCodeID=8654, laborCode=7, empId=MOL20, active=false]",
//				emp.toString());
//	}
//
//	// @Test
//	// void testGetEmployeeFullArgumentsLowerCase() throws DatabaseException {
//	// Employee emp = new Employee(167,"molsbee", "mary n.",8654, 7, "mol20",
//	// false);
//	// assertEquals("MOLSBEE", emp.getLastName());
//	// assertEquals("MARY N.", emp.getFirstName());
//	// assertEquals(8654, emp.getBarCodeID());
//	// assertEquals(167, emp.getKey());
//	// assertEquals("MOL20", emp.getEmpId());
//	// assertEquals(7, emp.getLaborCode());
//	// assertEquals(false, emp.isActive());
//	// assertEquals("Employee [key=167, lastName=MOLSBEE, firstName=MARY N.,
//	// barCodeID=8654, laborCode=7, empId=MOL20, active=false]", emp.toString());
//	// }
//	@Test
//	void testGetEmployeeFromDBByID() throws DatabaseException {
//		Employee emp = db.getEmployee("MOL20");
//		assertEquals("MOLSBEE", emp.getLastName());
//		assertEquals("MARY N.", emp.getFirstName());
//		assertEquals(8654, emp.getBarCodeID());
//		assertEquals(167, emp.getKey());
//		assertEquals("MOL20", emp.getEmpId());
//		assertEquals(7, emp.getLaborCode());
//		assertEquals(false, emp.isActive());
//		assertEquals(
//				"Employee [key=167, lastName=MOLSBEE, firstName=MARY N., barCodeID=8654, laborCode=7, empId=MOL20, active=false]",
//				emp.toString());
//	}
//
//	@Test
//	void testGetEmployeeFromDBByKey() throws DatabaseException {
//		Employee emp = db.getEmployeeByKey(149);
//		assertEquals("GOMEZ", emp.getLastName());
//		assertEquals("MARTIN", emp.getFirstName());
//		assertEquals(5002, emp.getBarCodeID());
//		assertEquals(149, emp.getKey());
//		assertEquals("GOM30", emp.getEmpId());
//		assertEquals(9, emp.getLaborCode());
//		assertEquals(true, emp.isActive());
//	}
//
//	@Test
//	void testGetEmployeeFromDBException() {
//
//		Assertions.assertThrows(DataNotFoundException.class, () -> {
//			db.getEmployee("HITLER");
//		});
//		Assertions.assertThrows(NullInputDataException.class, () -> {
//			db.getEmployee(null);
//		});
//		Assertions.assertThrows(DataNotFoundException.class, () -> {
//			db.getEmployee("HITLER");
//		});
//		Assertions.assertThrows(DataNotFoundException.class, () -> {
//			db.getEmployeeByKey(666666);
//		});
//	}
//
//	@Test
//	void testGetEmployeesFromDB() throws DatabaseException {
//		List<Employee> emps = db.getEmployees();
//		assertEquals(269, emps.size());
//
//	}
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
