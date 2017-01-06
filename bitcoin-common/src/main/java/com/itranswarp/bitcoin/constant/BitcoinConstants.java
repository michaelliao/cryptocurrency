package com.itranswarp.bitcoin.constant;

import java.math.BigInteger;

import com.itranswarp.bitcoin.util.HashUtils;

public final class BitcoinConstants {

	public static final int MAGIC = 0xd9b4bef9;

	public static final int PORT = 8333;

	public static final int PROTOCOL_VERSION = 70014;

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

	public static final byte[] GENESIS_HASH_BYTES = HashUtils.toBytesAsLittleEndian(GENESIS_HASH);

	public static final byte[] GENESIS_BLOCK_DATA = HashUtils.toBytes(
			"0100000000000000000000000000000000000000000000000000000000000000000000003ba3edfd7a7b12b27ac72c3e67768f617fc81bc3888a51323a9fb8aa4b1e5e4a29ab5f49ffff001d1dac2b7c0101000000010000000000000000000000000000000000000000000000000000000000000000ffffffff4d04ffff001d0104455468652054696d65732030332f4a616e2f32303039204368616e63656c6c6f72206f6e206272696e6b206f66207365636f6e64206261696c6f757420666f722062616e6b73ffffffff0100f2052a01000000434104678afdb0fe5548271967f1a67130b7105cd6a828e03909a67962e0ea1f61deb649f6bc3f4cef38c4f35504e51ec112de5c384df7ba0b8d578a4c702b6bf11d5fac00000000");

	public static final String ZERO_HASH = "0000000000000000000000000000000000000000000000000000000000000000";

	public static final byte[] ZERO_HASH_BYTES = HashUtils
			.toBytesAsLittleEndian("0000000000000000000000000000000000000000000000000000000000000000");

	public static final int SIGHASH_ALL = 0x01;
}
