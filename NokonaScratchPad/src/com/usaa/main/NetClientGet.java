package com.usaa.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.nokona.reports.ReportProperties;

public class NetClientGet {

	// http://localhost:8080/RESTfulExample/json/product/get
	public static void main(String[] args) {

		// Runnable runnableTask = () -> {
		// try {
		// TimeUnit.MILLISECONDS.sleep(300);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// };

		// try {
		// ExecutorService executorService = Executors.newFixedThreadPool(2);
		URL url;
		HttpURLConnection conn = null;

		try {
			url = new URL("http://localhost:8080/Nokona/reports");
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			Gson gson = new Gson();
			String content = gson.toJson(new ReportProperties());
			writer.write(content);
			writer.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer jsonString = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null) {
				jsonString.append(line);
			}
			br.close();
			conn.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// conn = (HttpURLConnection) url.openConnection();
		// Gson gson = new Gson();
		// String content = gson.toJson(new ReportProperties());
		// byte[] outputInBytes = content.getBytes("UTF-8");
		// OutputStream osr = conn.getOutputStream();
		// osr.write( outputInBytes );
		// osr.close();
		// System.out.println("After openConnection: " + conn.getResponseCode());
		// } catch (MalformedURLException e2) {
		// // TODO Auto-generated catch block
		// e2.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// try {
		// Thread.sleep(5000);
		// } catch (InterruptedException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// try {
		// for (List<String> list : conn.getHeaderFields().values()) {
		// for (String name : list) {
		// System.out.println(name);
		// }
		// }
		// System.out.println(conn.getHeaderFields());
		// conn.connect();
		// System.out.println("After Connect: " + conn.getResponseCode());
		//
		// conn.setRequestMethod("GET");
		// System.out.println("After GET: " + conn.getResponseCode());
		// conn.setRequestProperty("Content-Type", "application/JSON; charset=utf-8");
		//
		// conn.setRequestProperty("Accept", "application/octet-stream");
		// System.out.println(conn.getResponseCode());
		//
		// if (conn.getResponseCode() != 200) {
		// throw new RuntimeException("Failed : HTTP error code : " +
		// conn.getResponseCode());
		// }
		// byte[] buffer = new byte[4096];
		// System.out.println("Before getInputStream");
		// InputStream is = conn.getInputStream();
		// System.out.println("After getInputStream");
		// File file = File.createTempFile("tempfile", ".bin");
		// OutputStream os = new FileOutputStream(file);
		// System.out.println("Starting 1");
		// // String ccResponse = "";
		// int n;
		// while ((n = is.read(buffer)) != -1) {
		// os.write(buffer, 0, n);
		// }
		// os.close();
		// // TimeUnit.MILLISECONDS.sleep(2000);
		// // return ccResponse;
		// // Callable<String> callableTask = () -> {
		// // System.out.println("Starting 1");
		// // String ccResponse = "";
		// // int n;
		// // while ((n = is.read(buffer)) != -1) {
		// // os.write(buffer, 0, n);
		// // }
		// // os.close();
		// // TimeUnit.MILLISECONDS.sleep(2000);
		// // return ccResponse;
		// // return "YO";
		// // };
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// byte[] buffer2 = new byte[4096];
		// InputStream is2 = conn2.getInputStream();
		// File file2 = File.createTempFile("tempfile2", ".bin");
		// OutputStream os2 = new FileOutputStream(file2);
		// URL url2 = new URL("http://localhost:8080/Nokona/reports/pdf");
		// HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
		// try {
		// Thread.sleep(5000);
		// } catch (InterruptedException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// conn2.setRequestMethod("GET");
		// conn2.setRequestProperty("Accept", "application/octet-stream");
		// if (conn2.getResponseCode() != 200) {
		// throw new RuntimeException("Failed : HTTP error code : " +
		// conn2.getResponseCode());
		// }
		// Callable<String> callableTask2 = () -> {
		// System.out.println("Starting 2");
		// String ccResponse = "";
		// int n;
		// while ((n = is2.read(buffer2)) != -1)
		// {
		// os2.write(buffer2, 0, n);
		// }
		// os2.close();
		// TimeUnit.MILLISECONDS.sleep(2000);
		// return ccResponse;
		//
		// };

		// List<Callable<String>> callables = new ArrayList<>();
		// callables.add(callableTask);
		// // callables.add(callableTask2);
		// try {
		// List<Future<String>> futures = executorService.invokeAll(callables);
		// for (Future<String> future : futures) {
		// if (future != null) {
		// System.out.println("Answer is: " + future.get());
		// }
		// }
		// } catch (InterruptedException | ExecutionException e) {
		// System.err.println("Executor error: " + e);
		// } finally {
		// executorService.shutdown();
		// }
		// conn.disconnect();
		//
		// } catch (MalformedURLException e) {
		//
		// e.printStackTrace();
		//
		// } catch (IOException e) {
		//
		// e.printStackTrace();
		//
		// }
		//
		// }
		// }
	}
}