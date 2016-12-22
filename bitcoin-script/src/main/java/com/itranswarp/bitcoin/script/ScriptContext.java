package com.itranswarp.bitcoin.script;

import java.util.Map;

import com.itranswarp.bitcoin.struct.Transaction;
import com.itranswarp.bitcoin.struct.TxOut;

public interface ScriptContext {

	void push(byte[] push);

	byte[] pop();

	Transaction getTransaction();

	/**
	 * Get previous TxOut as map. The key is composed by: "tx-hash#index".
	 */
	Map<String, TxOut> getPreviousTxOutAsMap();

	int getTxInIndex();
}
