package com.itranswarp.bitcoin;

import com.itranswarp.bitcoin.util.HashUtils;

public final class BitcoinConstants {

	public static final int MAGIC = 0xd9b4bef9;

	public static final int PORT = 8333;

	public static final int PROTOCOL_VERSION = 70002;

	public static final long NETWORK_SERVICES = 1L;

	public static final long NODE_ID = (long) (Math.random() * Long.MAX_VALUE);

	public static final String SUB_VERSION = "/Satoshi:0.7.2/";

	public static final String GENESIS_HASH = "000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f";

	public static final String ZERO_HASH = "0000000000000000000000000000000000000000000000000000000000000000";

	public static final byte[] GENESIS_HASH_BYTES = HashUtils.toBytesAsLittleEndian(GENESIS_HASH);

	public static final byte[] ZERO_HASH_BYTES = HashUtils
			.toBytesAsLittleEndian("0000000000000000000000000000000000000000000000000000000000000000");
}
