package com.nokona.exceptions;

public class PDFException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public PDFException(String msg) {
		super(msg);
	}
	public PDFException(String msg, Exception ex) {
		super(msg, ex);
	}
}
