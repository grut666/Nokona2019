package com.nokona.exceptions;

public class DatabaseConnectionException extends DatabaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DatabaseConnectionException(String msg) {
		super(msg);
	}
	public DatabaseConnectionException(String msg, Exception ex) {
		super(msg, ex);
	}

}
