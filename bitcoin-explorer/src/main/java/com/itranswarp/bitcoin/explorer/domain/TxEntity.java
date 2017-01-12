package com.itranswarp.bitcoin.explorer.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(indexes = @Index(name = "idx_block_hash", columnList = "blockHash"))
public class TxEntity {

	@Id
	@Column(nullable = false, updatable = false, length = EntityConstants.HASH_LENGTH)
	public String txHash;

	@Column(nullable = false, updatable = false, length = EntityConstants.HASH_LENGTH)
	public String blockHash;

	@Column(nullable = false, updatable = false)
	public long txIndex;

	@Column(nullable = false, updatable = false)
	public long inputCount;

	@Column(nullable = false, updatable = false)
	public long outputCount;

	@Column(nullable = false, updatable = false)
	public long totalInput;

	@Column(nullable = false, updatable = false)
	public long totalOutput;

	@Column(nullable = false, updatable = false)
	public long lockTime;

	@Column(nullable = false, updatable = false)
	public long version;

	@Transient
	public boolean isCoinBase() {
		return this.inputCount == 0;
	}

	@Transient
	public long fees() {
		if (isCoinBase()) {
			return 0;
		}
		return totalOutput - totalInput;
	}
}
