package com.nokona.testing;

import static org.junit.Assert.assertEquals;

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
import com.nokona.exceptions.UnknownDatabaseException;
import com.nokona.resource.NokonaEmployeeResource;

class TestAllResources {
	@Mock
	private NokonaDAOEmployee db;

	@InjectMocks
	private NokonaEmployeeResource empResource;

	@BeforeEach
	private void setupBeforeEach()  {
		MockitoAnnotations.initMocks(this);
	}
	@Test
	public void testEmployee404() throws DatabaseException {
		Mockito.when(db.getEmployee(Mockito.anyString())).thenThrow(new DataNotFoundException("Broken"));
		assertEquals(404, empResource.getEmployee("aaa").getStatus());	
	}
	@Test
	public void testEmployee500() throws DatabaseException {
		Mockito.when(db.getEmployee(Mockito.anyString())).thenThrow(new DatabaseConnectionException("Broken"));
		assertEquals(500, empResource.getEmployee("aaa").getStatus());
	}
	@Test
	public void testEmployee503() throws DatabaseException {
		Mockito.when(db.getEmployee(Mockito.anyString())).thenThrow(new UnknownDatabaseException("Broken"));
		assertEquals(503, empResource.getEmployee("aaa").getStatus());
	}
}
