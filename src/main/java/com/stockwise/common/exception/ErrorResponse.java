package com.stockwise.common.exception;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;

	private String path;

	private Integer status;

	private String message;

	private String requestId;
}
