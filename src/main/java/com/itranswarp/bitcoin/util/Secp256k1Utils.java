package com.itranswarp.bitcoin.util;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class Secp256k1Utils {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	final static ECParameterSpec p = ECNamedCurveTable.getParameterSpec("secp256k1");
	final static ECCurve curve = p.getCurve();
	final static ECPoint G = p.getG();
	final static BigInteger N = p.getN();
	final static BigInteger H = p.getH();

	public static KeyPair generateKeyPair() throws GeneralSecurityException {
		ECGenParameterSpec ecGenSpec = new ECGenParameterSpec("prime192v1");
		KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "BC");
		g.initialize(ecGenSpec, new SecureRandom());
		return g.generateKeyPair();
	}

	public static ECPoint getPoint(BigInteger k) {
		return G.multiply(k.mod(p.getN()));
	}

	public static ECPoint getG() {
		return G;
	}

	public static BigInteger getN() {
		return N;
	}

	public static BigInteger getH() {
		return H;
	}

	public static int getFieldSize() {
		return curve.getFieldSize();
	}

	public static ECCurve getCurve() {
		return curve;
	}
}
