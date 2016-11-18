package com.itranswarp.bitcoin.struct;

import java.io.IOException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.bitcoin.util.ScriptParser;
import com.itranswarp.cryptocurrency.common.SatoshiSerializer;

public class TxOut {

	@JsonSerialize(using = SatoshiSerializer.class)
	public long value; // int64, Transaction Value

	public long scriptLength;
	public byte[] pk_script; // uchar[], Usually contains the public key as a
								// Bitcoin
	// script setting up conditions to claim this output.

	String address;

	public TxOut(BitcoinInput input) throws IOException {
		this.value = input.readLong();
		this.scriptLength = input.readVarInt();
		this.pk_script = input.readBytes((int) scriptLength);
	}

	public String getAddress() {
		ScriptParser p = new ScriptParser();
		p.parse(this.pk_script);
		return p.getAddress();
	}

	public String getScript() {
		return new ScriptParser().parse(this.pk_script);
	}

	public byte[] toByteArray() {
		return new BitcoinOutput().writeLong(value).writeVarInt(this.scriptLength).write(pk_script).toByteArray();
	}
}
