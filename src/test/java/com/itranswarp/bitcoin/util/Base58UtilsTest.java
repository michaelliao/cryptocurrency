package com.itranswarp.bitcoin.util;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import com.itranswarp.bitcoin.util.Base58Utils;

public class Base58UtilsTest {

	@Test
	public void testEncode() {
		byte[] data = new BigInteger("1b1234ff09091", 16).toByteArray();
		assertEquals("4ih2JerSC", Base58Utils.encode(data));
	}

	@Test
	public void testDecode() {
		byte[] data = Base58Utils.decode("4ih2JerSC");
		assertEquals(new BigInteger("1b1234ff09091", 16), new BigInteger(1, data));
	}

}
