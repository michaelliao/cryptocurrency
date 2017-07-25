package com.itranswarp.bitcoin.wallet;

import java.util.Arrays;
import java.util.Objects;

import com.itranswarp.bitcoin.keypair.ECDSAKeyPair;

public class SecretKey {

	public final String label;
	public final byte[] key;

	public SecretKey(String label, byte[] key) {
		this.label = label;
		this.key = key;
	}

	public SecretKey copy() {
		byte[] copy = Arrays.copyOf(this.key, this.key.length);
		return new SecretKey(this.label, copy);
	}

	public String toPublicAddress() {
		return ECDSAKeyPair.of(this.key).toEncodedCompressedPublicKey();
	}

	public String toWIF() {
		return ECDSAKeyPair.of(this.key).toCompressedWIF();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SecretKey) {
			SecretKey k = (SecretKey) o;
			return Objects.equals(this.label, k.label) && Objects.deepEquals(this.key, k.key);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.label, Arrays.hashCode(this.key));
	}
}
