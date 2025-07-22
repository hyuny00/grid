package com.futechsoft.framework.exception;

public class BizException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BizException() {
		super();
	}

	public BizException(String errorMessage) {
		super(errorMessage);
	}

	public BizException(String message, Throwable cause) {
		super(message, cause);
	}

}