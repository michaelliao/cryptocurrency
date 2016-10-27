package com.itranswarp.bitcoin.util;

public class BytesUtils {

	public static boolean isZeros(byte[] bs) {
		for (byte b : bs) {
			if (b != 0) {
				return false;
			}
		}
		return true;
	}

	public static byte[] concat(byte[] buf1, byte[] buf2) {
		byte[] buffer = new byte[buf1.length + buf2.length];
		int offset = 0;
		System.arraycopy(buf1, 0, buffer, offset, buf1.length);
		offset += buf1.length;
		System.arraycopy(buf2, 0, buffer, offset, buf2.length);
		return buffer;
	}

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

}
