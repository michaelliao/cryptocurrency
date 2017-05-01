package com.itranswarp.bitcoin.struct;

import java.io.IOException;

import com.itranswarp.bitcoin.io.BitcoinInput;

/**
 * Inventory array.
 * 
 * @author Michael Liao
 */
public class Inv {

	InvVect[] inventory;

	public Inv() {
	}

	public Inv(BitcoinInput input) throws IOException {
		long count = input.readVarInt(); // do not store count
		this.inventory = new InvVect[(int) count];
		for (int i = 0; i < this.inventory.length; i++) {
			this.inventory[i] = new InvVect(input);
		}
	}
}
