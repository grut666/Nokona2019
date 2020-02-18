package com.nokona.testing;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;

import com.nokona.data.NokonaDatabaseTicket;
import com.nokona.db.NokonaDAOTicket;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.model.Employee;
import com.nokona.model.Labels;
import com.nokona.model.Ticket;
import com.nokona.utilities.BarCodeUtilities;

public class PrintTicketLabels {

	public static void main(String[] args) throws PrintException, IOException, DatabaseException, SQLException, NullInputDataException {
		Connection conn;
		NokonaDatabaseTicket db =  new NokonaDAOTicket("root", "xyz");
		conn = db.getConn();
		conn.setAutoCommit(false);
//		String defaultPrinter = PrintServiceLookup.lookupDefaultPrintService().getName();
//		System.out.println("Default printer: " + defaultPrinter);
		PrintService [] services = PrintServiceLookup.lookupPrintServices(null, null);
		PrintService barCodePrinter = null;
		for (PrintService service : services) {
			if (service.getName().contains("P3010")) {
				barCodePrinter = service;
				break;
			}
		}
	
//		Employee emp = new Employee(518, "FULKERSON", "DOUGLAS", 851,8,"FUL10", true);
		Ticket ticket = db.getTicketByKey(35965);// Get real ticketID
//		int pageQuantity = 1;
		Labels labels = new Labels();
		labels.setLabels(BarCodeUtilities.generateTicketLabels(ticket));

		String labelOutput = labels.getLabels();
		System.out.println(labelOutput);
//		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
//		pras.add(new Copies(1));

//		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
//		Doc doc = new SimpleDoc(labelOutput.getBytes(), flavor, null);
//		DocPrintJob job = barCodePrinter.createPrintJob();
//
//		PrintJobWatcher pjw = new PrintJobWatcher(job);
//		job.print(doc, pras);
//		pjw.waitForDone();

	}
}



