package com.itranswarp.bitcoin.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.bitcoin.struct.Header;
import com.itranswarp.bitcoin.struct.Transaction;
import com.itranswarp.bitcoin.util.BytesUtils;
import com.itranswarp.bitcoin.util.HashUtils;

/**
 * Build P2P message:
 * https://en.bitcoin.it/wiki/Protocol_documentation#Message_structure
 * 
 * @author liaoxuefeng
 */
public class BlockMessage extends Message {

	public Header header;
	public Transaction[] txns;

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
			for (int i = 0; i < this.txns.length; i++) {
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
			results[i] = HashUtils.doubleSha256(BytesUtils.concat(hashes[2 * i], hashes[2 * i + 1]));
		}
		if (extra == 1) {
			results[count] = HashUtils
					.doubleSha256(BytesUtils.concat(hashes[hashes.length - 1], hashes[hashes.length - 1]));
		}
		return results;
	}

	@Override
	public String toString() {
		return "BlockMessage(txnCount=" + this.txns.length + ")";
	}

}
