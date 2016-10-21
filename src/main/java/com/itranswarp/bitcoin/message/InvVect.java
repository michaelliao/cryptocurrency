package com.itranswarp.bitcoin.message;

import java.io.IOException;

import com.itranswarp.bitcoin.io.BitcoinInput;

public class InvVect {

	int type; // uint32
	byte[] hash; // 32-bytes hash

	public InvVect() {
	}

	public InvVect(BitcoinInput input) throws IOException {
		this.type = input.readInt();
		this.hash = input.readBytes(32);
	}
}
