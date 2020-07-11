package com.nokona.reports.laborcodes;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.nokona.data.NokonaDatabase;
import com.nokona.db.NokonaDAO;
import com.nokona.exceptions.DatabaseConnectionException;

//import net.sf.jasperreports.engine.JREmptyDataSource;
//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JRExporter;
//import net.sf.jasperreports.engine.JasperCompileManager;
//import net.sf.jasperreports.engine.JasperExportManager;
//import net.sf.jasperreports.engine.JasperFillManager;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.JasperReport;
//import net.sf.jasperreports.engine.export.JRCsvExporter;
//import net.sf.jasperreports.engine.export.JRPdfExporter;
//import net.sf.jasperreports.engine.export.JRXlsExporter;
//import net.sf.jasperreports.engine.util.JRLoader;
//import net.sf.jasperreports.export.CsvExporterConfiguration;
//import net.sf.jasperreports.export.CsvReportConfiguration;
//import net.sf.jasperreports.export.Exporter;
//import net.sf.jasperreports.export.ExporterConfiguration;
//import net.sf.jasperreports.export.ExporterInput;
//import net.sf.jasperreports.export.ExporterOutput;
//import net.sf.jasperreports.export.OutputStreamExporterOutput;
//import net.sf.jasperreports.export.PdfExporterConfiguration;
//import net.sf.jasperreports.export.PdfReportConfiguration;
//import net.sf.jasperreports.export.ReportExportConfiguration;
//import net.sf.jasperreports.export.SimpleExporterInput;
//import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
//import net.sf.jasperreports.export.SimpleWriterExporterOutput;
//import net.sf.jasperreports.export.SimpleXlsExporterConfiguration;
//import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
//import net.sf.jasperreports.export.WriterExporterOutput;
//import net.sf.jasperreports.export.XlsExporterConfiguration;
//import net.sf.jasperreports.export.XlsReportConfiguration;

// Java Program To Call Jasper Report

public class ReportLaborCodes {
//	private static String DB_URL = "jdbc:mysql://localhost:3306/Nokona?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
//	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
//	private static String USER_NAME = "root";
//	private static String PASSWORD = "xyz";
//	private static Connection conn;
//
//	public static void main(String[] args) throws JRException, IOException {
//	
//		try {
//			NokonaDatabase db = new NokonaDAO();
//			conn = db.getConn();
//			JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile("JasperReports/LaborCodes.jasper");
//
//			Map<String, Object> parameters = new HashMap<String, Object>();
//			parameters.put("laborCode", 3);
////			parameters.put("net.sf.jasperreports.export.xls.exclude.origin.band.title", "title");
//
////			// Fill the Jasper Report
//			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
//			
////			jasperPrint.setProperty(propName, value);
////			// Creation of the HTML Jasper Reports
////			JasperExportManager.exportReportToPdfFile(jasperPrint, "MyJasperReport3.pdf");
//			Exporter<ExporterInput, PdfReportConfiguration, PdfExporterConfiguration, OutputStreamExporterOutput> pexporter = null;
//			pexporter = new JRPdfExporter();
//			pexporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//			pexporter.setExporterOutput(new SimpleOutputStreamExporterOutput("output.pdf"));
//			pexporter.exportReport();
//			
//			Exporter<ExporterInput, CsvReportConfiguration, CsvExporterConfiguration, WriterExporterOutput> exporter = null;
//			exporter = new JRCsvExporter();
//			jasperPrint.setProperty("net.sf.jasperreports.export.csv.exclude.origin.band.title", "title");
//			jasperPrint.setProperty("net.sf.jasperreports.export.csv.remove.empty.space.between.rows","true");
//			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//			exporter.setExporterOutput(new SimpleWriterExporterOutput("output.csv"));
//
//			exporter.exportReport();
//			System.out.println("Fini");
//		} catch (JRException e) {
//			e.printStackTrace();
//
//		} catch (DatabaseConnectionException e) {
//
//			e.printStackTrace();
//		} finally {
//			try {
//				conn.close();
//			} catch (SQLException e) {
//
//				e.printStackTrace();
//			}
//		}

//	}



}