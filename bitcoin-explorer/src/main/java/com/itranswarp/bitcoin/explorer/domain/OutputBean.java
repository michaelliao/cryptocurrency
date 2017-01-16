package com.itranswarp.bitcoin.explorer.domain;

public class OutputBean {

	public long amount;

	public String pkScript;

	public String address;

	public OutputBean(OutputEntity output) {
		this.amount = output.amount;
		this.pkScript = output.pkScript;
		this.address = output.address;
	}

}
