package com.itranswarp.bitcoin;

public class BitcoinException extends RuntimeException {

	public BitcoinException() {
	}

	public BitcoinException(String message, Throwable cause) {
		super(message, cause);
	}

	public BitcoinException(String message) {
		super(message);
	}

	public BitcoinException(Throwable cause) {
		super(cause);
	}

}
