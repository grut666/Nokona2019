package com.nokona.testing.resources;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.nokona.db.NokonaDAOOperation;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseConnectionException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.UnknownDatabaseException;
import com.nokona.model.Operation;
import com.nokona.resource.NokonaOperationResource;

class TestOperationResource {
	@Mock
	private NokonaDAOOperation db;

	@InjectMocks
	private NokonaOperationResource opResource;

	@BeforeEach
	private void setupBeforeEach() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testOperationGoodGet() throws DatabaseException {
		Mockito.when(db.getOperation(Mockito.anyString()))
				.thenReturn(new Operation("A55", "INSTALL RUBBER IN POCKET",.0225,3,9693,0));
		assertEquals("A55", ((Operation) (opResource.getOperation("MOL20").getEntity())).getOpCode());
	}

	@Test
	public void testOperationGet404() throws DatabaseException {
		Mockito.when(db.getOperation(Mockito.anyString())).thenThrow(new DataNotFoundException("Broken"));
		assertEquals(404, opResource.getOperation("aaa").getStatus());
	}

	@Test
	public void testOperationGet500() throws DatabaseException {
		Mockito.when(db.getOperation(Mockito.anyString())).thenThrow(new DatabaseConnectionException("Broken"));
		assertEquals(500, opResource.getOperation("aaa").getStatus());
	}

	@Test
	public void testOperation503() throws DatabaseException {
		Mockito.when(db.getOperation(Mockito.anyString())).thenThrow(new UnknownDatabaseException("Broken"));
		assertEquals(503, opResource.getOperation("aaa").getStatus());
	}

	@Test
	public void testOperationByKeyGoodGet() throws DatabaseException {
		Mockito.when(db.getOperationByKey(Mockito.anyInt()))
				.thenReturn(new Operation("A55", "INSTALL RUBBER IN POCKET",.0225,3,9693,0));
		assertEquals("A55", ((Operation) (opResource.getOperationByKey(9693).getEntity())).getOpCode());
		assertEquals(200, opResource.getOperationByKey(9693).getStatus());
	}

	@Test
	public void testOperationByKeyGet404() throws DatabaseException {
		Mockito.when(db.getOperationByKey(Mockito.anyInt())).thenThrow(new DataNotFoundException("Broken"));
		assertEquals(404, opResource.getOperationByKey(9693).getStatus());
	}

	@Test
	public void testOperationByKeyGet500() throws DatabaseException {
		Mockito.when(db.getOperationByKey(Mockito.anyInt())).thenThrow(new DatabaseConnectionException("Broken"));
		assertEquals(500, opResource.getOperationByKey(9693).getStatus());
	}

	@Test
	public void testOperationByKey503() throws DatabaseException {
		Mockito.when(db.getOperationByKey(Mockito.anyInt())).thenThrow(new UnknownDatabaseException("Broken"));
		assertEquals(503, opResource.getOperationByKey(9693).getStatus());
	}

	@Test
	public void testOperationsGoodGet() throws DatabaseException {
		Mockito.when(db.getOperations()).thenReturn(new ArrayList<Operation>());
		assertEquals(200, opResource.getOperations().getStatus());
	}

	@Test
	public void testOperationsGet500() throws DatabaseException {
		Mockito.when(db.getOperations()).thenThrow(new DatabaseConnectionException("Broken"));
		assertEquals(500, opResource.getOperations().getStatus());
	}

	@Test
	public void testOperationsGet503() throws DatabaseException {
		Mockito.when(db.getOperations()).thenThrow(new DatabaseException("Broken"));
		assertEquals(503, opResource.getOperations().getStatus());
	}
//
	@Test
	public void testOperationsGoodPut() throws DatabaseException {
		Mockito.when(db.updateOperation(Mockito.any(Operation.class)))
				.thenReturn(new Operation("A55", "INSTALL RUBBER IN POCKET",.0225,3,9693,0));
		assertEquals(200, opResource
				.updateOperation("A55", new Operation("A55", "INSTALL RUBBER IN POCKET",.0225,3,9693,0)).getStatus());
	}

	@Test
	public void testOperationsPut400() throws DatabaseException {
		assertEquals(400, opResource
				.updateOperation("AZZ", new Operation("A55", "INSTALL RUBBER IN POCKET",.0225,3,9693,0)).getStatus());
	}

