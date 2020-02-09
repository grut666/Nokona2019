package com.nokona.testing;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class TestMain {
	private Client client;
	private WebTarget target;
	public static void main(String[] args) {
		
		

		

	}
	@PostConstruct
	protected void init() {
	    client = ClientBuilder.newClient();
	    //query params: ?q=Turku&cnt=10&mode=json&units=metric
	    target = client.target("http://localhost:8080/Nokona/Employees");
//	       .queryParam("cnt", "10")
//	       .queryParam("mode", "json")
//	       .queryParam("units", "metric");
	}
}
