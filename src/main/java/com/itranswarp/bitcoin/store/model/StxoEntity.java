package com.itranswarp.bitcoin.store.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.itranswarp.bitcoin.store.AbstractEntity;

@Entity
public class StxoEntity extends AbstractEntity {

	/**
	 * STXO id like
	 * "591e91f809d716912ca1d4a9295e70c3e78bab077683f79350f101da64588073#1"
	 */
	@Id
	@Column(length = HASH32_LENGTH + 10)
	public String txHash;

	@Column(nullable = false, length = HASH32_LENGTH)
	public String blockHash;

	@Column(nullable = false)
	public long amount;

	@Override
	public String getId() {
		return this.txHash;
	}

}
