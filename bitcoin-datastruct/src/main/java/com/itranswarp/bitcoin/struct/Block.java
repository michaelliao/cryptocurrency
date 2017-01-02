package com.itranswarp.bitcoin.struct;

public class Block {

	public Header header;

	public long txCount;

	public Transaction[] txs;

	public String getBlockHash() {
		return "";
	}
}
