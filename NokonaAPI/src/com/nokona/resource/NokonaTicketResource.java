package com.nokona.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nokona.data.NokonaDatabaseTicket;
import com.nokona.dto.TicketDetailDtoIn;
import com.nokona.dto.TicketDetailDtoInRecord;
import com.nokona.dto.TicketDetailDtoOut;
import com.nokona.enums.OperationStatus;
import com.nokona.enums.TicketStatus;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseConnectionException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.model.Labels;
import com.nokona.model.Ticket;
import com.nokona.model.TicketDetail;
import com.nokona.model.TicketHeader;
import com.nokona.utilities.BarCodeUtilities;
import com.nokona.utilities.DateUtilities;
//import com.nokona.utilities.TransferToAccess;

@Path("/tickets")
@ApplicationScoped
public class NokonaTicketResource {

	@Inject
	private NokonaDatabaseTicket db;
	// private static SimpleDateFormat dateFormat = new
	// SimpleDateFormat("YYYY-mm-dd");

	public NokonaTicketResource() throws DatabaseException {

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{key}")
	public Response getTicket(@PathParam("key") int key) {

		Ticket ticket;

		try {
			ticket = db.getTicketByKey(key);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

		return Response.status(200).entity(ticket).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/byjob/{jobId}/{status}")
	public Response getTicketsByJob(@PathParam("jobId") String jobId, @PathParam("status") String status) {

		List<Ticket> tickets = new ArrayList<>();
		;

		try {
			tickets = db.getTicketsByJob(jobId, status);

		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

		return Response.status(200).entity(tickets).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response getTickets(@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("A") @QueryParam("status") String status) {
		try {

			if (!status.equals("A")) {
				if ("N".equalsIgnoreCase(status)) {
					status = "";
				}
				return Response.status(200).entity(db.getTicketsByStatus(status, offset)).build();
			}
			return Response.status(200).entity(db.getTickets(offset)).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/ticketheaders")
	public Response getTicketHeaders(@DefaultValue("0") @QueryParam("offset") int offset,
			@DefaultValue("A") @QueryParam("status") String status) {

		try {
			if (!status.equals("A")) {
				if ("N".equalsIgnoreCase(status)) {
					status = "";
				}
				return Response.status(200).entity(db.getTicketHeadersByStatus(status, offset)).build();
			}
			return Response.status(200).entity(db.getTicketHeaders(offset)).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/ticketdetails/{key}")
	public Response getTicketDetailByKey(@PathParam("key") long detailKey) {
		try {
			String stringKey = "" + detailKey;
			if (stringKey.length() < 3) {
				throw new DatabaseException("Invalid detail key.  Size is less than 3");
			}
			int header = Integer.parseInt(stringKey.substring(0, stringKey.length() - 2));
			System.out.println("Header in resource is " + header);
			TicketDetail ticketDetail = db.getTicketDetailByDetailKey(detailKey);
			TicketHeader ticketHeader = db.getTicketHeaderByKey(header);
			return Response.status(200).entity(new TicketDetailDtoOut(ticketHeader, ticketDetail)).build();

		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/ticketheaders/{key}")
	public Response getTicketHeaderByKey(@PathParam("key") long key) {

		TicketHeader ticketHeader;

		try {
			ticketHeader = db.getTicketHeaderByKey(key);

		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}
		// String year = ticketHeader.getDateCreated().toString();
		return Response.status(200).entity(ticketHeader).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/labels")
	public Response getTicketLabels(Ticket ticketIn) {
		// This will always send the labels to the printer
		System.out.println("In Ticket Resource getTicketLabels.  Key is " + ticketIn.getTicketHeader().getKey());
		Labels labels;
		try {
			labels = new Labels();
			labels.setLabels(BarCodeUtilities.generateTicketLabels(ticketIn));
			// Uncomment the next 1 line to get actual printing
			new NokonaLabelsResource().printLabels(labels);
			ticketIn.getTicketHeader().setTicketStatus(TicketStatus.PRINTED);
			System.out.println(
					"In resource.  Status is " + ticketIn.getTicketHeader().getTicketStatus().getTicketStatus());
			db.updateTicketHeader(ticketIn.getTicketHeader());
		} catch (Exception ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + ":" + db + "\"}").build();
		}

		return Response.status(200).entity(labels).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bykey/{key}")
	public Response deleteTicketByKey(@PathParam("key") int key) {

		try {
			db.deleteTicketByKey(key);
			// TransferToAccess.transfer("TICKETHEADER_D");
			// TransferToAccess.transfer("TICKETDETAIL_D");
			return Response.status(200).entity("{\"Success\":\"200\"}").build();
		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/")
	public Response addTicket(@QueryParam("action") String action, TicketHeader ticketHeader) {
		boolean yesPrint = false;
		if ("P".equalsIgnoreCase(action)) {
			yesPrint = true;
			ticketHeader.setTicketStatus(TicketStatus.PRINTED);
		}
		Ticket ticket;
		try {
			ticket = db.addTicket(ticketHeader);
			
			if (yesPrint) {
				getTicketLabels(ticket);
			}
			return Response.status(200).entity(ticket).build();
		} catch (DataNotFoundException ex) {
			return Response.status(404).entity(ex.getMessage()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/ticketheaders/{ticketkey}") // Probably just completion status for now
	public Response updateTicketHeader(@PathParam("ticketkey") long ticketKey, TicketHeader ticketHeader) {
		try {
//			System.out.println("TicketKey is " + ticketKey);
			System.out.println("TicketKey is " + ticketHeader.getKey());
			if (ticketKey != ticketHeader.getKey()) {
				return Response.status(400).entity("{\"error\":\" Mismatch between body and URL\"}").build();
			}
			db.updateTicketHeader(ticketHeader);
			return Response.status(200).entity("{\"Success\":\"200\"}").build();
		}

		catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/ticketdetails") // This is the only way to update ticketDetails, from the Scanning application
							// (for now, anyway)
	public Response updateTicketDetail(TicketDetailDtoIn ticketDetailDtoIn) {
		try {
			for (TicketDetailDtoInRecord dtoIn : ticketDetailDtoIn.getDetailRecords()) {
				System.out.println("Ticket Number:" + dtoIn.getTicketNumber());
				TicketDetail ticketDetail = db.getTicketDetailByDetailKey(dtoIn.getTicketNumber());
				if ("ZZZ".equals(ticketDetail.getOpCode())) { // Job has been scanned as complete
					TicketHeader th = db.getTicketHeaderByKey(ticketDetail.getKey());
					th.setTicketStatus(TicketStatus.COMPLETE);
					db.updateTicketHeader(th);
					continue;
				}
				System.out.println("***** Rate is " + dtoIn.getHourlyRateSAH() + "******");
				ticketDetail.setOperationStatus(OperationStatus.COMPLETE);
				ticketDetail.setEmployeeBarCodeID1(dtoIn.getBarCodeID1());
				ticketDetail.setActualQuantity1(dtoIn.getQuantity1());
				ticketDetail.setEmployeeBarCodeID2(dtoIn.getBarCodeID2());
				ticketDetail.setActualQuantity2(dtoIn.getQuantity2());
				ticketDetail.setHourlyRateSAH(dtoIn.getHourlyRateSAH());
				ticketDetail.setStatusDate(DateUtilities.stringToJavaDate(ticketDetailDtoIn.getDateOfTicket()));
				db.updateTicketDetail(ticketDetail);

			}
			return Response.status(200).entity("{\"Success\":\"200\"}").build();

		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + db + "\"}").build();
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/ticketdetails/testdto")
	public Response produceTestDto() {
		TicketDetailDtoInRecord record1 = new TicketDetailDtoInRecord(4098847, 10, 4690, 4098847, 0, 0.0);
		TicketDetailDtoInRecord record2 = new TicketDetailDtoInRecord(4098848, 10, 4690, 4098847, 0, 0.0);
		TicketDetailDtoInRecord record3 = new TicketDetailDtoInRecord(4098849, 10, 4690, 4098847, 0, 0.0);
		List<TicketDetailDtoInRecord> records = new ArrayList<>(Arrays.asList(record1, record2, record3));
		TicketDetailDtoIn dtoIn = new TicketDetailDtoIn("2021-01-23", records);

		return Response.status(200).entity(dtoIn).build();

	}

}