package com.nokona.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/test")

public class NokonaTest {
	



	@GET
	@Produces(MediaType.APPLICATION_JSON)
	
	public Response getTest() {


		return Response.status(200).entity("Success").build();
	}

	

}
