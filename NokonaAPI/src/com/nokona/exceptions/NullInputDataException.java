package com.nokona.exceptions;

public class NullInputDataException extends DatabaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public NullInputDataException(String msg) {
		super(msg);
	}
	public NullInputDataException(String msg, Exception ex) {
		super(msg, ex);
		
	}

}
