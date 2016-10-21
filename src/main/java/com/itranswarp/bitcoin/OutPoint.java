package com.itranswarp.bitcoin;

import java.io.IOException;

import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;

public class OutPoint {

	byte[] hash; // 32-bytes, the hash of the referenced transaction.
	long index; // uint32, the index of the specific output in the transaction.
				// The first output is 0, etc.

	public OutPoint(BitcoinInput input) throws IOException {
		this.hash = input.readBytes(32);
		this.index = input.readUnsignedInt();
	}

	public byte[] getHash() {
		return hash;
	}

	public void setHash(byte[] hash) {
		this.hash = hash;
	}

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public byte[] toByteArray() {
		return new BitcoinOutput().write(hash).writeUnsignedInt(index).toByteArray();
	}
}
