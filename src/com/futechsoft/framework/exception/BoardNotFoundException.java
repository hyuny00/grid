package com.futechsoft.framework.exception;

public class BoardNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BoardNotFoundException() {
		super();
	}

	public BoardNotFoundException(String errorMessage) {
		super(errorMessage);
	}

	public BoardNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}