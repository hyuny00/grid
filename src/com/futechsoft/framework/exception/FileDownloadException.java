package com.futechsoft.framework.exception;

public class FileDownloadException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FileDownloadException() {
		super();
	}

	public FileDownloadException(String errorMessage) {
		super(errorMessage);
	}

	public FileDownloadException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileDownloadException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.getMessage(), cause);
	}

}
