package com.nokona.exceptions;

public class DuplicateDataException extends DatabaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DuplicateDataException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}
	public DuplicateDataException(String msg, Exception ex) {
		super(msg, ex);
		// TODO Auto-generated constructor stub
	}

}
