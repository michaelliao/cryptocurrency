package com.itranswarp.bitcoin.struct;

import java.io.IOException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.bitcoin.serializer.SatoshiSerializer;

public class TxOut {

	/**
	 * int64, Transaction Value
	 */
	@JsonSerialize(using = SatoshiSerializer.class)
	public long value;

	/**
	 * uchar[], Usually contains the public key as a Bitcoin script setting up
	 * conditions to claim this output.
	 */
	public byte[] pk_script;

	public TxOut(BitcoinInput input) throws IOException {
		this.value = input.readLong();
		long scriptLength = input.readVarInt();
		this.pk_script = input.readBytes((int) scriptLength);
	}

	public byte[] toByteArray() {
		return new BitcoinOutput().writeLong(value).writeVarInt(this.pk_script.length).write(pk_script).toByteArray();
	}
}
