package com.nokona.resource;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nokona.data.NokonaDatabaseJob;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseConnectionException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.model.JobDetail;
import com.nokona.model.JobHeader;

@Path("/jobs")
public class NokonaJobResource {

	@Inject
	private NokonaDatabaseJob db;

	public NokonaJobResource() throws DatabaseException {

	}
	
//  HEADERS
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/jobheaders")
	public Response getJobHeaders() {
		try {
			return Response.status(200).entity(db.getJobHeaders()).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/jobheaders/{jobId}")
	public Response getJobHeadersByJobId(@PathParam("jobId") String jobId) {
		try {

			return Response.status(200).entity(db.getJobHeader(jobId)).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/jobheaders/bykey/{key}")
	public Response getJobHeadersByKey(@PathParam("key") long key ) {
		try {

			return Response.status(200).entity(db.getJobHeaderByKey(key)).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/jobheaders/{jobId}")
	public Response updateJobHeader(@PathParam("jobId") String jobId, JobHeader jobHeaderIn) {

		if (!jobId.equals(jobHeaderIn.getJobId())) {
			return Response.status(400).entity("{\"error\":\" Mismatch between body and URL\"}").build();
		}
		try {
			return Response.status(200).entity(db.updateJobHeader(jobHeaderIn)).build();
		} catch (DuplicateDataException e) {
			return Response.status(422).entity(e.getMessage()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)

	@Path("/jobheaders")
	public Response addJobHeader(JobHeader jobHeaderIn) {
		JobHeader jobHeader;
		try {
			jobHeader = db.addJobHeader(jobHeaderIn);
		} catch (DuplicateDataException e) {
			return Response.status(422).entity(e.getMessage()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		return Response.status(201).entity(jobHeader).build();
	}

// DETAILS	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/jobdetails/{jobid}")
	public Response getJobDetailsByJobId(@PathParam("jobid") String jobId) {
		try {

			return Response.status(200).entity(db.getJobDetails(jobId)).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}
	
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/jobdetails/{jobId}")

	public Response updateJobDetail(@PathParam("jobId") String jobId, JobDetail jobDetailIn) {

		if (!jobId.equals(jobDetailIn.getJobId())) {
			return Response.status(400).entity("{\"error\":\" Mismatch between body and URL\"}").build();
		}
		try {
			return Response.status(200).entity(db.updateJobDetails(jobDetailIn)).build();
		} catch (DuplicateDataException e) {
			return Response.status(422).entity(e.getMessage()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)

	@Path("/jobdetails")
	public Response addJobDetail(JobDetail jobDetailIn) {
		List<JobDetail> jobDetails;
		try {
			jobDetails = db.addJobDetails(jobDetailIn);
		} catch (DuplicateDataException e) {
			return Response.status(422).entity(e.getMessage()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		return Response.status(201).entity(jobDetails).build();
	}
	

// Job in its entirety
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{jobId}")
	public Response getJobByJobId(@PathParam("jobId") String jobId) {
		try {

			return Response.status(200).entity(db.getJob(jobId)).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}


	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{jobId}")
	public Response deleteJob(@PathParam("jobId") String jobId) {

		try {
			db.deleteJob(jobId);
			return Response.status(200).entity("{\"Success\":\"200\"}").build();
		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + jobId + " not found\"}").build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

	}
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bykey/{key}")
	public Response deleteJobByKey(@PathParam("key") Long key) {

		try {
			db.deleteJobByKey(key);
			return Response.status(200).entity("{\"Success\":\"200\"}").build();
		} catch (DataNotFoundException ex) {
			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}

	}

}
