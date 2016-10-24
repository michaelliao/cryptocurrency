package com.itranswarp.bitcoin.store;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.itranswarp.crytocurrency.store.AbstractEntity;

@Entity
public class UtxoEntity extends AbstractEntity {

	@Column(length = 40)
	public String utxoHash;

	@Column(length = 32)
	public String blockId;

	@Column
	public long amount;

	@Override
	public String getId() {
		return this.utxoHash;
	}

}
