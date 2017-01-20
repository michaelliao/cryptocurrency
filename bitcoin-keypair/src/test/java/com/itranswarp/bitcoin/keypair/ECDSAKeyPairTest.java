package com.itranswarp.bitcoin.keypair;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import com.itranswarp.bitcoin.keypair.ECDSAKeyPair;
import com.itranswarp.bitcoin.util.HashUtils;

public class ECDSAKeyPairTest {

	@Test
	public void testGeneratePrivateKey() throws Exception {
		byte[] key = ECDSAKeyPair.generatePrivateKey();
		System.out.println("Key: " + HashUtils.toHexString(key));
		int n = key[0] & 0xff;
		assertTrue(n > 0 && n < 0xff);
		ECDSAKeyPair.checkPrivateKey(new BigInteger(1, key));
	}

	@Test
	public void testCreateKeyPairWithPrivateKey() {
		BigInteger privateKey = new BigInteger("e9873d79c6d87dc0fb6a5778633389f4453213303da61f20bd67fc233aa33262", 16);
		ECDSAKeyPair kp = ECDSAKeyPair.of(privateKey);
		assertEquals("5Kb8kLf9zgWQnogidDA76MzPL6TsZZY36hWXMssSzNydYXYB9KF", kp.getWalletImportFormat());
	}

	@Test
	public void testPrivateKeyToPublicKey() {
		BigInteger privateKey = new BigInteger("1e99423a4ed27608a15a2616a2b0e9e52ced330ac530edcc32c8ffc6a526aedd", 16);
		BigInteger[] publicKey = new BigInteger[] {
				new BigInteger("f028892bad7ed57d2fb57bf33081d5cfcf6f9ed3d3d7f159c2e2fff579dc341a", 16),
				new BigInteger("07cf33da18bd734c600b96a72bbc4749d5141c90ec8ac328ae52ddfe2e505bdb", 16) };
		ECDSAKeyPair kp = ECDSAKeyPair.of(privateKey);
		assertArrayEquals(publicKey, kp.getPublicKey());
	}

	@Test
	public void testGetWalletImportFormat() {
		BigInteger privateKey = new BigInteger("0c28fca386c7a227600b2fe50b7cae11ec86d3bf1fbe471be89827e19d72aa1d", 16);
		ECDSAKeyPair kp = ECDSAKeyPair.of(privateKey);
		assertEquals("5HueCGU8rMjxEXxiPuD5BDku4MkFqeZyd4dZ1jvhTVqvbTLvyTJ", kp.getWalletImportFormat());
	}

	@Test
	public void testCreateKeyPairWithWIF() {
		BigInteger privateKey = new BigInteger("0c28fca386c7a227600b2fe50b7cae11ec86d3bf1fbe471be89827e19d72aa1d", 16);
		String wif = "5HueCGU8rMjxEXxiPuD5BDku4MkFqeZyd4dZ1jvhTVqvbTLvyTJ";
		ECDSAKeyPair kp = ECDSAKeyPair.of(wif);
		assertEquals(privateKey, kp.getPrivateKey());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateKeyPairWithInvalidWIF() {
		String wif = "5HueCGU8rMjxEXxiPuD5BDku4MkFqeZyd4dZ1jvhTVqvbTLvyTj";
		ECDSAKeyPair.of(wif);
	}

	@Test
	public void testGetAddress() {
		BigInteger privateKey = new BigInteger("18e14a7b6a307f426a94f8114701e7c8e774e7f9a47e2c2035db29a206321725", 16);
		ECDSAKeyPair kp = ECDSAKeyPair.of(privateKey);
		assertEquals("16UwLL9Risc3QfPqBUvKofHmBQ7wMtjvM", kp.getAddress());
	}
}
