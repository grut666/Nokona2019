package com.nokona.testing.resources;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import com.nokona.exceptions.DatabaseException;
import com.nokona.reports.ReportProperties;
import com.nokona.resource.NokonaReportsResource;

class TestReportsResource {
//	@Mock
//	private NokonaDAOEmployee db;

//	@InjectMocks
	private NokonaReportsResource reportsResource;

//	@BeforeEach
//	private void setupBeforeEach() {
//		MockitoAnnotations.initMocks(this);
//	}

	@Test
	public void testReportGoodGet() throws DatabaseException {
		reportsResource = new NokonaReportsResource();
		
		assertTrue("MARY N.", ( (reportsResource.getPdfReport(new ReportProperties()).getEntity())).toString().endsWith(".pdf"));
	}

	

}
