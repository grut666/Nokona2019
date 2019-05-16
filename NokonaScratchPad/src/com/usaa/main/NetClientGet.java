package com.usaa.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class NetClientGet {

	// http://localhost:8080/RESTfulExample/json/product/get
	public static void main(String[] args) {

		Runnable runnableTask = () -> {
			try {TimeUnit.MILLISECONDS.sleep(300);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
				}
			};
		
	  try {

		URL url = new URL("http://localhost:8080/Nokona/operations/A184");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		
		URL url2 = new URL("http://localhost:8080/Nokona/operations/B109");
		HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
		conn2.setRequestMethod("GET");
		conn2.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
		if (conn2.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn2.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(
				(conn2.getInputStream())));


//		System.out.println("Output from Server .... \n");
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		
		Callable<String> callableTask = () -> {
			System.out.println("Starting 1");
			String input;
			String ccResponse = "";
			while ((input = br.readLine()) != null) {
				ccResponse += input;
			}
			TimeUnit.MILLISECONDS.sleep(2000);
			//return ccResponse;
			return "YO";
		};
		Callable<String> callableTask2 = () -> {
			System.out.println("Starting 2");
			String input;
			String ccResponse = "";
			while ((input = br2.readLine()) != null) {
				ccResponse += input;
			}
			TimeUnit.MILLISECONDS.sleep(2000);
//			return ccResponse;
			return "YEE";

		};
		List<Callable<String>> callables = new ArrayList<>();
		callables.add(callableTask);
		callables.add(callableTask2);
		try {
			List<Future<String>> futures = executorService.invokeAll(callables);
			for(Future<String> future : futures) {
				if (future != null) {
					System.out.println( "Answer is: " + future.get());
				}
			}
		}
		catch(InterruptedException | ExecutionException e) {
			System.err.println("Executor error: " + e);
		}
		finally {
			executorService.shutdown();
		}
		conn.disconnect();

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }

	}

}