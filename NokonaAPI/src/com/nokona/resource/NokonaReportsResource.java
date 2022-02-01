package com.nokona.resource;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
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
import javax.servlet.ServletContext;

import com.nokona.data.NokonaDatabase;
import com.nokona.enums.ReportCategory;
import com.nokona.exceptions.PDFException;
import com.nokona.qualifiers.BaseDaoQualifier;
import com.nokona.reports.OrderBy;
import com.nokona.reports.ReportProcesser;
import com.nokona.reports.ReportProperties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Path("/reports")
public class NokonaReportsResource {
	@Context
	private ServletContext context;
	private static final String PDF_DIRECTORY = "/tmp";
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
		
		return Response
				.ok(new ReportProperties(ReportCategory.EMPLOYEE, 
						parameters, "Dummy Report", "2022-01-01", "2022-01-10", ordersBy, "111", "222", "333", true, true))
				.build();

	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Produces("application/pdf")
	@Path("/pdf")
	public Response getPdfReport(ReportProperties properties) {
		new ReportProcesser(properties);
		File file = null;
		try {
			file = getJasperReport(properties);
			return Response.ok((Object)file)
					.header("Content-Disposition", "attachment; filename=" + file.getAbsolutePath()).build();

		} catch (PDFException e) {
			return Response.status(500).entity(e.getMessage()).build();
		}
	}
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Produces("application/pdf")
	@Path("/csv")
	public Response getCsvReport(ReportProperties properties) {

		File file = null;
		try {
			file = getJasperReport(properties);
			return Response.ok((Object)file)
					.header("Content-Disposition", "attachment; filename=\"" + file.getAbsolutePath()).build();

		} catch (PDFException e) {
			return Response.status(500).entity(e.getMessage()).build();
		}
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/pdftest")
	public Response getPdfReportTest() {
//		ReportProperties properties = new ReportProperties();
		return getPdfReport(null);
	}

	// @GET
	//// @Produces("application/octet-stream")
	// @Produces(MediaType.APPLICATION_JSON)
	// @Path("/csv")
	// public Response getCsvReport(ReportProperties properties) {
	//// File fileNew = new File("c:/codebase/Reports/output.csv");
	//// try {
	//// fileNew.createNewFile();
	//// } catch (IOException e) {
	//// return Response.ok(e.getMessage()).build();
	//// }
	//// File file = new File("c:/codebase/Reports/output.csv");
	//// return Response.ok(file).header("Content-Disposition", "attachment;
	// filename=\"c:/codebase/Reports/output.csv\"").build();
	// return Response.ok(new Operation("csv","Test",1.1,5,10,15 )).build();
	// }

//	private File testPDF() throws PDFException {
//
//		File dir = new File(PDF_DIRECTORY);
//		if (!dir.exists()) {
//			dir.mkdir();
//		}
//
//		File file;
//
//		try {
//			file = new File(pdfFile());
//		} catch (IOException e) {
//			throw new PDFException(e.getMessage());
//		}
//
//		return file;
//	}

//	private String pdfFile() throws IOException {
//		String fileName = PDF_DIRECTORY + "/" + generatePDFName();
//
//		PDDocument document = new PDDocument();
//
//		PDPage pdPage = new PDPage();
//		document.addPage(pdPage);
//		PDPageContentStream contentStream = new PDPageContentStream(document, pdPage);
//
//		// Begin the Content stream
//		contentStream.beginText();
//
//		// Setting the font to the Content stream
//		contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
//
//		// Setting the position for the line
//
//		contentStream.newLineAtOffset(1, 500);
//
//		String text = "This is the sample document.  Hello from Mark";
//
//		// Adding text in the form of string
//		contentStream.showText(text);
//
//		// Ending the content stream
//		contentStream.endText();
//
//		System.out.println("Content added");
//
//		// Closing the content stream
//		contentStream.close();
//		document.save(fileName);
//
//		document.close();
//
//		return fileName;
//	}

	private String generatePDFName() {
		// return "Nokona_" + UUID.randomUUID().toString() + ".pdf"; // This will be the
		// real file after test
		return "Nokona_" + UUID.randomUUID().toString() + ".pdf";
	}

	private File getJasperReport(ReportProperties properties) throws PDFException {
		String fileName = PDF_DIRECTORY + "/" + generatePDFName();
		// Check properties, etc. Figure out which template to use. Then, the below:
		File dir = new File(PDF_DIRECTORY);
		if (!dir.exists()) {
			dir.mkdir();
		}
		try {
//			String templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/LaborCodes.jrxml");
			String templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/EmployeesByName.jrxml");

			JasperReport jasperReport = JasperCompileManager.compileReport(templateFileName);

			System.out.println("JasperReport");
			Map<String, Object> parms = new HashMap<String, Object>();
			
			//  Practicing
			//	parms.put("FIRST_LETTER", "F");
				parms.put("ACTIVE1", 0);
				parms.put("ACTIVE2", 1);
		
			
			//  End Practice
			conn = db.getConn();
			System.out.println("Conn");
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parms, conn);
			System.out.println("JasperPrint");
			JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
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
		response.header("Content-Disposition",
				"attachment; filename=new-android-book.pdf");
		return response.build();

	}

}
