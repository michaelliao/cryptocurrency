package com.itranswarp.bitcoin.explorer.exception;

public class ApiNotFoundException extends ApiException {

	static final String ERROR_CODE = "not_found";

	public ApiNotFoundException() {
		super(ERROR_CODE);
	}

	public ApiNotFoundException(String message) {
		super(ERROR_CODE, message);
	}

}
