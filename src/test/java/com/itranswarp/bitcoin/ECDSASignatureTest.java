package com.itranswarp.bitcoin;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.itranswarp.bitcoin.util.HashUtils;

public class ECDSASignatureTest {

	String[] messages = { "Hello, world", "Hahahahahahahahaha",
			"43a09ce25022100c4f62ce7d0e58d024ea61580faca05a16f9423e5e388b3076fb3d22b2d70dad7" };

	@Test
	public void testSignAndVerify() throws Exception {
		for (String message : messages) {
			ECDSAKeyPair keyPair = ECDSAKeyPair.createNewKeyPair();
			BigInteger priKey = keyPair.getPrivateKey();
			BigInteger[] pubKey = keyPair.getPublicKey();
			byte[] data = message.getBytes(StandardCharsets.UTF_8);
			for (int i = 0; i < 3; i++) {
				byte[] signature = ECDSASignature.sign(priKey, data);
				System.out.println(HashUtils.toHexString(signature));
				boolean verified = ECDSASignature.verifySignature(pubKey, data, signature);
				assertTrue(verified);
			}
		}
	}

}
