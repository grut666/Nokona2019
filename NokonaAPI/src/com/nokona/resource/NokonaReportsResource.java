package com.nokona.resource;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import com.nokona.data.NokonaDatabase;
import com.nokona.db.NokonaDAO;
import com.nokona.exceptions.DatabaseConnectionException;
import com.nokona.qualifiers.BaseDaoQualifier;
import com.nokona.reports.OrderBy;
import com.nokona.reports.ReportProperties;

@Path("/reports")
public class NokonaReportsResource {

	private NokonaDatabase db;
	private Connection conn;

	public NokonaReportsResource() {
		if (db == null) {
			try {
				db = new NokonaDAO();
			} catch (DatabaseConnectionException e) {
				e.printStackTrace();
			}
		}
		conn = db.getConn();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEmptyReportProperties() {
		OrderBy orderBy = new OrderBy("JobId", true);
		OrderBy orderBy2 = new OrderBy("StatusDate", false);
		List<OrderBy> ordersBy = new ArrayList<OrderBy>();
		ordersBy.add(orderBy);
		ordersBy.add(orderBy2); 
		return Response
				.ok(new ReportProperties(new Date(), new Date(), ordersBy, true, true, "111", "222"))
				.build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{reportName}")
	public Response getReport(@PathParam("reportName") String reportName, ReportProperties properties) {
		if (properties.isPdfNotExcel()) {
			return getByPdf(reportName, properties);
		} else {
			return getByCsv(reportName, properties);
		}

	}
	protected Response getByPdf(String reportName, ReportProperties properties) {
		return Response.ok(properties).build();
	}
	protected Response getByCsv(String reportName, ReportProperties properties) {
		return Response.ok(properties).build();
	}
}
