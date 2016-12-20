package com.itranswarp.bitcoin.util;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.junit.Test;

public class Secp256k1UtilsTest {

	static final Provider BC = new BouncyCastleProvider();

	PublicKey getPublicKeyFromBytes(BigInteger[] pubKey) throws Exception {
		ECParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
		KeyFactory kf = KeyFactory.getInstance("ECDSA", BC);
		ECPoint point = Secp256k1Utils.getCurve().createPoint(pubKey[0], pubKey[1]);
		ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(point, spec);
		return kf.generatePublic(pubKeySpec);
	}

	PrivateKey getPrivateKeyFromBytes(BigInteger priKey) throws Exception {
		ECParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
		KeyFactory kf = KeyFactory.getInstance("ECDSA", BC);
		ECPrivateKeySpec priKeySpec = new ECPrivateKeySpec(priKey, spec);
		return kf.generatePrivate(priKeySpec);
	}

	public byte[] sign(PrivateKey priKey, byte[] message) throws Exception {
		Signature sign = Signature.getInstance("SHA256withECDSA", BC);
		sign.initSign(priKey, new SecureRandom());
		sign.update(message);
		return sign.sign();
	}

	public boolean verifySignature(PublicKey pubKey, byte[] message, byte[] signature) throws Exception {
		Signature sign = Signature.getInstance("SHA256withECDSA", new BouncyCastleProvider());
		sign.initVerify(pubKey);
		sign.update(message);
		return sign.verify(signature);
	}

	@Test
	public void testGetN() throws Exception {
		// ECDSAKeyPair keyPair = ECDSAKeyPair.createNewKeyPair();
		// BigInteger priKey = keyPair.getPrivateKey();
		// BigInteger[] pubKey = keyPair.getPublicKey();
		// byte[] message = HashUtils.toBytes(
		// "54686973206973206a75737420736f6d6520706f696e746c6573732064756d6d7920737472696e672e205468616e6b7320616e7977617920666f722074616b696e67207468652074696d6520746f206465636f6465206974203b2d29");
		// byte[] signature = sign(getPrivateKeyFromBytes(priKey), message);
		// System.out.println(HashUtils.toHexString(signature));
		// System.out.println(verifySignature(getPublicKeyFromBytes(pubKey),
		// message, signature));

		// ECDSASigner signer = new ECDSASigner();
		// ByteArrayOutputStream buffer = new ByteArrayOutputStream ();
		// DERSequenceGenerator g=new DERSequenceGenerator(buffer);
		// g.addObject(null);
		// g.close();
	}

}
