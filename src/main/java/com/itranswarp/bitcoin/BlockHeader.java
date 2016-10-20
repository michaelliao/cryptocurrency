package com.itranswarp.bitcoin;

import java.io.IOException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itranswarp.bitcoin.io.BitCoinInput;
import com.itranswarp.cryptocurrency.common.HashSerializer;
import com.itranswarp.cryptocurrency.common.TimestampSerializer;

public class BlockHeader {

	int version; // int32, block version information (note, this is signed)
	byte[] prevHash; // 32 bytes, The hash value of the previous block this
						// particular block references
	byte[] merkleHash; // 32 bytes, The reference to a Merkle tree collection
						// which is a hash of all transactions related to this
						// block
	long timestamp; // uint32, A timestamp recording when this block was created
					// (Will overflow in 2106)
	long bits; // uint32, The calculated difficulty target being used for this
				// block
	long nonce; // uint32, The nonce used to generate this block… to allow
				// variations of the header and compute different hashes

	public BlockHeader(BitCoinInput input) throws IOException {
		this.version = input.readInt();
		this.prevHash = input.readBytes(32);
		this.merkleHash = input.readBytes(32);
		this.timestamp = input.readUnsignedInt();
		this.bits = input.readUnsignedInt();
		this.nonce = input.readUnsignedInt();
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@JsonSerialize(using = HashSerializer.class)
	public byte[] getPrevHash() {
		return prevHash;
	}

	public void setPrevHash(byte[] prevHash) {
		this.prevHash = prevHash;
	}

	@JsonSerialize(using = HashSerializer.class)
	public byte[] getMerkleHash() {
		return merkleHash;
	}

	public void setMerkleHash(byte[] merkleHash) {
		this.merkleHash = merkleHash;
	}

	@JsonSerialize(using = TimestampSerializer.class)
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getBits() {
		return bits;
	}

	public void setBits(long bits) {
		this.bits = bits;
	}

	public long getNonce() {
		return nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

}
