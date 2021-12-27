package com.nokona.resource;

import javax.inject.Inject;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nokona.data.NokonaDatabaseTicket;
import com.nokona.enums.TicketStatus;
import com.nokona.exceptions.DatabaseException;
import com.nokona.model.Labels;
import com.nokona.model.Ticket;
import com.nokona.model.TicketHeader;
import com.nokona.utilities.BarCodeUtilities;

@Path("/labels")
public class NokonaLabelsResource {
	@Inject
		private NokonaDatabaseTicket db;
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response printLabels(Labels labels) {
		
		try {
			printIt(labels);
			long dbKey = fetchKey(labels);
			if (dbKey > 0) {
				Ticket ticket;
				try {
					ticket = db.getTicketByKey(dbKey);
				
				ticket.getTicketHeader().setTicketStatus(TicketStatus.PRINTED);
				db.updateTicket(ticket);
				} catch (DatabaseException e) {
					return Response.status(500).entity(e.getMessage()).build();
				}
			}
			return Response.status(200).entity("{\"Success\":\"" + "Success" + "\"}").build();
		} catch (PrintException e) {
			return Response.status(404).entity("{\"error\":\"" + "Could Not Find Barcode Printer" + "\"}").build();
		}
	}

	public void printIt(Labels labels) throws PrintException {

		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		pras.add(new Copies(1));

		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		Doc doc = new SimpleDoc(labels.getLabels().getBytes(), flavor, null);
		DocPrintJob job = BarCodeUtilities.getBarCodePrinter().createPrintJob();

		PrintJobWatcher pjw = new PrintJobWatcher(job);
		job.print(doc, pras);
		pjw.waitForDone();
	}
	private long fetchKey(Labels labels) {
		
		return 0;
	}
}

class PrintJobWatcher {
	boolean done = false;

	PrintJobWatcher(DocPrintJob job) {
		job.addPrintJobListener(new PrintJobAdapter() {
			public void printJobCanceled(PrintJobEvent pje) {
				allDone();
			}

			public void printJobCompleted(PrintJobEvent pje) {
				allDone();
			}

			public void printJobFailed(PrintJobEvent pje) {
				allDone();
			}

			public void printJobNoMoreEvents(PrintJobEvent pje) {
				allDone();
			}

			void allDone() {
				synchronized (PrintJobWatcher.this) {
					done = true;
					System.out.println("Printing done ...");
					PrintJobWatcher.this.notify();
				}
			}
		});
	}

	public synchronized void waitForDone() {
		try {
			while (!done) {
				wait();
			}
		} catch (InterruptedException e) {
		}
	}
}
