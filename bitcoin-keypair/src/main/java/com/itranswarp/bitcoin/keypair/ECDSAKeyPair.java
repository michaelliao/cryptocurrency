package com.itranswarp.bitcoin.keypair;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

import com.itranswarp.bitcoin.util.Base58Utils;
import com.itranswarp.bitcoin.util.BytesUtils;
import com.itranswarp.bitcoin.util.HashUtils;
import com.itranswarp.bitcoin.util.Secp256k1Utils;

/**
 * The ECDSAKeyPair which contains a private key. Public key can be calculated
 * by private key.
 * 
 * @author Michael Liao
 */
public class ECDSAKeyPair {

	private final BigInteger privateKey;

	// public key can be cacluated by private key:
	private BigInteger[] publicKey = null;

	/**
	 * Construct a keypair with private key.
	 */
	private ECDSAKeyPair(BigInteger privateKey) {
		this.privateKey = privateKey;
	}

	/**
	 * Create KeyPair with specified WIF string.
	 */
	public static ECDSAKeyPair of(String wif) {
		byte[] key = parseWIF(wif);
		return of(key);
	}

	/**
	 * Create KeyPair with specified private key.
	 */
	public static ECDSAKeyPair of(byte[] privateKey) {
		return of(new BigInteger(1, privateKey));
	}

	/**
	 * Create KeyPair with specified private key.
	 */
	public static ECDSAKeyPair of(BigInteger privateKey) {
		checkPrivateKey(privateKey);
		return new ECDSAKeyPair(privateKey);
	}

	/**
	 * Create a new KeyPair with secure random private key.
	 */
	public static ECDSAKeyPair createNewKeyPair() {
		return of(generatePrivateKey());
	}

	/**
	 * Get private key as BigInteger.
	 */
	public BigInteger getPrivateKey() {
		return this.privateKey;
	}

