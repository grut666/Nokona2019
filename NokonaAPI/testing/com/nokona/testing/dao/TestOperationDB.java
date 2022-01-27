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
import com.nokona.data.NokonaDatabaseOperation;
import com.nokona.db.NokonaDAOOperation;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.model.Operation;

class TestOperationDB {
	private static Connection conn;
	private static NokonaDatabaseOperation db;

	@BeforeAll
	private static void setupBeforeEach() throws DatabaseException, SQLException {
		db = new NokonaDAOOperation(Constants.USER_NAME, Constants.PASSWORD);
		conn = db.getConn();
		conn.setAutoCommit(false);
	}

	@AfterEach
	private void tearDown() throws DatabaseException, SQLException {
		db.rollback();
	}

	@Test
	void testGetOperationZeroArguments() throws DatabaseException {
		Operation op = new Operation();
		assertEquals("", op.getDescription());
		assertEquals(0, op.getHourlyRateSAH());
		assertEquals(-1, op.getKey());
		assertEquals(0, op.getLaborCode());
		assertEquals(0, op.getLastStudyYear());
		assertEquals("", op.getOpCode());
//		assertEquals("Operation(opCode=, description=, hourlyRateSAH=0.0, laborCode=0, key=-1, lastStudyYear=0)",
//				op.toString());
	}

	@Test
	void testGetOperationFullArguments() throws DatabaseException {
		Operation op = new Operation( 9693,"A55", "INSTALL RUBBER IN POCKET", 3, .0225, 2020);
		assertEquals("INSTALL RUBBER IN POCKET", op.getDescription());
		assertEquals(.0225, op.getHourlyRateSAH());
		assertEquals(9693, op.getKey());
		assertEquals(3, op.getLaborCode());
		assertEquals(2020, op.getLastStudyYear());
		assertEquals("A55", op.getOpCode());
//		assertEquals("Operation(opCode=A55, description=INSTALL RUBBER IN POCKET, hourlyRateSAH=0.0225, laborCode=3, key=9693, lastStudyYear=2020)",
//				op.toString());
	}
//
	@Test
	void testGetOperationFullArgumentsLowerCase() throws DatabaseException {
		Operation op = new Operation(987654, "zzz", "install rubber in pocket",3,.0225, 2020);
		db.addOperation(op);
		op = db.getOperation("ZZZ");
		assertEquals("INSTALL RUBBER IN POCKET", op.getDescription());
		assertEquals(.0225, op.getHourlyRateSAH());
		assertEquals(3, op.getLaborCode());
		assertEquals(2020, op.getLastStudyYear());
		assertEquals("ZZZ", op.getOpCode());
	}
//
	@Test
	void testGetOperationFromDBByID() throws DatabaseException {
		Operation op = db.getOperation("A55");
		assertEquals(.0225, op.getHourlyRateSAH());
		assertEquals(9693, op.getKey());
		assertEquals(3, op.getLaborCode());
		assertEquals(0, op.getLastStudyYear());
		assertEquals("A55", op.getOpCode());
//		assertEquals("Operation(opCode=A55, description=INSTALL RUBBER IN POCKET, hourlyRateSAH=0.0225, laborCode=3, key=9693, lastStudyYear=0)",
//				op.toString());
	}
//
	@Test
	void testGetOperationFromDBByKey() throws DatabaseException {
		Operation op = db.getOperationByKey(9693);
		assertEquals(.0225, op.getHourlyRateSAH());
		assertEquals(9693, op.getKey());
		assertEquals(3, op.getLaborCode());
		assertEquals(0, op.getLastStudyYear());
		assertEquals("A55", op.getOpCode());
	}
//
	@Test
	void testGetOperationFromDBException() {

		Assertions.assertThrows(DataNotFoundException.class, () -> {
			db.getOperation("HITLER");
		});
		Assertions.assertThrows(NullInputDataException.class, () -> {
			db.getOperation(null);
		});
		Assertions.assertThrows(DataNotFoundException.class, () -> {
			db.getOperationByKey(666666);
		});
	}
//
	@Test
	void testGetOperationsFromDB() throws DatabaseException {
		List<Operation> ops = db.getOperations();
		assertEquals(2852, ops.size());

	}
//
	@Test
	void testUpdateOperation() throws DatabaseException {
		Operation op = db.getOperationByKey(9693);
		op.setHourlyRateSAH(.0666);
		op.setLaborCode(12);
		op.setLastStudyYear(2020);
		op.setOpCode("ZZZ");

		Operation newOp = db.updateOperation(op);
		assertEquals(.0666, newOp.getHourlyRateSAH());
		assertEquals(12, newOp.getLaborCode());
		assertEquals(2020, newOp.getLastStudyYear());
		assertEquals("ZZZ", newOp.getOpCode());
		assertEquals(9693, newOp.getKey());
	}
//
	@Test
	void testDeleteOperation() throws DatabaseException {
		List<Operation> Operations = db.getOperations();
		int startingCount = Operations.size();
		db.deleteOperation("A50");
		assertEquals(startingCount - 1, db.getOperations().size());

	}

	@Test
	void testDeleteDataNotFoundException() throws DatabaseException {
		Assertions.assertThrows(DataNotFoundException.class, () -> {
			db.deleteOperationByKey(666666);
		});
	}

	@Test
	void testDeleteOperationByKey() throws DatabaseException {
		List<Operation> Operations = db.getOperations();
		int startingCount = Operations.size();
		db.deleteOperationByKey(9693);
		assertEquals(startingCount - 1, db.getOperations().size());

	}
//
	@Test
	void testAddOperation() throws DatabaseException {
		Operation op = new Operation(555,"yyy", "Do something cool",4, .0678,2010);
		List<Operation> operations = db.getOperations();
		int startingCount = operations.size();
		db.addOperation(op);
		assertEquals(startingCount + 1, db.getOperations().size());

	}
//
	@Test
	void testAddOperationDupeOpCodeException() throws DatabaseException {
		Operation op = new Operation(555, "A50", "Do something cool",4, .0678,2010);
		Assertions.assertThrows(DuplicateDataException.class, () -> {
			db.addOperation(op);
		});
	}
//
	@Test
	void testAddOperationNullException() throws DatabaseException {

		Assertions.assertThrows(NullInputDataException.class, () -> {
			db.addOperation(null);
		});
	}



}
