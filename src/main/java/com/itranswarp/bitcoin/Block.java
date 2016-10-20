package com.itranswarp.bitcoin;

import java.io.IOException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itranswarp.bitcoin.io.BitCoinInput;
import com.itranswarp.bitcoin.io.BitCoinOutput;
import com.itranswarp.cryptocurrency.common.Hash;
import com.itranswarp.cryptocurrency.common.HashSerializer;

public class Block {

	int size;
	BlockHeader blockHeader;
	Transaction[] txs;

	public Block() {
	}

	public Block(BitCoinInput input) throws IOException {
		// read block size:
		this.size = input.readInt();
		this.blockHeader = new BlockHeader(input);
		// number of tx:
		long n_tx = input.readVarInt();
		this.txs = new Transaction[(int) n_tx];
		for (int i = 0; i < txs.length; i++) {
			txs[i] = new Transaction(input);
		}
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public BlockHeader getBlockHeader() {
		return blockHeader;
	}

	public void setBlockHeader(BlockHeader blockHeader) {
		this.blockHeader = blockHeader;
	}

	public long getNumOfTx() {
		return txs.length;
	}

	public Transaction[] getTxs() {
		return txs;
	}

	public void setTxs(Transaction[] txs) {
		this.txs = txs;
	}

	/**
	 * Get block hash. https://en.bitcoin.it/wiki/Block_hashing_algorithm
	 */
	@JsonSerialize(using = HashSerializer.class)
	public byte[] getBlockHash() {
		BlockHeader hdr = this.getBlockHeader();
		byte[] data = new BitCoinOutput().writeInt(hdr.getVersion()).write(hdr.getPrevHash()).write(getMerkleRoot())
				.writeUnsignedInt(hdr.getTimestamp()).writeUnsignedInt(hdr.getBits()).writeUnsignedInt(hdr.getNonce())
				.toByteArray();
		return Hash.doubleSha256(data);
	}

	/**
	 * Calculate block nonce. https://en.bitcoin.it/wiki/Block_hashing_algorithm
	 */
	public long calculateNonce() {
		System.out.println("Calculate nonce...");
		BlockHeader hdr = this.getBlockHeader();
		int zeros = 3;
		byte[] prefix = new BitCoinOutput().writeInt(hdr.getVersion()).write(hdr.getPrevHash()).write(getMerkleRoot())
				.writeUnsignedInt(hdr.getTimestamp()).writeUnsignedInt(hdr.getBits()).toByteArray();
		long nonce = (-1);
		byte[] blockHash = null;
		long startTime = System.currentTimeMillis();
		for (long tryNonce = 0; tryNonce < 0xffffffffL; tryNonce++) {
			byte[] nonceBytes = new byte[] { (byte) (0xff & tryNonce), (byte) (0xff & (tryNonce >> 8)),
					(byte) (0xff & (tryNonce >> 16)), (byte) (0xff & (tryNonce >> 24)) };
			byte[] data = concat(prefix, nonceBytes);
			byte[] hash = Hash.doubleSha256(data);
			int leadingZeros = 0;
			for (byte b : hash) {
				if (b == 0) {
					leadingZeros++;
				} else {
					break;
				}
			}
			if (leadingZeros >= zeros) {
				nonce = tryNonce;
				blockHash = hash;
				System.out.println();
				break;
			}
			if (tryNonce % 1000000 == 0) {
				System.out.print('.');
			}
		}
		double execTime = (System.currentTimeMillis() - startTime) / 1000.0;
		System.out.printf("Cost %.2f seconds.\n", execTime);
		if (nonce < 0) {
			System.out.println("Nonce not found!");
		} else {
			System.out.println("Found nonce = " + nonce + ", block hash = " + Hash.toHexString(blockHash));
		}
		return nonce;
	}

	@JsonSerialize(using = HashSerializer.class)
	public byte[] getMerkleRoot() {
		Bytes[] hashes = java.util.Arrays.asList(txs).stream().map((tx) -> {
			byte[] bs = tx.getHash();
			return new Bytes(bs);
		}).toArray(Bytes[]::new);
		while (hashes.length > 1) {
			hashes = merkleHash(hashes);
		}
		return hashes[0].data;
	}

	Bytes[] merkleHash(Bytes[] hashes) {
		int count = hashes.length / 2;
		int extra = hashes.length % 2;
		Bytes[] results = new Bytes[count + extra];
		for (int i = 0; i < count; i++) {
			results[i] = new Bytes(Hash.doubleSha256(concat(hashes[2 * i].data, hashes[2 * i + 1].data)));
		}
		if (extra == 1) {
			results[count] = new Bytes(
					Hash.doubleSha256(concat(hashes[hashes.length - 1].data, hashes[hashes.length - 1].data)));
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

	static class Bytes {
		final byte[] data;

		public Bytes(byte[] data) {
			this.data = data;
		}
	}
}
