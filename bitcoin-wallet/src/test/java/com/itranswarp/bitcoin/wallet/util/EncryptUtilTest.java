package com.itranswarp.bitcoin.wallet.util;

import static org.junit.Assert.*;

import java.security.SecureRandom;
import java.util.UUID;

import org.junit.Test;

public class EncryptUtilTest {

	@Test
	public void testEncryptAndDecrypt() throws Exception {
		String password = "a-secret-key";
		byte[] message = "hello, encrypt with pbe-aes-256!!!".getBytes("UTF-8");
		byte[] salt = EncryptUtil.secureSalt();
		byte[] encrypted = EncryptUtil.encrypt(password.toCharArray(), salt, message);
		byte[] decrypted = EncryptUtil.decrypt(password.toCharArray(), salt, encrypted);
		assertArrayEquals(message, decrypted);
	}

	@Test
	public void testDecrypt() throws Exception {
		for (int i = 0; i < 100; i++) {
			String password = UUID.randomUUID().toString();
			byte[] message = SecureRandom.getInstanceStrong().generateSeed(1024);
			byte[] salt = EncryptUtil.secureSalt();
			byte[] encrypted = EncryptUtil.encrypt(password.toCharArray(), salt, message);
			byte[] decrypted = EncryptUtil.decrypt(password.toCharArray(), salt, encrypted);
			assertArrayEquals(message, decrypted);
		}
	}
}
