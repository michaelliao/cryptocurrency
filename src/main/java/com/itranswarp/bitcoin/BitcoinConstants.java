package com.itranswarp.bitcoin;

import com.itranswarp.cryptocurrency.common.Hash;

public final class BitcoinConstants {

	public static final int MAGIC = 0xd9b4bef9;

	public static final int PORT = 8333;

	public static final int PROTOCOL_VERSION = 70002;

	public static final long NETWORK_SERVICES = 1L;

	public static final long NODE_ID = (long) (Math.random() * Long.MAX_VALUE);

	public static final String SUB_VERSION = "/Satoshi:0.7.2/";

	public static final byte[] GENESIS_HASH = Hash
			.toBytesAsLittleEndian("000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f");

	public static final byte[] ZERO_HASH = Hash
			.toBytesAsLittleEndian("0000000000000000000000000000000000000000000000000000000000000000");
}
