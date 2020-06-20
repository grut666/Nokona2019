package com.nokona.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.nokona.data.NokonaDatabase;
import com.nokona.qualifiers.BaseDaoQualifier;
import com.nokona.reports.OrderBy;
import com.nokona.reports.ReportProcesser;
import com.nokona.reports.ReportProperties;

@Path("/reports")
public class NokonaReportsResource {

	@Inject
	@BaseDaoQualifier
	private NokonaDatabase db;
	private Connection conn;

	public NokonaReportsResource() {

		// conn = db.getConn();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEmptyReportProperties() {
		System.out.println(conn);
		conn = db.getConn();
		System.out.println(conn);
		conn = db.getConn();
		OrderBy orderBy = new OrderBy("JobId", true);
		OrderBy orderBy2 = new OrderBy("StatusDate", false);
		List<OrderBy> ordersBy = new ArrayList<OrderBy>();
		ordersBy.add(orderBy);
		ordersBy.add(orderBy2);
		return Response.ok(new ReportProperties("Dummy Report", new Date(), new Date(), ordersBy, true, "111", "222", "CSV"))
				.build();
	}

	@GET
//	@Produces("application/octet-stream")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/pdf")
	@Path("/pdf")
	public Response getPdfReport(ReportProperties properties) {

		File file = new File("c:/codebase/Reports/output.pdf");  // This would be the file created by the ReportProcesser
	    FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			return Response.status(500).entity(e.getMessage()).build();
		}
	    ResponseBuilder responseBuilder = javax.ws.rs.core.Response.ok((Object) fileInputStream);
	    responseBuilder.type("application/pdf");
	    responseBuilder.header("Content-Disposition", "filename=" + new ReportProcesser().generateReport(properties)); 
	    return responseBuilder.build();
	}
	

//	@GET
////	@Produces("application/octet-stream")
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/csv")
//	public Response getCsvReport(ReportProperties properties) {
////		File fileNew = new File("c:/codebase/Reports/output.csv");
////		try {
////			fileNew.createNewFile();
////		} catch (IOException e) {
////			return Response.ok(e.getMessage()).build();
////		}
////		File file = new File("c:/codebase/Reports/output.csv");
////		return Response.ok(file).header("Content-Disposition", "attachment; filename=\"c:/codebase/Reports/output.csv\"").build();
//		return Response.ok(new Operation("csv","Test",1.1,5,10,15 )).build();
//	}

}
