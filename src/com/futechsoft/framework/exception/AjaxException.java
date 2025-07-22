package com.futechsoft.framework.exception;

public class AjaxException extends Exception {

	private static final long serialVersionUID = 1L;

	public AjaxException() {
		super();
	}

	public AjaxException(String errorMessage) {
		super(errorMessage);
	}

	public AjaxException(String message, Throwable cause) {
		super(message, cause);
	}

}