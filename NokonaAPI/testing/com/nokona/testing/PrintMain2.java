package com.nokona.testing;

import java.io.IOException;

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
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

import com.nokona.exceptions.NullInputDataException;
import com.nokona.model.Employee;
import com.nokona.model.Labels;
import com.nokona.utilities.BarCodeUtilities;

public class PrintMain2 {

	public static void main(String[] args) throws PrintException, IOException, NullInputDataException {

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
	
		Employee emp = new Employee(518, "FULKERSON", "DOUGLAS", 851,8,"FUL10", true);
		Labels labels = new Labels();
		labels.setLabels(BarCodeUtilities.generateLabels(emp, 1));
		// prints the famous hello world! plus a form feed
//		InputStream is = new ByteArrayInputStream(labels.getLabels().getBytes("UTF8"));
		String labelOutput = labels.getLabels();
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		pras.add(new Copies(1));

		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		Doc doc = new SimpleDoc(labelOutput.getBytes(), flavor, null);
		DocPrintJob job = barCodePrinter.createPrintJob();

		PrintJobWatcher pjw = new PrintJobWatcher(job);
		job.print(doc, pras);
		pjw.waitForDone();
//		is.close();
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
