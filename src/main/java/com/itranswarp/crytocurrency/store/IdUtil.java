package com.itranswarp.crytocurrency.store;

import java.util.concurrent.atomic.AtomicLong;

public class IdUtil {

	static final AtomicLong inc = new AtomicLong(0L);

	static final long MAX_INCREAMENT = 0xfffff + 1;

	/**
	 * Generate 16-char hex string.
	 * 
	 * @return 16-char string.
	 */
	public static String nextId() {
		long timestamp = System.currentTimeMillis();
		long increase = inc.incrementAndGet() % MAX_INCREAMENT;
		return String.format("%011x%05x", timestamp, increase);
	}
}
