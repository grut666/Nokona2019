package com.nokona.resource;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/printers")
public class NokonaPrintersResource {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/barcodeprinter")
	public Response getBarCodePrinter() {

		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		PrintService barCodePrinter = null;
		for (PrintService service : services) {
			if (service.getName().contains("P3010")) {
				barCodePrinter = service;
				break;
			}
		}

		if (barCodePrinter == null) {

			return Response.status(404).entity("{\"error\":\"" + "Could Not Find Barcode Printer" + "\"}").build();
		}

		return Response.status(200).entity(barCodePrinter).build();
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response getAllPrinters() {

		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		

		if (services == null) {

			return Response.status(404).entity("{\"error\":\"" + "Could Not Find Any Printers" + "\"}").build();
		}

		return Response.status(200).entity(services).build();
	}
}
