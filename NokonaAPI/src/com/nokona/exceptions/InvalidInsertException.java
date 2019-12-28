package com.nokona.exceptions;

public class InvalidInsertException extends DatabaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public InvalidInsertException(String msg) {
		super(msg);
	}
	public InvalidInsertException(String msg, Exception ex) {
		super(msg, ex);
	}

}
