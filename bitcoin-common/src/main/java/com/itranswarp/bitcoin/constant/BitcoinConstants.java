package com.itranswarp.bitcoin.constant;

import java.math.BigInteger;

import com.itranswarp.bitcoin.util.HashUtils;

public final class BitcoinConstants {

	public static final int MAGIC = 0xd9b4bef9;

	public static final int PORT = 8333;

	public static final int PROTOCOL_VERSION = 70002;

	public static final long NETWORK_SERVICES = 1L;

	public static final byte NETWORK_ID = 0x00;
	public static final byte[] NETWORK_ID_ARRAY = { NETWORK_ID };

	public static final byte PUBLIC_KEY_PREFIX = 0x04;
	public static final byte[] PUBLIC_KEY_PREFIX_ARRAY = { PUBLIC_KEY_PREFIX };

	public static final byte PRIVATE_KEY_PREFIX = (byte) 0x80;
	public static final byte[] PRIVATE_KEY_PREFIX_ARRAY = { PRIVATE_KEY_PREFIX };

	public static final BigInteger MIN_PRIVATE_KEY = new BigInteger("ffffffffffffffff", 16);
	public static final BigInteger MAX_PRIVATE_KEY = new BigInteger(
			"fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364140", 16);

	public static final long NODE_ID = (long) (Math.random() * Long.MAX_VALUE);

	public static final String SUB_VERSION = "/Satoshi:0.7.2/";

	public static final String GENESIS_HASH = "000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f";

	public static final String ZERO_HASH = "0000000000000000000000000000000000000000000000000000000000000000";

	public static final byte[] GENESIS_HASH_BYTES = HashUtils.toBytesAsLittleEndian(GENESIS_HASH);

	public static final byte[] ZERO_HASH_BYTES = HashUtils
			.toBytesAsLittleEndian("0000000000000000000000000000000000000000000000000000000000000000");

	public static final int SIGHASH_ALL = 0x01;
}
