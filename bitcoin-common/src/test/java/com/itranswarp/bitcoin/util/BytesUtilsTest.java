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

	@Test
	public void testConcat() {
		assertArrayEquals(new byte[] {}, BytesUtils.concat(new byte[] {}, new byte[] {}));
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6, 7 },
				BytesUtils.concat(new byte[] {}, new byte[] { 1, 2, 3, 4, 5, 6, 7 }));
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6, 7 },
				BytesUtils.concat(new byte[] { 1, 2, 3 }, new byte[] { 4, 5, 6, 7 }));
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6, 7 },
				BytesUtils.concat(new byte[] {}, new byte[] { 1, 2, 3 }, new byte[] { 4, 5, 6, 7 }));
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6, 7 },
				BytesUtils.concat(new byte[] { 1, 2, 3 }, new byte[] { 4, 5, 6, 7 }, new byte[] {}));
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6, 7 },
				BytesUtils.concat(new byte[] { 1, 2, 3 }, new byte[] {}, new byte[] { 4, 5, 6, 7 }));
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6, 7 },
				BytesUtils.concat(new byte[] { 1, 2, 3 }, new byte[] { 4, 5 }, new byte[] { 6, 7 }));
	}
}
