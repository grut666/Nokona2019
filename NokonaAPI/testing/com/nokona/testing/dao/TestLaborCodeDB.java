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
import com.nokona.data.NokonaDatabaseLaborCode;
import com.nokona.db.NokonaDAOLaborCode;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.model.LaborCode;

public class TestLaborCodeDB {
	private static Connection conn;
	private static NokonaDatabaseLaborCode db;

	@BeforeAll
	private static void setupBeforeEach() throws DatabaseException, SQLException {
		db = new NokonaDAOLaborCode(Constants.USER_NAME, Constants.PASSWORD);
		conn = db.getConn();
		conn.setAutoCommit(false);
	}

	@AfterEach
	private void tearDown() throws DatabaseException, SQLException {
		db.rollback();
	}

	@Test
	public void testGetLaborCodeZeroArguments() throws DatabaseException {
		LaborCode lc = new LaborCode();
		assertEquals(-1, lc.getKey());
		assertEquals(0, lc.getLaborCode());
		assertEquals("", lc.getDescription());
		assertEquals(0, lc.getRate());			
		assertEquals("LaborCode(key=-1, laborCode=0, description=, rate=0.0)",
				lc.toString());
	}

	@Test
	public void testGetLaborCodeFullArguments() throws DatabaseException {
		LaborCode lc = new LaborCode(1234, 22, "DOING STUFF", .12);
		assertEquals("DOING STUFF", lc.getDescription());
		assertEquals(1234, lc.getKey());
		assertEquals(22, lc.getLaborCode());
		assertEquals(.12, lc.getRate());
		assertEquals("LaborCode(key=1234, laborCode=22, description=DOING STUFF, rate=0.12)",
				lc.toString());
	}
////
	@Test
	public void testGetLaborCodeFullArgumentsLowerCase() throws DatabaseException {
		LaborCode lc = new LaborCode(1234, 22, "doing stuff", .12);
		db.addLaborCode(lc);
		lc = db.getLaborCode(22);
		assertEquals("DOING STUFF", lc.getDescription());
		assertEquals(22, lc.getLaborCode());
		assertEquals(.12, lc.getRate());
		
	}
//
	@Test
	void testGetLaborCodeFromDBByID() throws DatabaseException {
		LaborCode lc = db.getLaborCode(8);
		assertEquals(10, lc.getRate());
		assertEquals(9, lc.getKey());
		assertEquals(8, lc.getLaborCode());
		assertEquals("FERDINDAND, WEBS", lc.getDescription());

	}
//
	@Test
	void testGetLaborCodeFromDBByKey() throws DatabaseException {
		LaborCode lc = db.getLaborCodeByKey(9);
		assertEquals(10, lc.getRate());
		assertEquals(9, lc.getKey());
		assertEquals(8, lc.getLaborCode());
		assertEquals("FERDINDAND, WEBS", lc.getDescription());
	}
//
	@Test
	void testGetLaborCodeFromDBException() {

		Assertions.assertThrows(DataNotFoundException.class, () -> {
			db.getLaborCode(55);
		});

		Assertions.assertThrows(DataNotFoundException.class, () -> {
			db.getLaborCodeByKey(666666);
		});
	}

	@Test
	void testGetLaborCodesFromDB() throws DatabaseException {
		List<LaborCode> lcs = db.getLaborCodes();
		assertEquals(16, lcs.size());

	}

	@Test
	void testUpdateLaborCode() throws DatabaseException {
		LaborCode lc = db.getLaborCodeByKey(9);
		lc.setDescription("XXX");
		lc.setRate(66.66);
		lc.setLaborCode(99);

		LaborCode newLc = db.updateLaborCode(lc);
		assertEquals("XXX", newLc.getDescription());
		assertEquals(99, newLc.getLaborCode());
		assertEquals(66.66, newLc.getRate());
	}
//
	@Test
	void testDeleteLaborCode() throws DatabaseException {
		List<LaborCode> LaborCodes = db.getLaborCodes();
		int startingCount = LaborCodes.size();
		db.deleteLaborCode(9);
		assertEquals(startingCount - 1, db.getLaborCodes().size());

	}

	@Test
	void testDeleteDataNotFoundException() throws DatabaseException {
		Assertions.assertThrows(DataNotFoundException.class, () -> {
			db.deleteLaborCodeByKey(666666);
		});
	}

	@Test
	void testDeleteLaborCodeByKey() throws DatabaseException {
		List<LaborCode> LaborCodes = db.getLaborCodes();
		int startingCount = LaborCodes.size();
		db.deleteLaborCodeByKey(9);
		assertEquals(startingCount - 1, db.getLaborCodes().size());

	}

	@Test
	void testAddLaborCode() throws DatabaseException {
		LaborCode op = new LaborCode(0, 75, "Something Cool", .66);
		List<LaborCode> LaborCodes = db.getLaborCodes();
		int startingCount = LaborCodes.size();
		db.addLaborCode(op);
		assertEquals(startingCount + 1, db.getLaborCodes().size());

	}

	@Test
	void testAddLaborCodeDupeOpCodeException() throws DatabaseException {
		LaborCode op = new LaborCode(0, 6, "New Record", .66);
		Assertions.assertThrows(DuplicateDataException.class, () -> {
			db.addLaborCode(op);
		});
	}

	@Test
	void testAddLaborCodeNullException() throws DatabaseException {

		Assertions.assertThrows(NullInputDataException.class, () -> {
			db.addLaborCode(null);
		});
	}



}

