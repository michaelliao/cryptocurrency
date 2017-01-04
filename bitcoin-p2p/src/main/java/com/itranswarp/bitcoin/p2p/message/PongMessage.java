package com.itranswarp.bitcoin.p2p.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;

/**
 * Build P2P message:
 * https://en.bitcoin.it/wiki/Protocol_documentation#Message_structure
 * 
 * @author liaoxuefeng
 */
public class PongMessage extends Message {

	long nonce;

	public PongMessage(long nonce) {
		super("pong");
		this.nonce = nonce;
	}

	public PongMessage(byte[] payload) throws IOException {
		super("pong");
		try (BitcoinInput input = new BitcoinInput(new ByteArrayInputStream(payload))) {
			this.nonce = input.readLong();
		}
	}

	@Override
	protected byte[] getPayload() {
		return new BitcoinOutput().writeLong(this.nonce).toByteArray();
	}

	@Override
	public String toString() {
		return "PongMessage(nonce=" + this.nonce + ")";
	}

}
