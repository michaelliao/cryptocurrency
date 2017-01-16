package com.itranswarp.bitcoin.explorer.domain;

public class InputBean {

	public String prevOutHash;

	public long prevOutIndex;

	public String sigScript;

	public InputBean(OutputEntity output) {
		this.prevOutHash = output.txoutHash;
		this.prevOutIndex = output.txoutIndex;
		this.sigScript = output.sigScript;
	}

}
