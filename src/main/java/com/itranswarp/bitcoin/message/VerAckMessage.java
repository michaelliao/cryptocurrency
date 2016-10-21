package com.itranswarp.bitcoin.message;

import java.io.IOException;

/**
 * Build P2P message:
 * https://en.bitcoin.it/wiki/Protocol_documentation#Message_structure
 * 
 * @author liaoxuefeng
 */
public class VerAckMessage extends Message {

	public VerAckMessage(byte[] payload) throws IOException {
		super("verack");
	}

	@Override
	protected byte[] getPayload() {
		return new byte[0];
	}
}
