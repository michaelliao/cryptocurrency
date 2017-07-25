package com.itranswarp.bitcoin.wallet;

import static org.junit.Assert.*;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.itranswarp.bitcoin.keypair.ECDSAKeyPair;

public class SecretKeyManagerTest {

	@Test
	public void testLoad() {
		char[] password = "random-pass".toCharArray();
		String file = "tmp.wallet.json";
		remove(file);
		SecretKeyManager manager = new SecretKeyManager(password, file);
		// generate 3 keys:
		List<BigInteger> keys = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ECDSAKeyPair kp = ECDSAKeyPair.createNewKeyPair();
			keys.add(kp.getPrivateKey());
			String wif = kp.toUncompressedWIF();
			manager.importKey("name-" + i, wif);
		}
		// try open wallet:
		SecretKeyManager m2 = new SecretKeyManager(password, file);
		// get 3 keys:
		List<SecretKey> keys2 = m2.getKeys();
		assertEquals(keys2.size(), keys.size());
		for (int i = 0; i < keys.size(); i++) {
			assertEquals("name-" + i, keys2.get(i).label);
			BigInteger key = keys.get(i);
			BigInteger key2 = new BigInteger(1, keys2.get(i).key);
			assertEquals(key2, key);
		}
	}

	void remove(String file) {
		File f = new File(file);
		if (f.isFile()) {
			f.delete();
		}
	}

}
