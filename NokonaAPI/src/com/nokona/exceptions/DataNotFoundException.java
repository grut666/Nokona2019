package com.nokona.exceptions;

public class DataNotFoundException extends DatabaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DataNotFoundException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}
	public DataNotFoundException(String msg, Exception ex) {
		super(msg, ex);
		// TODO Auto-generated constructor stub
	}

}
