package com.itranswarp.cryptocurrency.common;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.jcajce.provider.digest.RIPEMD160;
import org.bouncycastle.util.Arrays;

public class Hash {

	public static byte[] ripeMd160(byte[] input) {
		MessageDigest digest = new RIPEMD160.Digest();
		digest.update(input);
		return digest.digest();
	}

	public static byte[] sha256(byte[] input) {
		Digest d = new SHA256Digest();
		d.update(input, 0, input.length);
		byte[] out = new byte[d.getDigestSize()];
		d.doFinal(out, 0);
		return out;
	}

	public static byte[] doubleSha256(byte[] input) {
		byte[] round1 = sha256(input);
		return sha256(round1);
	}

	static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

	public static String toReversedHexString(byte[] b) {
		return toHexString(Arrays.reverse(b), false);
	}

	public static String toHexString(byte[] b) {
		return toHexString(b, false);
	}

	public static String toHexString(byte[] b, boolean sep) {
		StringBuilder sb = new StringBuilder(b.length << 2);
		for (byte x : b) {
			int hi = (x & 0xf0) >> 4;
			int lo = x & 0x0f;
			sb.append(HEX_CHARS[hi]);
			sb.append(HEX_CHARS[lo]);
			if (sep) {
				sb.append(' ');
			}
		}
		return sb.toString().trim();
	}

	public static String toHexStringAsBigEndian(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length << 2);
		for (int i = b.length - 1; i >= 0; i--) {
			byte x = b[i];
			int hi = (x & 0xf0) >> 4;
			int lo = x & 0x0f;
			sb.append(HEX_CHARS[hi]);
			sb.append(HEX_CHARS[lo]);
		}
		return sb.toString();
	}

	public static String hmacSha1(String data, String key) {
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
		Mac mac = null;
		try {
			mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new RuntimeException(e);
		}
		byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(rawHmac);
	}
}
