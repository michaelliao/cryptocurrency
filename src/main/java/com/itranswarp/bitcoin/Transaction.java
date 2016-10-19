package com.itranswarp.bitcoin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itranswarp.bitcoin.io.BitCoinBlockDataInput;
import com.itranswarp.bitcoin.io.BitCoinBlockDataOutput;
import com.itranswarp.cryptocurrency.common.Hash;
import com.itranswarp.cryptocurrency.common.HashSerializer;
import com.itranswarp.cryptocurrency.common.LockTimeSerializer;

public class Transaction {

	int version; // int32, transaction data format version (note, this is
					// signed)
	long tx_in_count; // var_int, number of Transaction inputs
	TxIn[] tx_ins; // a list of 1 or more transaction inputs or sources for
					// coins
	long tx_out_count; // var_int, number of Transaction outputs
	TxOut[] tx_outs; // a list of 1 or more transaction outputs or destinations
						// for coins
	long lock_time; // uint32_t, the block number or timestamp at which this
					// transaction is unlocked:
	// 0 Not locked
	// < 500000000 Block number at which this transaction is unlocked
	// >= 500000000 UNIX timestamp at which this transaction is unlocked
	// If all TxIn inputs have final (0xffffffff) sequence numbers then
	// lock_time is irrelevant. Otherwise, the transaction may not be added to a
	// block until after lock_time (see NLockTime).

	public Transaction(BitCoinBlockDataInput input) throws IOException {
		this.version = input.readInt();
		this.tx_in_count = input.readVarInt();
		this.tx_ins = new TxIn[(int) this.tx_in_count];
		for (int i = 0; i < tx_ins.length; i++) {
			this.tx_ins[i] = new TxIn(input);
		}
		this.tx_out_count = input.readVarInt();
		this.tx_outs = new TxOut[(int) this.tx_out_count];
		for (int i = 0; i < tx_outs.length; i++) {
			this.tx_outs[i] = new TxOut(input);
		}
		this.lock_time = input.readUnsignedInt();
	}

	@JsonSerialize(using = HashSerializer.class)
	public byte[] getHash() {
		byte[] buffer = null;
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		try (BitCoinBlockDataOutput output = new BitCoinBlockDataOutput(byteOutput)) {
			this.dump(output);
			buffer = byteOutput.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return Hash.doubleSha256(buffer);
	}

	public void dump(BitCoinBlockDataOutput output) throws IOException {
		output.writeInt(version);
		output.writeVarInt(tx_in_count);
		for (int i = 0; i < tx_ins.length; i++) {
			tx_ins[i].dump(output);
		}
		output.writeVarInt(tx_out_count);
		for (int i = 0; i < tx_outs.length; i++) {
			tx_outs[i].dump(output);
		}
		output.writeUnsignedInt(lock_time);
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getTx_in_count() {
		return tx_in_count;
	}

	public void setTx_in_count(long tx_in_count) {
		this.tx_in_count = tx_in_count;
	}

	public TxIn[] getTx_ins() {
		return tx_ins;
	}

	public void setTx_ins(TxIn[] tx_ins) {
		this.tx_ins = tx_ins;
	}

	public long getTx_out_count() {
		return tx_out_count;
	}

	public void setTx_out_count(long tx_out_count) {
		this.tx_out_count = tx_out_count;
	}

	public TxOut[] getTx_outs() {
		return tx_outs;
	}

	public void setTx_outs(TxOut[] tx_outs) {
		this.tx_outs = tx_outs;
	}

	@JsonSerialize(using = LockTimeSerializer.class)
	public long getLock_time() {
		return lock_time;
	}

	public void setLock_time(long lock_time) {
		this.lock_time = lock_time;
	}

}
