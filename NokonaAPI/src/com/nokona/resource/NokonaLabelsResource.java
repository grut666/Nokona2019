package com.nokona.resource;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nokona.model.Labels;
import com.nokona.printer.PrintJobWatcher;
import com.nokona.utilities.BarCodeUtilities;

@Path("/labels")
public class NokonaLabelsResource {
//	@Inject
//		private NokonaDatabaseTicket db;
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response printLabels(Labels labels) {
		System.out.println("******** Entering printLabels ***********************");
		try {
			printIt(labels);
			return Response.status(200).entity("{\"Success\":\"" + "Success" + "\"}").build();
		} 
		catch (PrintException e) {
			return Response.status(404).entity("{\"error\":\"" + "Could Not Find Barcode Printer" + "\"}").build();
		}
	}

	protected void printIt(Labels labels) throws PrintException {
		System.out.println("******** Entering printIt ***********************");
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		pras.add(new Copies(1));
		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		Doc doc = new SimpleDoc(labels.getLabels().getBytes(), flavor, null);
		PrintService printService = BarCodeUtilities.getBarCodePrinter();
		if (printService == null) {
			return;
		}
		DocPrintJob job = printService.createPrintJob();

		PrintJobWatcher pjw = new PrintJobWatcher(job);
		job.print(doc, pras);
		System.out.println("******************Before pjw.waitForDone");
		pjw.waitForDone();
		System.out.println("******************After pjw.waitForDone");
		
	}
}

