package com.itranswarp.bitcoin.struct;

import java.io.IOException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.bitcoin.serializer.SatoshiSerializer;
import com.itranswarp.bitcoin.util.ScriptParser;

public class TxOut {

	/**
	 * int64, Transaction Value
	 */
	@JsonSerialize(using = SatoshiSerializer.class)
	public long value;

	public long scriptLength;

	/**
	 * uchar[], Usually contains the public key as a Bitcoin script setting up
	 * conditions to claim this output.
	 */
	public byte[] pk_script;

	private String address;

	public TxOut(BitcoinInput input) throws IOException {
		this.value = input.readLong();
		this.scriptLength = input.readVarInt();
		this.pk_script = input.readBytes((int) scriptLength);
	}

	public String getAddress() {
		if (this.address == null) {
			ScriptParser p = new ScriptParser();
			p.parse(this.pk_script);
			this.address = p.getAddress();
		}
		return this.address;
	}

	public String getScript() {
		return new ScriptParser().parse(this.pk_script);
	}

	public byte[] toByteArray() {
		return new BitcoinOutput().writeLong(value).writeVarInt(this.scriptLength).write(pk_script).toByteArray();
	}
}
