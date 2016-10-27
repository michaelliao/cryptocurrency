package com.itranswarp.bitcoin.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class BytesUtilsTest {

	@Test
	public void testIsZeros() {
		assertTrue(BytesUtils.isZeros(new byte[] {}));
		assertTrue(BytesUtils.isZeros(new byte[] { 0 }));
		assertTrue(BytesUtils.isZeros(new byte[] { 0, 0, }));
		assertTrue(BytesUtils.isZeros(new byte[] { 0, 0, 0, 0 }));
		assertFalse(BytesUtils.isZeros(new byte[] { 1 }));
		assertFalse(BytesUtils.isZeros(new byte[] { 0, 0, 0, 1 }));
		assertFalse(BytesUtils.isZeros(new byte[] { 1, 0, 0, 0 }));
	}

}
