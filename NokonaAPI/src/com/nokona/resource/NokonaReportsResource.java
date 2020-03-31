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
import com.nokona.reports.OrderBy;
import com.nokona.reports.ReportProperties;

@Path("/reports")
public class NokonaReportsResource {
	@Inject
	private NokonaDatabase db;
	private Connection conn;

	public NokonaReportsResource() {
		conn = db.getConn();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEmptyReportProperties() {

		return Response
				.ok(new ReportProperties(new Date(), new Date(), new ArrayList<OrderBy>(), true, true, "111", "222"))
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
