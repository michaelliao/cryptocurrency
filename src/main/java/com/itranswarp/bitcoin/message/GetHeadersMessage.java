package com.itranswarp.bitcoin.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.itranswarp.bitcoin.io.BitcoinInput;

/**
 * Build P2P message:
 * https://en.bitcoin.it/wiki/Protocol_documentation#Message_structure
 * 
 * @author liaoxuefeng
 */
public class GetHeadersMessage extends Message {

	int version; // uint32
	long hashCount; // var int
	byte[][] hashes; // byte[32]
	byte[] hashStop; // hash of the last desired block header; set to zero to
						// get as many blocks as possible (2000)

	public GetHeadersMessage(byte[] payload) throws IOException {
		super("getheaders");
		try (BitcoinInput input = new BitcoinInput(new ByteArrayInputStream(payload))) {
			this.version = input.readInt();
			this.hashCount = input.readVarInt();
			this.hashes = new byte[(int) this.hashCount][];
			for (int i = 0; i < this.hashCount; i++) {
				this.hashes[i] = input.readBytes(32);
			}
			this.hashStop = input.readBytes(32);
		}
	}

	@Override
	protected byte[] getPayload() {
		return new byte[0];
	}

	@Override
	public String toString() {
		return "GetHeadersMessage(hashCount=" + this.hashCount + ")";
	}

}
