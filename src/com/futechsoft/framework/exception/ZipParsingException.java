package com.futechsoft.framework.exception;

public class ZipParsingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ZipParsingException(String message) {
		super(message);
	}

	public ZipParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ZipParsingException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.getMessage(), cause);
	}

}
