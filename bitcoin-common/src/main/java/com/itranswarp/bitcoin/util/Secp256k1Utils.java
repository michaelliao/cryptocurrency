package com.itranswarp.bitcoin.util;

import java.math.BigInteger;
import java.security.Provider;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

public class Secp256k1Utils {

	public static final Provider BC = new BouncyCastleProvider();

	final static ECParameterSpec p = ECNamedCurveTable.getParameterSpec("secp256k1");
	final static ECCurve curve = p.getCurve();
	final static ECPoint G = p.getG();
	final static BigInteger N = p.getN();
	final static BigInteger H = p.getH();

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

	public static String publicKeyToAddress(byte[] uncompressed) {
		if (uncompressed.length != 65) {
			throw new IllegalArgumentException(
					"bad length of uncompressed bytes: expect 65 but actual " + uncompressed.length);
		}
		byte[] hash = HashUtils.ripeMd160(HashUtils.sha256(uncompressed));
		byte[] hashWithNetworkId = BytesUtils.concat(NETWORK_ID_ARRAY, hash);
		byte[] checksum = HashUtils.doubleSha256(hashWithNetworkId);
		byte[] address = BytesUtils.concat(hashWithNetworkId, Arrays.copyOfRange(checksum, 0, 4));
		return Base58Utils.encode(address);
	}

	static final byte NETWORK_ID = 0x00;
	static final byte[] NETWORK_ID_ARRAY = { NETWORK_ID };

}
