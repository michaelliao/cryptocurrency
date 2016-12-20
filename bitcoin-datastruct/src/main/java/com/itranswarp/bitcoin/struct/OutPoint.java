package com.itranswarp.bitcoin.struct;

import java.io.IOException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.bitcoin.serializer.HashSerializer;

public class OutPoint {

	/**
	 * 32-bytes, the hash of the referenced transaction.
	 */
	@JsonSerialize(using = HashSerializer.class)
	public byte[] hash;

	/**
	 * uint32, the index of the specific output in the transaction. The first
	 * output is 0, etc.
	 */
	public long index;

	public OutPoint(BitcoinInput input) throws IOException {
		this.hash = input.readBytes(32);
		this.index = input.readUnsignedInt();
	}

	public byte[] toByteArray() {
		return new BitcoinOutput().write(hash).writeUnsignedInt(index).toByteArray();
	}
}
