package com.nokona.exceptions;

public class DatabaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DatabaseException(String msg) {
		super(msg);
	}
	public DatabaseException(String msg, Exception ex) {
		super(msg, ex);
	}
}
