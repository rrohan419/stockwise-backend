package com.stockwise.common.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

	private final HttpStatus httpStatus;


    public CustomException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

    public CustomException(String message, Throwable cause) {
		super(message, cause);
		this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	}

    public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
