package com.itranswarp.bitcoin.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test data from:
 * 
 * https://en.bitcoin.it/wiki/Technical_background_of_version_1_Bitcoin_addresses
 * 
 * @author liaoxuefeng
 */
public class Secp256k1UtilsTest {

	@Test
	public void testUncompressedPublicKeyToAddress() {
		String hex = "0450863ad64a87ae8a2fe83c1af1a8403cb53f53e486d8511dad8a04887e5b23522cd470243453a299fa9e77237716103abc11a1df38855ed6f2ee187e9c582ba6";
		String address = Secp256k1Utils.uncompressedPublicKeyToAddress(HashUtils.toBytes(hex));
		assertEquals("16UwLL9Risc3QfPqBUvKofHmBQ7wMtjvM", address);
	}

	@Test
	public void testHash160PublicKeyToAddress() {
		String hex = "010966776006953d5567439e5e39f86a0d273bee";
		String address = Secp256k1Utils.hash160PublicKeyToAddress(HashUtils.toBytes(hex));
		assertEquals("16UwLL9Risc3QfPqBUvKofHmBQ7wMtjvM", address);
	}
}
