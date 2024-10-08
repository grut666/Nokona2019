package com.nokona.resource;

import javax.enterprise.context.ApplicationScoped;
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

import org.apache.commons.lang3.StringUtils;

import com.nokona.data.NokonaDatabaseJob;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseConnectionException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.model.Job;
import com.nokona.model.JobDetail;
import com.nokona.model.JobHeader;


@Path("/jobs")
@ApplicationScoped
public class NokonaJobResource {

	@Inject
	private NokonaDatabaseJob db;

	public NokonaJobResource()  {
		super();
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
	@Path("/jobheaders/{jobId : .+}")
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
	@Path("/jobheaders/{jobId : .+}")
	public Response updateJobHeader(@PathParam("jobId") String jobId, JobHeader jobHeaderIn) {

		if (!jobId.equals(jobHeaderIn.getJobId())) {
			return Response.status(400).entity("{\"error\":\" Mismatch between body and URL\"}").build();
		}
		try {
			JobHeader jobHeader = db.updateJobHeader(jobHeaderIn);
//			TransferToAccess.transfer("JOBHEADER_U");
			return Response.status(200).entity(jobHeader).build();
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
//			TransferToAccess.transfer("JOBHEADER_C");
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
	@Path("/jobdetails/{jobId : .+}")
	public Response getJobDetailsByJobId(@PathParam("jobId") String jobId) {
		try {
			jobId = StringUtils.removeEnd(jobId, "-LH");
			jobId = StringUtils.removeEnd(jobId, "-RH");
			System.out.println("In Get Job Details Resource, jobId is " + jobId);
			System.out.println("******** JobId is " + jobId);
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
	@Path("/jobdetails/{jobId : .+}")

	public Response updateJobDetail(@PathParam("jobId") String jobId, JobDetail jobDetailIn) {

		if (!jobId.equals(jobDetailIn.getJobId())) {
			return Response.status(400).entity("{\"error\":\" Mismatch between body and URL\"}").build();
		}
		try {
			db.updateJobDetail(jobDetailIn);
//			TransferToAccess.transfer("JOBDETAIL_U");
			return Response.status(200).entity("{\"Success\":\"200\"}").build();
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

		try {
		  db.addJobDetail(jobDetailIn);
//		  TransferToAccess.transfer("JOBDETAIL_C");
		} catch (DuplicateDataException e) {
			return Response.status(422).entity(e.getMessage()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		return Response.status(201).entity("{\"Success\":\"200\"}").build();
	}
	

// Job in its entirety
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{jobId : .+}")
	public Response getJobByJobId(@PathParam("jobId") String jobId) {
		try {
			System.out.println("In Get Resource, jobId is " + jobId);

			return Response.status(200).entity(db.getJob(jobId)).build();
		} catch (DatabaseException ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (Exception ex) {
			return Response.status(404).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{jobId : .+}")
	public Response deleteJob(@PathParam("jobId") String jobId) {

		try {
			System.out.println("In Delete Resource, jobId is " + jobId);
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
//	@DELETE
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/bykey/{key}")
//	public Response deleteJobByKey(@PathParam("key") Long key) {
//
//		try {
//			db.deleteJobByKey(key);
//			return Response.status(200).entity("{\"Success\":\"200\"}").build();
//		} catch (DataNotFoundException ex) {
//			return Response.status(404).entity("{\"error\":\"" + key + " not found\"}").build();
//		} catch (DatabaseConnectionException ex) {
//			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		} catch (DatabaseException ex) {
//			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
//		}
//	}
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateJob(Job jobIn) {
		System.err.println("----------------------------" + jobIn + "-------------------------------");
		Job job;
		try {
			System.err.println("Incoming");
			System.err.println(jobIn.getHeader().getJobType());
//			System.err.println("Job Type is " + jobIn.getHeader().getJobType().getJobType());
			job = db.addJob(jobIn);

		} catch (DuplicateDataException e) {
			return Response.status(422).entity(e.getMessage()).build();
		} catch (DatabaseConnectionException ex) {
			return Response.status(500).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		} catch (DatabaseException ex) {
			return Response.status(503).entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
		}
		return Response.status(201).entity(job).build();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addJob(Job jobIn) {
		
		return updateJob(jobIn);
	}

}