	/**
	 * Convert to java.security.PrivateKey.
	 */
	public PrivateKey toPrivateKey() {
		try {
			ECParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
			KeyFactory kf = KeyFactory.getInstance("ECDSA", Secp256k1Utils.BC);
			ECPrivateKeySpec priKeySpec = new ECPrivateKeySpec(getPrivateKey(), spec);
			return kf.generatePrivate(priKeySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Convert to java.security.PublicKey.
	 */
	public PublicKey toPublicKey() {
		return toPublicKey(getPublicKey());
	}

	/**
	 * Convert to java.security.PublicKey.
	 */
	public static PublicKey toPublicKey(BigInteger[] pubKey) {
		try {
			ECParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
			KeyFactory kf = KeyFactory.getInstance("ECDSA", Secp256k1Utils.BC);
			ECPoint point = Secp256k1Utils.getCurve().createPoint(pubKey[0], pubKey[1]);
			ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(point, spec);
			return kf.generatePublic(pubKeySpec);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create signature using current private key.
	 */
	public byte[] createSignature(byte[] message) {
		try {
			Signature sign = Signature.getInstance("SHA256withECDSA", Secp256k1Utils.BC);
			sign.initSign(toPrivateKey(), new SecureRandom());
			sign.update(message);
			return sign.sign();
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Verify signature.
	 */
	public static boolean verifySignature(BigInteger[] pubKey, byte[] message, byte[] signature) {
		try {
			Signature sign = Signature.getInstance("SHA256withECDSA", Secp256k1Utils.BC);
			sign.initVerify(toPublicKey(pubKey));
			sign.update(message);
			return sign.verify(signature);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Verify signature.
	 */
	public boolean verifySignature(byte[] message, byte[] signature) {
		return verifySignature(getPublicKey(), message, signature);
	}

	/**
	 * Get public key as BigInteger[] with 2 elements.
	 */
	public BigInteger[] getPublicKey() {
		if (this.publicKey == null) {
			ECPoint point = Secp256k1Utils.getG().multiply(privateKey);
			ECPoint normed = point.normalize();
			byte[] x = normed.getXCoord().getEncoded();
			byte[] y = normed.getYCoord().getEncoded();
			this.publicKey = new BigInteger[] { new BigInteger(1, x), new BigInteger(1, y) };
		}
		return this.publicKey;
	}

	/**
	 * Get version 1 of BitCoin address (hash of public key):
	 * https://en.bitcoin.it/wiki/Technical_background_of_version_1_Bitcoin_addresses
	 */
	public String getAddress() {
		BigInteger[] keys = getPublicKey();
		byte[] xs = bigIntegerToBytes(keys[0], 32);
		byte[] ys = bigIntegerToBytes(keys[1], 32);
		byte[] uncompressed = BytesUtils.concat(PUBLIC_KEY_PREFIX_ARRAY, xs, ys);
		return Secp256k1Utils.publicKeyToAddress(uncompressed);
	}

	/**
	 * Get Wallet Import Format string defined in:
	 * https://en.bitcoin.it/wiki/Wallet_import_format
	 */
	public String getWalletImportFormat() {
		byte[] key = bigIntegerToBytes(this.privateKey, 32);
		byte[] extendedKey = BytesUtils.concat(PRIVATE_KEY_PREFIX_ARRAY, key);
		byte[] hash = HashUtils.doubleSha256(extendedKey);
		byte[] checksum = Arrays.copyOfRange(hash, 0, 4);
		byte[] extendedKeyWithChecksum = BytesUtils.concat(extendedKey, checksum);
		return Base58Utils.encode(extendedKeyWithChecksum);
	}

	static byte[] parseWIF(String wif) {
		byte[] data = Base58Utils.decodeChecked(wif);
		if (data[0] != PRIVATE_KEY_PREFIX) {
			throw new IllegalArgumentException("Leading byte is not 0x80.");
		}
		// remove first 0x80:
		return Arrays.copyOfRange(data, 1, data.length);
	}

	// generate random private key between 0x00ffff... ~ 0xff0000...
	static byte[] generatePrivateKey() {
		byte[] hash = null;
		int first;
		SecureRandom sr;
		try {
			sr = SecureRandom.getInstanceStrong();
		} catch (NoSuchAlgorithmException e) {
			sr = new SecureRandom();
		}
		do {
			byte[] rnd = new byte[200];
			sr.nextBytes(rnd);
			hash = HashUtils.doubleSha256(rnd);
			first = hash[0] & 0xff;
		} while (first == 0x00 || first == 0xff);
		return hash;
	}

	static byte[] bigIntegerToBytes(BigInteger bi, int length) {
		byte[] data = bi.toByteArray();
		if (data.length == length) {
			return data;
		}
		// remove leading zero:
		if (data[0] == 0) {
			data = Arrays.copyOfRange(data, 1, data.length);
		}
		if (data.length > length) {
			throw new IllegalArgumentException("BigInteger is too large.");
		}
		byte[] copy = new byte[length];
		System.arraycopy(data, 0, copy, length - data.length, data.length);
		return copy;
	}

	static void checkPrivateKey(BigInteger bi) {
		if (bi == null) {
			throw new IllegalArgumentException("Private key is null.");
		}
		if (bi.compareTo(MIN_PRIVATE_KEY) == (-1)) {
			throw new IllegalArgumentException("Private key is too small.");
		}
		if (bi.compareTo(MAX_PRIVATE_KEY) == 1) {
			throw new IllegalArgumentException("Private key is too large.");
		}
	}

	static final byte PUBLIC_KEY_PREFIX = 0x04;
	static final byte[] PUBLIC_KEY_PREFIX_ARRAY = { PUBLIC_KEY_PREFIX };

	static final byte PRIVATE_KEY_PREFIX = (byte) 0x80;
	static final byte[] PRIVATE_KEY_PREFIX_ARRAY = { PRIVATE_KEY_PREFIX };

	private static final BigInteger MIN_PRIVATE_KEY = new BigInteger("ffffffffffffffff", 16);
	private static final BigInteger MAX_PRIVATE_KEY = new BigInteger(
			"fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364140", 16);

	@Override
	public String toString() {
		return "KeyPair<" + this.getPrivateKey().toString(16) + ">";
	}
}
