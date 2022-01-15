package com.nokona.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtilities {


	public static Date convertSQLDateToUtilDate(java.sql.Date sqlDate) {
		
	    return sqlDate == null ? null : new java.util.Date(sqlDate.getTime());
	}
	public static java.sql.Date convertUtilDateToSQLDate(java.util.Date utilDate) {
	    return utilDate == null ? null : new java.sql.Date(utilDate.getTime());
	}
	public static java.sql.Date stringToSQLDate(String strDate) {
		DateFormat formatter = null;
        Date convertedDate = null;
        formatter =new SimpleDateFormat("yyyy-MM-dd");
        try {
			convertedDate =(Date) formatter.parse(strDate);
		} catch (ParseException e) {
			convertedDate = null;
		}
	    return convertUtilDateToSQLDate(convertedDate);
	}
	public static java.util.Date stringToJavaDate(String strDate) {
		DateFormat formatter = null;
        Date convertedDate = null;
        formatter =new SimpleDateFormat("yyyy-MM-dd");
        try {
			convertedDate =(Date) formatter.parse(strDate);
		} catch (ParseException e) {
			convertedDate = null;
		}
	    return convertedDate;
	}
}