	@Test
	public void testOperationsPut422() throws DatabaseException {
		Mockito.when(db.updateOperation(Mockito.any(Operation.class))).thenThrow(new DuplicateDataException("Broken"));
		assertEquals(422, opResource
				.updateOperation("A55", new Operation("A55", "INSTALL RUBBER IN POCKET",.0225,3,9693,0)).getStatus());
	}

	@Test
	public void testOperationsPut500() throws DatabaseException {
		Mockito.when(db.updateOperation(Mockito.any(Operation.class)))
				.thenThrow(new DatabaseConnectionException("Broken"));
		assertEquals(500, opResource
				.updateOperation("A55", new Operation("A55", "INSTALL RUBBER IN POCKET",.0225,3,9693,0)).getStatus());
	}
//
	@Test
	public void testOperationsPut503() throws DatabaseException {
		Mockito.when(db.updateOperation(Mockito.any(Operation.class))).thenThrow(new DatabaseException("Broken"));
		assertEquals(503, opResource
				.updateOperation("A55", new Operation("A55", "INSTALL RUBBER IN POCKET",.0225,3,9693,0)).getStatus());
	}

	//
	@Test
	public void testOperationsAdd201() throws DatabaseException {
		assertEquals(201,
				opResource.addOperation(new Operation("A55", "INSTALL RUBBER IN POCKET",.0225,3,9693,0)).getStatus());
	}

	@Test
	public void testOperationsAdd422() throws DatabaseException {
		Mockito.when(db.addOperation(Mockito.any(Operation.class))).thenThrow(new DuplicateDataException("Broken"));
		assertEquals(422,
				opResource.addOperation(new Operation("A55", "INSTALL RUBBER IN POCKET",.0225,3,9693,0)).getStatus());
	}

	@Test
	public void testOperationsAdd500() throws DatabaseException {
		Mockito.when(db.addOperation(Mockito.any(Operation.class))).thenThrow(new DatabaseConnectionException("Broken"));
		assertEquals(500,
				opResource.addOperation(new Operation("A55", "INSTALL RUBBER IN POCKET",.0225,3,9693,0)).getStatus());
	}

	@Test
	public void testOperationsAdd503() throws DatabaseException {
		Mockito.when(db.addOperation(Mockito.any(Operation.class))).thenThrow(new DatabaseException("Broken"));
		assertEquals(503,
				opResource.addOperation(new Operation("A55", "INSTALL RUBBER IN POCKET",.0225,3,9693,0)).getStatus());
	}
//	//
//
//	//
//	// @DELETE
//
	@Test
	public void testOperationsDel200() throws DatabaseException {
		assertEquals(200, opResource.deleteOperation("A55").getStatus());
	}

	@Test
	public void testOperationsDel404() throws DatabaseException {
		Mockito.doThrow(DataNotFoundException.class).when(db).deleteOperation(Mockito.anyString());
		assertEquals(404, opResource.deleteOperation("A55").getStatus());
	}

	@Test
	public void testOperationsDel500() throws DatabaseException {
		Mockito.doThrow(DatabaseConnectionException.class).when(db).deleteOperation(Mockito.anyString());
		assertEquals(500, opResource.deleteOperation("A55").getStatus());
	}

	@Test
	public void testOperationsDel503() throws DatabaseException {
		Mockito.doThrow(DatabaseException.class).when(db).deleteOperation(Mockito.anyString());
		assertEquals(503, opResource.deleteOperation("A55").getStatus());
	}
//	//
//	
	@Test
	public void testOperationsDelByKey200() throws DatabaseException {
		assertEquals(200, opResource.deleteOperationByKey(7777).getStatus());
	}

	@Test
	public void testOperationsDelByKey404() throws DatabaseException {
		Mockito.doThrow(DataNotFoundException.class).when(db).deleteOperationByKey(Mockito.anyInt());
		assertEquals(404, opResource.deleteOperationByKey(7777).getStatus());
	}

	@Test
	public void testOperationsDelByKey500() throws DatabaseException {
		Mockito.doThrow(DatabaseConnectionException.class).when(db).deleteOperationByKey(Mockito.anyInt());
		assertEquals(500, opResource.deleteOperationByKey(7777).getStatus());
	}

	@Test
	public void testOperationsDelByKey503() throws DatabaseException {
		Mockito.doThrow(DatabaseException.class).when(db).deleteOperationByKey(Mockito.anyInt());
		assertEquals(503, opResource.deleteOperationByKey(7777).getStatus());
	}
//	


}
