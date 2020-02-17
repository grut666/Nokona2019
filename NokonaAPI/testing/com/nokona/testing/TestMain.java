package com.nokona.testing;

import java.sql.SQLException;

import com.nokona.utilities.DateUtilities;

public class TestMain {
	
	public static void main(String[] args) {
		System.out.println(DateUtilities.stringToSQLDate("2020-02-16"));
		SQLException s = new SQLException("Hello");
		System.out.println(s.getMessage());
	}

}
