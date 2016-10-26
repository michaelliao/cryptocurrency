package com.itranswarp.bitcoin.util;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.itranswarp.bitcoin.util.HashUtils;

/**
 * Test data from: https://en.bitcoin.it/wiki/Protocol_documentation#Hashes
 */
public class HashUtilsTest {

	@Test
	public void testSha256() {
		assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
				HashUtils.toHexString(HashUtils.sha256("".getBytes(StandardCharsets.UTF_8))));
		assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
				HashUtils.toHexString(HashUtils.sha256("hello".getBytes(StandardCharsets.UTF_8))));
	}

	@Test
	public void testDoubleSha256() {
		assertEquals("9595c9df90075148eb06860365df33584b75bff782a510c6cd4883a419833d50",
				HashUtils.toHexString(HashUtils.doubleSha256("hello".getBytes(StandardCharsets.UTF_8))));
	}

	@Test
	public void testSha256AndRipeMD160() {
		assertEquals("b6a9c8c230722b7c748331a8b450f05566dc7d0f",
				HashUtils.toHexString(HashUtils.ripeMd160(HashUtils.sha256("hello".getBytes(StandardCharsets.UTF_8)))));
	}

	@Test
	public void testHexString() {
		String s = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
		byte[] data = HashUtils.toBytes(s);
		assertEquals(s, HashUtils.toHexString(data));
	}

	@Test
	public void testHexStringAsLittleEndian() {
		String s = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
		byte[] data = HashUtils.toBytesAsLittleEndian(s);
		assertEquals(s, HashUtils.toHexStringAsLittleEndian(data));
	}
}
