package com.itranswarp.bitcoin.p2p.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.bitcoin.util.RandomUtils;

/**
 * Build P2P message:
 * https://en.bitcoin.it/wiki/Protocol_documentation#Message_structure
 * 
 * @author liaoxuefeng
 */
public class PingMessage extends Message {

	long nonce;

	public PingMessage() {
		super("ping");
		this.nonce = RandomUtils.randomLong();
	}

	public PingMessage(byte[] payload) throws IOException {
		super("ping");
		try (BitcoinInput input = new BitcoinInput(new ByteArrayInputStream(payload))) {
			this.nonce = input.readLong();
		}
	}

	public long getNonce() {
		return this.nonce;
	}

	@Override
	protected byte[] getPayload() {
		return new BitcoinOutput().writeLong(this.nonce).toByteArray();
	}

	@Override
	public String toString() {
		return "PingMessage(nonce=" + this.nonce + ")";
	}

}
