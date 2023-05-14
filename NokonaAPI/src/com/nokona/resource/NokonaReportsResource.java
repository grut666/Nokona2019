package com.nokona.resource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.servlet.ServletContext;

import com.nokona.data.NokonaDatabase;
import com.nokona.db.NokonaDAO;
import com.nokona.enums.ReportCategory;
import com.nokona.exceptions.DatabaseConnectionException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.PDFException;
import com.nokona.qualifiers.BaseDaoQualifier;
import com.nokona.reports.JobReports;
import com.nokona.reports.LaborReports;
import com.nokona.reports.ReportProperties;
import com.nokona.reports.TicketReports;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
//import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.export.CsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleCsvMetadataExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;

@Path("/reports")
@ApplicationScoped()
public class NokonaReportsResource extends NokonaDAO {
	@Context
	private ServletContext context;
	private static final String CSV_DIRECTORY = "/tmpCSV";
	private static final String HTML_DIRECTORY = "/tmpHTML";
	@Inject
	@BaseDaoQualifier
	private NokonaDatabase db;

	public NokonaReportsResource() throws DatabaseConnectionException {
		super();
	}
	public NokonaReportsResource(String userName, String password) throws DatabaseException {
		super(userName, password);

	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/pdf")
	@Path("/pdf")
	public Response getPdfReport(@DefaultValue("") @QueryParam("startDate") String startDate,
			@DefaultValue("") @QueryParam("endDate") String endDate,
			@DefaultValue("") @QueryParam("reportName") String reportName,
			@DefaultValue("") @QueryParam("reportCategory") String reportCategory,
			@DefaultValue("") @QueryParam("status") String status,
			@DefaultValue("") @QueryParam("category") String category,
			@DefaultValue("") @QueryParam("all") String all,
			@DefaultValue("") @QueryParam("csv") String csv,
			@DefaultValue("") @QueryParam("jobId") String jobId) {
		ReportProperties properties = new ReportProperties();

		properties.setParameters(new HashMap<String, String>());
		properties.setStartDate(startDate);
		properties.setEndDate(endDate);
		properties.setJobId(jobId);
		properties.setReportName(reportName);
		properties.setReportCategory(ReportCategory.valueOf(reportCategory));
		properties.getParameters().put("STATUS", status);
		properties.getParameters().put("CATEGORY", category);
		properties.getParameters().put("ALL", all);
		properties.setCsvNotHtml("True".equals(csv) ? true : false);
		System.out.println("*******" + properties);
		// Load properties with QueryParameter values"
		File file = null;
		try {
			file = getJasperReport(properties);
			if (file == null) {
				return Response.status(Status.BAD_REQUEST).build();
			}
			String returnString = "attachment; filename=" + file.getAbsolutePath();
			System.out.println(returnString);
			if (properties.isCsvNotHtml()) {
				return Response.ok("CSV file is " + file.getAbsoluteFile()).build();
			}
			return Response.ok((Object) file).header("Content-Disposition", returnString).build();
		} catch (PDFException e) {
			return Response.status(500).entity(e.getMessage()).build();
		}
	}

//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces("application/pdf")
//	@Path("/pdf")
//	public Response postPdfReport(ReportProperties properties) {
//		System.out.println("Job is*************** " + properties.getJobId());
//		File file = null;
//		try {
//			file = getJasperReport(properties);
//			if (file == null) {
//				return Response.status(Status.BAD_REQUEST).build();
//			}
//			String returnString = "attachment; filename=" + file.getAbsolutePath();
//			System.out.println(returnString);
//			if (properties.isCsvNotHtml()) {
//				return Response.ok("CSV file is " + file.getAbsoluteFile()).build();
//			}
//			return Response.ok((Object) file).header("Content-Disposition", returnString).build();
//		} catch (PDFException e) {
//			return Response.status(500).entity(e.getMessage()).build();
//		}
//	}

//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.TEXT_HTML)
//	@Path("/csv")
//	public Response getCsvReport(ReportProperties properties) {
//		System.out.println("**************************" + properties.getReportName());
//		File file = null;
//		try {
//			file = getJasperReport(properties);
//			if (file == null) {
//				return Response.status(Status.BAD_REQUEST).build();
//			}
//			return Response.ok((Object) file)
//					.header("Content-Disposition", "attachment; filename=\"" + file.getAbsolutePath()).build();
//
//		} catch (PDFException e) {
//			return Response.status(500).entity(e.getMessage()).build();
//		}
//	}

//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_OCTET_STREAM)
//	@Path("/pdftest")
//	public Response getPdfReportTest() {
//		return postPdfReport(null);
//	}

	private String generateHTMLName() {
		return "Nokona_" + UUID.randomUUID().toString() + ".html";
	}

	private String generateCSVName() {
		return "Nokona_" + UUID.randomUUID().toString() + ".csv";
	}

	private File getJasperReport(ReportProperties properties) throws PDFException {
		String fileName;
		File dir;
		boolean csvFormat = properties.isCsvNotHtml();
		System.out.println("******** csvFormat is " + csvFormat + "*******");
		if (csvFormat) {
			fileName = CSV_DIRECTORY + "/" + generateCSVName();
			dir = new File(CSV_DIRECTORY);
		} else {
			fileName = HTML_DIRECTORY + "/" + generateHTMLName();
			dir = new File(HTML_DIRECTORY);
		}
		// Check properties, etc. Figure out which template to use. Then, the below:
		if (!dir.exists()) {
			dir.mkdir();
		}
		String templateFileName;
		// try {
		ReportCategory rc = properties.getReportCategory();

		if (rc == null) {
			return null;
		}
		
		Map<String, Object> parms = new HashMap<String, Object>();
		if (csvFormat) {
			parms.put(JRParameter.IS_IGNORE_PAGINATION, true);
		}
		// String reportName = properties.getReportName();
		switch (rc.getCategory().toUpperCase()) {
		case "EMPLOYEE":
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/EmployeesByName.jrxml");
			break;
		case "JOB":
			templateFileName = JobReports.construct(context, properties, parms);
			break;
		case "LABOR":
				System.out.println("Labor************************");
			templateFileName = LaborReports.construct(context, properties, parms);
			System.out.println("templateFileName ************************ " + templateFileName);
			break;
		case "OPERATION":
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/EmployeesByName.jrxml");
			break;
		case "TICKET":
			templateFileName = TicketReports.construct(context, properties, parms);
			break;
		default:
			templateFileName = context.getRealPath("/WEB-INF/JasperTemplates/EmployeesByName.jrxml");
			break;
		}
		try  {
			// Connection conn = db.getConn();
			JasperReport jasperReport = JasperCompileManager.compileReport(templateFileName);

			System.out.println("JasperReport");

			// Practicing
			// parms.put("FIRST_LETTER", "F");
			// parms.put("ACTIVE1", 0);
			// parms.put("ACTIVE2", 1);
			// End Practice

			System.out.println("****** Report Resource Conn is: " + conn);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parms, conn);
			// JasperPrint jasperPrint =
			// (JasperPrint)JRLoader.loadObjectFromFile(templateFileName);
			System.out.println("JasperPrint after connection");
			if (csvFormat) {
				System.out.println("Starting CSV export");

				JRCsvExporter exporter = new JRCsvExporter(new SimpleJasperReportsContext());
				CsvExporterConfiguration configuration = new SimpleCsvMetadataExporterConfiguration();
				configuration.isWriteBOM();

				exporter.setConfiguration(configuration);

				exporter.setExporterOutput(new SimpleWriterExporterOutput(fileName));
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setConfiguration(configuration);

				exporter.exportReport();

				System.out.println("Finished with CSV export");
			} else {
				System.out.println("Starting HTML export");
				JasperExportManager.exportReportToHtmlFile(jasperPrint, fileName);
				System.out.println("Finished with HTML export");
			}

			return new File(fileName);

		} catch (JRException e) {
			System.out.println("JRException is " + e.getMessage());
			throw new PDFException(e.getMessage());
		} 
//		catch (SQLException e) {
//			throw new PDFException("SQLException is " + e.getMessage());
//		}

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

}
