package com.itranswarp.bitcoin.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

	static final Log log = LogFactory.getLog(BlockMessage.class);

	private byte[] blockHash = null;

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

	/**
	 * Validate block hash.
	 */
	public boolean validateHash() {
		byte[] merkleHash = calculateMerkleHash();
		if (!Arrays.equals(merkleHash, this.header.merkleHash)) {
			log.error("Validate merckle hash failed.");
			return false;
		}
		byte[] blockHash = getBlockHash();
		// TODO: validate bits:
		return true;
	}

	public byte[] getBlockHash() {
		if (this.blockHash == null) {
			byte[] data = new BitcoinOutput().writeInt(this.header.version).write(this.header.prevHash)
					.write(this.header.merkleHash).writeUnsignedInt(this.header.timestamp)
					.writeUnsignedInt(this.header.bits).writeUnsignedInt(this.header.nonce).toByteArray();
			this.blockHash = HashUtils.doubleSha256(data);
		}
		return this.blockHash;
	}

	byte[] calculateMerkleHash() {
		byte[][] hashes = java.util.Arrays.asList(this.txns).stream().map((tx) -> {
			return tx.getTxHash();
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
