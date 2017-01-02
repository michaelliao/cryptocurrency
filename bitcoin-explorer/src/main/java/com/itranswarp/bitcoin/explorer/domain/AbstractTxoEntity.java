package com.itranswarp.bitcoin.explorer.domain;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public class AbstractTxoEntity {

	/**
	 * Tx output hash: 000...000#12
	 */
	@Id
	@Column(nullable = false, updatable = false, length = EntityConstants.HASH_LENGTH + 10)
	public String txoHash;

	@Column(nullable = false, updatable = false)
	public long amount;

	@Column(nullable = false, updatable = false, length = 1000)
	public String pkScript;

	@Transient
	public String getTxHash() {
		return this.txoHash.substring(0, EntityConstants.HASH_LENGTH);
	}

	@Transient
	public int getIndex() {
		return Integer.parseInt(this.txoHash.substring(EntityConstants.HASH_LENGTH + 1));
	}

}
