package com.nokona.utilities;

import java.util.Date;

public class DateUtilities {


	public static Date convertSQLDateToUtilDate(java.sql.Date sqlDate) {
	    return new java.util.Date(sqlDate.getTime());
	}
	public static java.sql.Date convertUtilDateToSQLDate(java.util.Date utilDate) {
	    return new java.sql.Date(utilDate.getTime());
	}
}
