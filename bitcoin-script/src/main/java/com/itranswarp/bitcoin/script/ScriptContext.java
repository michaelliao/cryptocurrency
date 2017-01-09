package com.itranswarp.bitcoin.script;

import com.itranswarp.bitcoin.struct.Transaction;
import com.itranswarp.bitcoin.struct.TxOut;

public interface ScriptContext {

	void push(byte[] push);

	byte[] pop();

	Transaction getTransaction();

	/**
	 * Get previous UTXO.
	 */
	TxOut getUTXO(String txHash, long index);

	int getTxInIndex();
}
