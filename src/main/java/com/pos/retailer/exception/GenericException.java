package com.pos.retailer.exception;

import java.io.Serializable;

public class GenericException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GenericException() {
		super();
	}

	public GenericException(String msg) {
		super(msg);
	}

	public GenericException(String msg, Exception e) {
		super(msg, e);
	}

}
