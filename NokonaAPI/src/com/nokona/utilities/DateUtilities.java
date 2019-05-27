package com.nokona.utilities;

import java.util.Date;

public class DateUtilities {


	public static Date convertSQLDateToUtilDate(java.sql.Date sqlDate) {
	    return new java.util.Date(sqlDate.getTime());
	}
}
