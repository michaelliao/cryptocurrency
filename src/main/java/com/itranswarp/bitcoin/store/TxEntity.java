package com.itranswarp.bitcoin.store;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.itranswarp.crytocurrency.store.AbstractEntity;

@Entity
public class TxEntity extends AbstractEntity {

	@Column(length = 40)
	public String txHash;

	@Column(length = 40)
	public String inputHash;

	@Column(length = 40)
	public String outputHash;

	@Column(length = 64)
	public String blockHash;

	@Column
	public long blockNum;

	@Column
	public long amount;

}
