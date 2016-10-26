package com.itranswarp.bitcoin.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.itranswarp.bitcoin.Transaction;
import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.bitcoin.struct.Header;
import com.itranswarp.cryptocurrency.common.Hash;

/**
 * Build P2P message:
 * https://en.bitcoin.it/wiki/Protocol_documentation#Message_structure
 * 
 * @author liaoxuefeng
 */
public class BlockMessage extends Message {

	Header header;
	Transaction[] txns;

	public BlockMessage() {
		super("addr");
		this.txns = new Transaction[0];
	}

	public BlockMessage(byte[] payload) throws IOException {
		super("addr");
		try (BitcoinInput input = new BitcoinInput(new ByteArrayInputStream(payload))) {
			this.header = new Header(input);
			long txnCount = input.readVarInt(); // do not store txn_count
			this.txns = new Transaction[(int) txnCount];
			for (int i = 0; i < txnCount; i++) {
				this.txns[i] = new Transaction(input);
			}
		}
	}

	@Override
	protected byte[] getPayload() {
		BitcoinOutput output = new BitcoinOutput();
		output.write(this.header.toByteArray());
		output.writeVarInt(this.txns.length);
		for (int i = 0; i < this.txns.length; i++) {
			output.write(this.txns[i].toByteArray());
		}
		return output.toByteArray();
	}

	public byte[] calculateMerkleRoot() {
		byte[][] hashes = java.util.Arrays.asList(this.txns).stream().map((tx) -> {
			return tx.calculateHash();
		}).toArray(byte[][]::new);
		while (hashes.length > 1) {
			hashes = merkleHash(hashes);
		}
		return hashes[0];
	}

	byte[][] merkleHash(byte[][] hashes) {
		int count = hashes.length / 2;
		int extra = hashes.length % 2;
		byte[][] results = new byte[count + extra][];
		for (int i = 0; i < count; i++) {
			results[i] = Hash.doubleSha256(concat(hashes[2 * i], hashes[2 * i + 1]));
		}
		if (extra == 1) {
			results[count] = Hash.doubleSha256(concat(hashes[hashes.length - 1], hashes[hashes.length - 1]));
		}
		return results;
	}

	byte[] concat(byte[] b1, byte[] b2) {
		byte[] r = new byte[b1.length + b2.length];
		int offset = 0;
		System.arraycopy(b1, 0, r, offset, b1.length);
		offset += b1.length;
		System.arraycopy(b2, 0, r, offset, b2.length);
		return r;
	}

	@Override
	public String toString() {
		return "BlockMessage(txnCount=" + this.txns.length + ")";
	}

}
