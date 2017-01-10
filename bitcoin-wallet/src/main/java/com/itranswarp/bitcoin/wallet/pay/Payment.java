package com.itranswarp.bitcoin.wallet.pay;

import java.util.ArrayList;
import java.util.List;

import com.itranswarp.bitcoin.struct.Transaction;
import com.itranswarp.bitcoin.struct.TxOut;

public class Payment {

	List<TxOut> outs = new ArrayList<>();
	List<PayTo> pays = new ArrayList<>();
	
	public Payment() {
		//
	}

	public Payment alloc(Transaction tx, int outputIndex) {
	TxOut out=	tx.tx_outs[outputIndex];
		return this;
	}

	public Payment payTo(byte[] address, long amount) {
		
		return this;
	}

	public byte[] pay() {
		//
		return null;
	}
}

class PayTo {
	final byte[] address;
	final long amount;

	public PayTo(byte[] address, long amount) {
 		this.address = address;
		this.amount = amount;
	}
}
