package com.nokona.resource;

import java.io.File;
//import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
//import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.servlet.ServletContext;

import com.nokona.data.NokonaDatabase;
import com.nokona.enums.ReportCategory;
import com.nokona.exceptions.PDFException;
import com.nokona.qualifiers.BaseDaoQualifier;
import com.nokona.reports.OrderBy;
//import com.nokona.reports.ReportProcesser;
import com.nokona.reports.ReportProperties;

import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
//import net.sf.jasperreports.engine.export.ExporterFilter;
//import net.sf.jasperreports.engine.export.JRCsvExporter;
//import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
//import net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory;
//import net.sf.jasperreports.export.CsvReportConfiguration;
//import net.sf.jasperreports.export.SimpleCsvReportConfiguration;

@Path("/reports")
public class NokonaReportsResource {
	@Context
	private ServletContext context;
	private static final String PDF_DIRECTORY = "/tmpPDF";
	private static final String CSV_DIRECTORY = "/tmpCSV";
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
		Map<String, String> parameters = new HashMap<>();
		parameters.put("ACTIVE", "Y");

		return Response.ok(new ReportProperties(ReportCategory.EMPLOYEE, parameters, "Dummy Report", "2022-01-01",
				"2022-01-10", ordersBy, "111", "222", "333", true, true)).build();

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Produces("application/pdf")
	@Path("/pdf")
	public Response getPdfReport(ReportProperties properties) {
//		new ReportProcesser(properties);
		File file = null;
		try {
			file = getJasperReport(properties);
			if (file == null) {
				return Response.status(Status.BAD_REQUEST).build();
			}
			String returnString =  "attachment; filename=" + file.getAbsolutePath();
			System.out.println(returnString);
			return Response.ok((Object) file)
				//	.header("Content-Disposition", "attachment; filename=" + file.getAbsolutePath()).build();
					.header("Content-Disposition", returnString).build();
		} catch (PDFException e) {
			return Response.status(500).entity(e.getMessage()).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	@Path("/csv")
	public Response getCsvReport(ReportProperties properties) {
		System.out.println("**************************" + properties.getReportName());
		File file = null;
		try {
			file = getJasperReport(properties);
			if (file == null) {
				return Response.status(Status.BAD_REQUEST).build();
			}
			return Response.ok((Object) file)
					.header("Content-Disposition", "attachment; filename=\"" + file.getAbsolutePath()).build();

		} catch (PDFException e) {
			return Response.status(500).entity(e.getMessage()).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/pdftest")
	public Response getPdfReportTest() {
		// ReportProperties properties = new ReportProperties();
		return getPdfReport(null);
	}

	
	private String generatePDFName() {
		// return "Nokona_" + UUID.randomUUID().toString() + ".pdf"; // This will be the
		// real file after test
		return "Nokona_" + UUID.randomUUID().toString() + ".pdf";
	}

//	private String generateCSVName() {
//		// return "Nokona_" + UUID.randomUUID().toString() + ".pdf"; // This will be the
//		// real file after test
//		return "Nokona_" + UUID.randomUUID().toString() + ".csv";
//	}

	private File getJasperReport(ReportProperties properties) throws PDFException {
		String fileName;
		File dir;
		boolean pdfFormat = properties.isPdfNotExcel();
		if (pdfFormat) {
			fileName = PDF_DIRECTORY + "/" + generatePDFName();
			dir = new File(PDF_DIRECTORY);
		} else {
			fileName = CSV_DIRECTORY + "/" + generatePDFName();
			dir = new File(CSV_DIRECTORY);
		}
		// Check properties, etc. Figure out which template to use. Then, the below:
		if (!dir.exists()) {
			dir.mkdir();
		}
		String templateFileName;
		try {
			ReportCategory rc = properties.getCategory();
			if (rc == null) {
				return null;
			}

			switch (rc.getCategory().toUpperCase()) {
			case "EMPLOYEE":
				templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/EmployeesByName.jrxml");
				break;
			case "JOB":
				templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/EmployeesByName.jrxml");
				break;
			case "LABOR":
				templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/LaborCodes.jrxml");
				break;
			case "OPERATION":
				templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/EmployeesByName.jrxml");
				break;
			case "TICKET":
				templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/TicketWIP.jrxml");
				break;
			default:
				templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/EmployeesByName.jrxml");
				break;
			}

			JasperReport jasperReport = JasperCompileManager.compileReport(templateFileName);

			System.out.println("JasperReport");
			Map<String, Object> parms = new HashMap<String, Object>();

			// Practicing
			parms.put("FIRST_LETTER", "F");
			parms.put("ACTIVE1", 0);
			parms.put("ACTIVE2", 1);

			// End Practice
			conn = db.getConn();
			System.out.println("Conn");
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parms, conn);
			System.out.println("JasperPrint");
			if (pdfFormat) {
				JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
			} else {
				JasperExportManager.exportReportToHtmlFile(jasperPrint, fileName);
//				JRCsvExporter.
			}
			System.out.println("JasperExportManager");

		} catch (JRException e) {
			System.out.println(e.getMessage());
			throw new PDFException(e.getMessage());
		}
		return new File(fileName);
	}


	private static final String FILE_PATH = "c:\\codebase\\MyJasperReport.pdf";

	@GET
	@Path("/pdftest")
	@Produces("application/pdf")
	public Response getFile() {

		File file = new File(FILE_PATH);

		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition", "attachment; filename=new-android-book.pdf");
		return response.build();

	}
//	private void exportToCsv(JasperPrint jasperPrint, OutputStream os) throws JRException{
//	    JRCsvExporter  exporter = new JRCsvExporter();
//	    CsvReportConfiguration configuration = new SimpleCsvReportConfiguration();	
//	    exporter.setConfiguration(configuration);   
//	    exporter.exportReport();
//	}

}
