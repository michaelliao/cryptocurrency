package com.itranswarp.bitcoin.store;

public class StoreException extends RuntimeException {

	public StoreException() {
		super();
	}

	public StoreException(String message, Throwable cause) {
		super(message, cause);
	}

	public StoreException(String message) {
		super(message);
	}

	public StoreException(Throwable cause) {
		super(cause);
	}

}
