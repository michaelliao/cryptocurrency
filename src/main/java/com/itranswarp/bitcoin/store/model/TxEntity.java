package com.itranswarp.bitcoin.store.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.itranswarp.bitcoin.store.AbstractEntity;

@Entity
public class TxEntity extends AbstractEntity {

	@Id
	@Column(length = HASH32_LENGTH)
	public String txHash;

	@Column(nullable = false, length = HASH32_LENGTH)
	public String blockHash;

	@Column(nullable = false)
	public int inputs;

	@Column(nullable = false)
	public int outputs;

	@Column(nullable = false)
	public int version;

	@Column(nullable = false)
	public long inputValue;

	@Column(nullable = false)
	public long outputValue;

	@Column(nullable = false)
	public long lockTime;

	@Column(nullable = false, length = 65535)
	public String payload;

	@Override
	public String getId() {
		return this.txHash;
	}

	public long getFee() {
		return this.outputValue - this.inputValue;
	}
}
