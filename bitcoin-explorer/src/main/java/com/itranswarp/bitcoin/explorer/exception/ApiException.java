package com.itranswarp.bitcoin.explorer.exception;

public class ApiException extends RuntimeException {

	private final String code;

	public String getCode() {
		return code;
	}

	public ApiException(String code) {
		this.code = code;
	}

	public ApiException(String code, String message) {
		super(message);
		this.code = code;
	}

	public ApiException(String code, Throwable cause) {
		super(cause);
		this.code = code;
	}

	public ApiException(String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}
}
