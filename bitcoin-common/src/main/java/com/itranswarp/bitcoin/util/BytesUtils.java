package com.itranswarp.bitcoin.util;

import org.bouncycastle.util.Arrays;

public class BytesUtils {

	/**
	 * Is byte array ALL zeros?
	 */
	public static boolean isZeros(byte[] bs) {
		for (byte b : bs) {
			if (b != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Join two byte arrays to a new byte array.
	 */
	public static byte[] concat(byte[] buf1, byte[] buf2) {
		byte[] buffer = new byte[buf1.length + buf2.length];
		int offset = 0;
		System.arraycopy(buf1, 0, buffer, offset, buf1.length);
		offset += buf1.length;
		System.arraycopy(buf2, 0, buffer, offset, buf2.length);
		return buffer;
	}

	/**
	 * Join three byte arrays to a new byte array.
	 */
	public static byte[] concat(byte[] buf1, byte[] buf2, byte[] buf3) {
		byte[] buffer = new byte[buf1.length + buf2.length + buf3.length];
		int offset = 0;
		System.arraycopy(buf1, 0, buffer, offset, buf1.length);
		offset += buf1.length;
		System.arraycopy(buf2, 0, buffer, offset, buf2.length);
		offset += buf2.length;
		System.arraycopy(buf3, 0, buffer, offset, buf3.length);
		return buffer;
	}

	/**
	 * Is array equals? (length equals and every byte equals)
	 */
	public static boolean equals(byte[] b1, byte[] b2) {
		if (b1 == null || b2 == null) {
			throw new IllegalArgumentException("one of the arguments is null");
		}
		if (b1.length != b2.length) {
			return false;
		}
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] != b2[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Reverse the byte array. Return new reversed array.
	 */
	public static byte[] reverse(byte[] msgHash) {
		return Arrays.reverse(msgHash);
	}

}
