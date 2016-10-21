package com.itranswarp.bitcoin.message;

import java.io.IOException;

import com.itranswarp.bitcoin.io.BitcoinInput;

public class Inv {

	long count;
	InvVect[] inventory;

	public Inv() {
	}

	public Inv(BitcoinInput input) throws IOException {
		this.count = input.readVarInt();
		this.inventory = new InvVect[(int) this.count];
		for (int i = 0; i < this.count; i++) {
			this.inventory[i] = new InvVect(input);
		}
	}
}
