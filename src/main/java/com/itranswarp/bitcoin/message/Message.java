package com.itranswarp.bitcoin.message;

import org.bouncycastle.util.Arrays;

import com.itranswarp.bitcoin.BitcoinConstants;
import com.itranswarp.bitcoin.io.BitCoinOutput;
import com.itranswarp.cryptocurrency.common.Hash;

/**
 * P2P message:
 * https://en.bitcoin.it/wiki/Protocol_documentation#Message_structure
 * 
 * @author liaoxuefeng
 */
public abstract class Message {

	byte[] command;

	public Message(String cmd) {
		this.command = getCommandBytes(cmd);
	}

	public byte[] toByteArray() {
		byte[] payload = getPayload();
		return new BitCoinOutput().writeInt(BitcoinConstants.MAGIC) // magic
				.write(this.command) // command: char[12]
				.writeInt(payload.length) // length: uint32_t
				.write(getCheckSum(payload)) // checksum: uint32_t
				.write(payload) // payload:
				.toByteArray();
	}

	protected abstract byte[] getPayload();

	byte[] getCommandBytes(String cmd) {
		byte[] cmdBytes = cmd.getBytes();
		if (cmdBytes.length < 1 || cmdBytes.length > 12) {
			throw new IllegalArgumentException("Bad command: " + cmd);
		}
		byte[] buffer = new byte[12];
		System.arraycopy(cmdBytes, 0, buffer, 0, cmdBytes.length);
		return buffer;
	}

	byte[] getCheckSum(byte[] payload) {
		byte[] hash = Hash.doubleSha256(payload);
		return Arrays.copyOfRange(hash, 0, 4);
	}
}
