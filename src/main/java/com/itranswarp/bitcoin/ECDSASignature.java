package com.itranswarp.bitcoin;

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

import com.itranswarp.bitcoin.util.Secp256k1Utils;

public class ECDSASignature {

	static final Provider BC = new BouncyCastleProvider();

	public static byte[] sign(BigInteger priKey, byte[] message) throws Exception {
		Signature sign = Signature.getInstance("SHA256withECDSA", BC);
		sign.initSign(getPrivateKey(priKey), new SecureRandom());
		sign.update(message);
		return sign.sign();
	}

	public static boolean verifySignature(BigInteger[] pubKey, byte[] message, byte[] signature) throws Exception {
		Signature sign = Signature.getInstance("SHA256withECDSA", BC);
		sign.initVerify(getPublicKey(pubKey));
		sign.update(message);
		return sign.verify(signature);
	}

	static PublicKey getPublicKey(BigInteger[] pubKey) throws Exception {
		ECParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
		KeyFactory kf = KeyFactory.getInstance("ECDSA", BC);
		ECPoint point = Secp256k1Utils.getCurve().createPoint(pubKey[0], pubKey[1]);
		ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(point, spec);
		return kf.generatePublic(pubKeySpec);
	}

	static PrivateKey getPrivateKey(BigInteger priKey) throws Exception {
		ECParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
		KeyFactory kf = KeyFactory.getInstance("ECDSA", BC);
		ECPrivateKeySpec priKeySpec = new ECPrivateKeySpec(priKey, spec);
		return kf.generatePrivate(priKeySpec);
	}

}
