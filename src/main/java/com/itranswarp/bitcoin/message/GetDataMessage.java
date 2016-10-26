package com.itranswarp.bitcoin.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.bitcoin.struct.InvVect;

/**
 * Build P2P message:
 * https://en.bitcoin.it/wiki/Protocol_documentation#Message_structure
 * 
 * @author liaoxuefeng
 */
public class GetDataMessage extends Message {

	long count; // var int
	InvVect[] inventory; // byte[36]

	public GetDataMessage(int type, byte[]... hashes) {
		super("getdata");
		this.count = hashes.length;
		this.inventory = new InvVect[hashes.length];
		for (int i = 0; i < this.inventory.length; i++) {
			InvVect iv = new InvVect();
			iv.type = type;
			iv.hash = hashes[i];
			this.inventory[i] = iv;
		}
	}

	public GetDataMessage(byte[] payload) throws IOException {
		super("getdata");
		try (BitcoinInput input = new BitcoinInput(new ByteArrayInputStream(payload))) {
			this.count = input.readVarInt();
			this.inventory = new InvVect[(int) this.count];
			for (int i = 0; i < this.count; i++) {
				this.inventory[i] = new InvVect(input);
			}
		}
	}

	@Override
	protected byte[] getPayload() {
		BitcoinOutput output = new BitcoinOutput().writeVarInt(this.count);
		for (int i = 0; i < this.count; i++) {
			output.write(this.inventory[i].toByteArray());
		}
		return output.toByteArray();
	}

	@Override
	public String toString() {
		return "GetDataMessage(count=" + this.count + ")";
	}

}
