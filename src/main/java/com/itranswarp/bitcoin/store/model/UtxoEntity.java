package com.itranswarp.bitcoin.store.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.itranswarp.bitcoin.store.AbstractEntity;

@Entity
public class UtxoEntity extends AbstractEntity {

	@Id
	@Column(length = 40)
	public String txHash;

	@Column(length = 32)
	public String blockId;

	@Column
	public long amount;

	@Override
	public String getId() {
		return this.txHash;
	}

}
