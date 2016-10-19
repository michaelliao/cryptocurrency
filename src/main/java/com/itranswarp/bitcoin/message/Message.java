package com.itranswarp.bitcoin.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.bouncycastle.util.Arrays;

import com.itranswarp.bitcoin.BitcoinConstants;
import com.itranswarp.bitcoin.io.BitCoinBlockDataOutput;
import com.itranswarp.cryptocurrency.common.Hash;

/**
 * P2P message:
 * https://en.bitcoin.it/wiki/Protocol_documentation#Message_structure
 * 
 * @author liaoxuefeng
 */
public class Message {

	byte[] command;

	byte[] payload;

	public Message(String cmd, byte[] payload) {
		if (payload == null || payload.length == 0) {
			throw new IllegalArgumentException("Bad payload");
		}
		this.command = getCommandBytes(cmd);
		this.payload = payload;
	}

	public byte[] getBody() {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try (BitCoinBlockDataOutput output = new BitCoinBlockDataOutput(buffer)) {
			// magic: uint32_t
			output.writeInt(BitcoinConstants.MAGIC);
			// command: char[12]
			output.write(this.command);
			// length: uint32_t
			output.writeInt(this.payload.length);
			// checksum: uint32_t
			output.write(getCheckSum(this.payload));
			// payload:
			output.write(this.payload);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return buffer.toByteArray();
	}

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
