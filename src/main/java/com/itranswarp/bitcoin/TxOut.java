package com.itranswarp.bitcoin;

import java.io.IOException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itranswarp.bitcoin.io.BitCoinBlockDataInput;
import com.itranswarp.bitcoin.io.BitCoinBlockDataOutput;
import com.itranswarp.cryptocurrency.common.SatoshiSerializer;
import com.itranswarp.cryptocurrency.common.ScriptParser;

public class TxOut {

	long value; // int64, Transaction Value
	long scriptLength;
	byte[] pk_script; // uchar[], Usually contains the public key as a Bitcoin
						// script setting up conditions to claim this output.

	String address;

	public TxOut(BitCoinBlockDataInput input) throws IOException {
		this.value = input.readLong();
		this.scriptLength = input.readVarInt();
		this.pk_script = input.readBytes((int) scriptLength);
	}

	@JsonSerialize(using = SatoshiSerializer.class)
	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public byte[] getPk_script() {
		return pk_script;
	}

	public void setPk_script(byte[] pk_script) {
		this.pk_script = pk_script;
	}

	public String getAddress() {
		ScriptParser p = new ScriptParser();
		p.parse(this.pk_script);
		return p.getAddress();
	}

	public String getScript() {
		return new ScriptParser().parse(this.pk_script);
	}

	public void dump(BitCoinBlockDataOutput output) throws IOException {
		output.writeLong(value);
		output.writeVarInt(this.scriptLength);
		output.write(pk_script);
	}
}
