package com.nokona.exceptions;

public class InvalidQuantityException extends DatabaseException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public InvalidQuantityException(String msg) {
		super(msg);
	}
	public InvalidQuantityException(String msg, Exception ex) {
		super(msg, ex);
		
	}

}
