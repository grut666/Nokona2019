package com.nokona.exceptions;

public class UnknownDatabaseException extends DatabaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public UnknownDatabaseException(String msg) {
		super(msg);
	}
	public UnknownDatabaseException(String msg, Exception ex) {
		super(msg, ex);
	}

}
