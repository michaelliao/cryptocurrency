package com.itranswarp.bitcoin.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.cryptocurrency.common.Hash;

public class InvMessage extends Message {

	InvVect[] inventory;

	public InvMessage() {
		super("inv");
		inventory = new InvVect[0];
	}

	public InvMessage(byte[] payload) throws IOException {
		super("inv");
		try (BitcoinInput input = new BitcoinInput(new ByteArrayInputStream(payload))) {
			long count = input.readVarInt(); // do not store count
			this.inventory = new InvVect[(int) count];
			for (int i = 0; i < count; i++) {
				this.inventory[i] = new InvVect(input);
			}
		}
	}

	public String[] getBlockHashes() {
		return Arrays.stream(this.inventory).filter((iv) -> {
			return iv.type == InvVect.MSG_BLOCK;
		}).map((iv) -> {
			return Hash.toHexStringAsLittleEndian(iv.hash);
		}).toArray(String[]::new);
	}

	@Override
	protected byte[] getPayload() {
		BitcoinOutput output = new BitcoinOutput();
		output.writeVarInt(this.inventory.length);
		for (int i = 0; i < this.inventory.length; i++) {
			output.write(this.inventory[i].toByteArray());
		}
		return output.toByteArray();
	}

	@Override
	public String toString() {
		return "InvMessage([" + String.join(", ", Arrays.stream(this.inventory).map((inv) -> {
			return inv.type + ":" + Hash.toHexStringAsLittleEndian(inv.hash);
		}).toArray(String[]::new)) + "])";
	}
}
