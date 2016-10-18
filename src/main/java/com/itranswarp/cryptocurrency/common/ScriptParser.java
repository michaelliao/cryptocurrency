package com.itranswarp.cryptocurrency.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScriptParser {

	/**
	 * Parse BitCoin script: https://en.bitcoin.it/wiki/Script
	 */
	public static String parse(byte[] script) {
		try (LittleEndianDataInputStream input = new LittleEndianDataInputStream(new ByteArrayInputStream(script))) {
			List<String> list = new ArrayList<>();
			int n;
			while ((n = input.read()) != (-1)) {
				switch (n) {
				case 0x61:
					list.add("OP_NOP");
					break;
				case 0x69:
					list.add("OP_VERIFY");
					break;
				case 0x6b:
					list.add("OP_TOALTSTACK");
					break;
				case 0x6c:
					list.add("OP_FROMALTSTACK");
					break;
				case 0x76:
					list.add("OP_DUP");
					break;
				case 0x88:
					list.add("OP_EQUALVERIFY");
					break;
				case 0xa9:
					list.add("OP_HASH160");
					break;
				case 0xaa:
					list.add("OP_HASH256");
					break;
				case 0xac:
					list.add("OP_CHECKSIG");
					break;
				default:
					if (n >= 0x01 && n <= 0x4b) {
						list.add("DATA(" + KeyPair.hashToPublicKey(input.readBytes(n)) + ")");
					} else {
						list.add("???");
					}
				}
			}
			return String.join(" ", list);
		} catch (IOException e) {
			return "ERROR: " + e.toString();
		}
	}

}
