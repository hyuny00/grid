package com.futechsoft.framework.exception;

public class FileUploadException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FileUploadException() {
		super();
	}

	public FileUploadException(String errorMessage) {
		super(errorMessage);
	}

	public FileUploadException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileUploadException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.getMessage(), cause);
	}

}