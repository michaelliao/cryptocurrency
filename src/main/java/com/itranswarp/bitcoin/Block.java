package com.itranswarp.bitcoin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itranswarp.cryptocurrency.common.Hash;
import com.itranswarp.cryptocurrency.common.HashSerializer;
import com.itranswarp.cryptocurrency.common.LittleEndianDataInputStream;
import com.itranswarp.cryptocurrency.common.LittleEndianDataOutputStream;

public class Block {

	int size;
	BlockHeader blockHeader;
	Transaction[] txs;

	public Block(LittleEndianDataInputStream input) throws IOException {
		// read magic number: d9b4bef9
		int magic = input.readInt();
		if (magic != 0xd9b4bef9) {
			throw new RuntimeException("Bad magic number.");
		}
		// read block size:
		this.size = input.readInt();
		this.blockHeader = new BlockHeader(input);
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
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		try (LittleEndianDataOutputStream output = new LittleEndianDataOutputStream(byteOutput)) {
			BlockHeader hdr = this.getBlockHeader();
			output.writeInt(hdr.getVersion());
			output.write(hdr.getPrevHash());
			output.write(getMerkleRoot());
			output.writeUnsignedInt(hdr.getTimestamp());
			output.writeUnsignedInt(hdr.getBits());
			output.writeUnsignedInt(hdr.getNonce());
			return Hash.doubleSha256(byteOutput.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Calculate block nonce. https://en.bitcoin.it/wiki/Block_hashing_algorithm
	 */
	public long calculateNonce() {
		System.out.println("Calculate nonce...");
		BlockHeader hdr = this.getBlockHeader();
		int zeros = 3;
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		try (LittleEndianDataOutputStream output = new LittleEndianDataOutputStream(byteOutput)) {
			output.writeInt(hdr.getVersion());
			output.write(hdr.getPrevHash());
			output.write(getMerkleRoot());
			output.writeUnsignedInt(hdr.getTimestamp());
			output.writeUnsignedInt(hdr.getBits());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		byte[] prefix = byteOutput.toByteArray();
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
		do {
			hashes = merkleHash(hashes);
		} while (hashes.length > 1);
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
