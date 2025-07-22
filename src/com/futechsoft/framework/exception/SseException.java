package com.futechsoft.framework.exception;

public class SseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SseException() {
		super();
	}

	public SseException(String errorMessage) {
		super(errorMessage);
	}

	public SseException(String message, Throwable cause) {
		super(message, cause);
	}

}
