package com.nokona.testing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.sql.SQLException;
import java.util.Date;

import com.nokona.utilities.DateUtilities;

public class TestMain {
	
	public static void main(String[] args) {
		System.out.println(DateUtilities.stringToSQLDate("2020-02-16"));
		SQLException s = new SQLException("Hello");
		System.out.println(s.getMessage());

		try { FileWriter writer = new FileWriter("OUTPUT.TXT", true);
				BufferedWriter bw = new BufferedWriter(writer);
				
			bw.write("This is a test at " + new Date());
			bw.newLine();
			bw.close();
			writer.close();
			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
	}

}
