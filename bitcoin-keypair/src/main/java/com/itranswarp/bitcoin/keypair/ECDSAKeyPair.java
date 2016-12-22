package com.itranswarp.bitcoin.keypair;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

import com.itranswarp.bitcoin.constant.BitcoinConstants;
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
	 * Convert to public key as uncompressed byte[].
	 */
	public byte[] toUncompressedPublicKey() {
		BigInteger[] keys = getPublicKey();
		byte[] xs = bigIntegerToBytes(keys[0], 32);
		byte[] ys = bigIntegerToBytes(keys[1], 32);
		return BytesUtils.concat(BitcoinConstants.PUBLIC_KEY_PREFIX_ARRAY, xs, ys);
	}

	/**
	 * Convert to java.security.PublicKey.
	 */
	public static BigInteger[] toPublicKey(byte[] uncompressedPk) {
		if (uncompressedPk == null || uncompressedPk.length != 65) {
			throw new IllegalArgumentException("Invalid public key.");
		}
		if (uncompressedPk[0] != BitcoinConstants.PUBLIC_KEY_PREFIX) {
			throw new IllegalArgumentException("Invalid public key.");
		}
		byte[] b1 = new byte[32];
		byte[] b2 = new byte[32];
		System.arraycopy(uncompressedPk, 1, b1, 0, 32);
		System.arraycopy(uncompressedPk, 33, b2, 0, 32);
		return new BigInteger[] { new BigInteger(1, b1), new BigInteger(1, b2) };
	}

	/**
	 * Convert to java.security.PublicKey.
	 */
	public static PublicKey toPublicKey(BigInteger[] pubKey) {
		try {
			KeyFactory kf = KeyFactory.getInstance("ECDSA", Secp256k1Utils.BC);
			ECPoint point = Secp256k1Utils.getCurve().createPoint(pubKey[0], pubKey[1]);
			ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(point, Secp256k1Utils.SPEC);
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

	public boolean verifySignature2(byte[] data, byte[] signature, byte[] pub) {
		ECDSASigner signer = new ECDSASigner();
		ECPublicKeyParameters params = new ECPublicKeyParameters(Secp256k1Utils.getCurve().decodePoint(pub),
				Secp256k1Utils.ECPARAMS);
		signer.init(false, params);
		try {
			ASN1InputStream decoder = new ASN1InputStream(signature);
			DERSequence seq = (DERSequence) decoder.readObject();
			ASN1Integer r = (ASN1Integer) seq.getObjectAt(0);
			ASN1Integer s = (ASN1Integer) seq.getObjectAt(1);
			decoder.close();
			return signer.verifySignature(data, r.getValue(), s.getValue());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
		byte[] uncompressed = BytesUtils.concat(BitcoinConstants.PUBLIC_KEY_PREFIX_ARRAY, xs, ys);
		return Secp256k1Utils.publicKeyToAddress(uncompressed);
	}

	/**
	 * Get Wallet Import Format string defined in:
	 * https://en.bitcoin.it/wiki/Wallet_import_format
	 */
	public String getWalletImportFormat() {
		byte[] key = bigIntegerToBytes(this.privateKey, 32);
		byte[] extendedKey = BytesUtils.concat(BitcoinConstants.PRIVATE_KEY_PREFIX_ARRAY, key);
		byte[] hash = HashUtils.doubleSha256(extendedKey);
		byte[] checksum = Arrays.copyOfRange(hash, 0, 4);
		byte[] extendedKeyWithChecksum = BytesUtils.concat(extendedKey, checksum);
		return Base58Utils.encode(extendedKeyWithChecksum);
	}

	static byte[] parseWIF(String wif) {
		byte[] data = Base58Utils.decodeChecked(wif);
		if (data[0] != BitcoinConstants.PRIVATE_KEY_PREFIX) {
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
		if (bi.compareTo(BitcoinConstants.MIN_PRIVATE_KEY) == (-1)) {
			throw new IllegalArgumentException("Private key is too small.");
		}
		if (bi.compareTo(BitcoinConstants.MAX_PRIVATE_KEY) == 1) {
			throw new IllegalArgumentException("Private key is too large.");
		}
	}

	@Override
	public String toString() {
		return "KeyPair<" + this.getPrivateKey().toString(16) + ">";
	}
}
