package com.nokona.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.nokona.exceptions.DataNotFoundException;

@Path("/hello")
public class NokonaResource {

	@GET
	@Path("/sayHello/{user}")
	public Response dispMessage(@PathParam("user") String msg) throws DataNotFoundException
	{
		
		String message = "Welcome " + msg + "!!!";

		return Response.status(200).entity(message).build();
	}
	

}
