package com.itranswarp.bitcoin.struct;

import java.io.IOException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.cryptocurrency.common.HashSerializer;
import com.itranswarp.cryptocurrency.common.TimestampSerializer;

public class Header {

	public int version; // int32, block version information (note, this is
						// signed)
	@JsonSerialize(using = HashSerializer.class)
	public byte[] prevHash; // 32 bytes, The hash value of the previous block
							// this
							// particular block references
	@JsonSerialize(using = HashSerializer.class)
	public byte[] merkleHash; // 32 bytes, The reference to a Merkle tree
								// collection
								// which is a hash of all transactions related
								// to this
								// block
	@JsonSerialize(using = TimestampSerializer.class)
	public long timestamp; // uint32, A timestamp recording when this block was
							// created
							// (Will overflow in 2106)
	public long bits; // uint32, The calculated difficulty target being used for
						// this
						// block
	public long nonce; // uint32, The nonce used to generate this block… to
						// allow
						// variations of the header and compute different hashes

	public Header(BitcoinInput input) throws IOException {
		this.version = input.readInt();
		this.prevHash = input.readBytes(32);
		this.merkleHash = input.readBytes(32);
		this.timestamp = input.readUnsignedInt();
		this.bits = input.readUnsignedInt();
		this.nonce = input.readUnsignedInt();
	}

	public byte[] toByteArray() {
		return new BitcoinOutput().writeInt(this.version).write(this.prevHash).write(this.merkleHash)
				.writeUnsignedInt(this.timestamp).writeUnsignedInt(this.bits).writeUnsignedInt(this.nonce)
				.toByteArray();
	}
}
