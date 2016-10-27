package com.itranswarp.bitcoin.struct;

import java.io.IOException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.cryptocurrency.common.HashSerializer;

public class OutPoint {

	@JsonSerialize(using = HashSerializer.class)
	public byte[] hash; // 32-bytes, the hash of the referenced transaction.

	public long index; // uint32, the index of the specific output in the
						// transaction. The first output is 0, etc.

	public OutPoint(BitcoinInput input) throws IOException {
		this.hash = input.readBytes(32);
		this.index = input.readUnsignedInt();
	}

	public byte[] toByteArray() {
		return new BitcoinOutput().write(hash).writeUnsignedInt(index).toByteArray();
	}
}
