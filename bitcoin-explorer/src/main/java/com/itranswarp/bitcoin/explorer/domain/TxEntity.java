package com.itranswarp.bitcoin.explorer.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TxEntity {

	@Id
	@Column(nullable = false, updatable = false, length = EntityConstants.HASH_LENGTH)
	public String txHash;

	@Column(nullable = false, updatable = false)
	public long inputCount;

	@Column(nullable = false, updatable = false)
	public long outputCount;

	@Column(nullable = false, updatable = false)
	public long lockTime;

	@Column(nullable = false, updatable = false)
	public long version;

	@Column(nullable = false, updatable = false)
	public long size;
}
