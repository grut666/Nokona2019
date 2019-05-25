package com.usaa.main;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;

public class PrintMain {
	/**
	 * Starts the application.
	 */
	public static void main(String[] args) {

		// DocFlavor flavor = DocFlavor.;
		PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
		aset.add(MediaSizeName.ISO_A4);
		aset.add(new Copies(2));
		PrintService[] service = PrintServiceLookup.lookupPrintServices(null, aset);
		System.out.println(service.length);
	}

}
