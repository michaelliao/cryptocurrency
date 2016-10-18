package com.itranswarp.cryptocurrency.common;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import com.itranswarp.cryptocurrency.common.Base58;

public class Base58Test {

	@Test
	public void testEncode() {
		byte[] data = new BigInteger("1b1234ff09091", 16).toByteArray();
		assertEquals("4ih2JerSC", Base58.encode(data));
	}

	@Test
	public void testDecode() {
		byte[] data = Base58.decode("4ih2JerSC");
		assertEquals(new BigInteger("1b1234ff09091", 16), new BigInteger(1, data));
	}

}
