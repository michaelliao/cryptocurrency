package com.itranswarp.bitcoin.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.cryptocurrency.common.Hash;

public class InvMessage extends Message {

	long count;
	InvVect[] inventory;

	public InvMessage() {
		super("inv");
	}

	public InvMessage(byte[] payload) throws IOException {
		super("inv");
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
		BitcoinOutput output = new BitcoinOutput();
		output.writeVarInt(this.inventory.length);
		for (int i = 0; i < this.inventory.length; i++) {
			output.write(this.inventory[i].toByteArray());
		}
		return output.toByteArray();
	}

	public String toString() {
		return "InvMessage([" + String.join(", ", Arrays.stream(this.inventory).map((inv) -> {
			return inv.type + ":" + Hash.toHexStringAsBigEndian(inv.hash);
		}).toArray(String[]::new)) + "])";
	}
}
