package com.itranswarp.bitcoin.explorer.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BlockEntity {

	@Id
	@Column(nullable = false, updatable = false, length = EntityConstants.HASH_LENGTH)
	public String blockHash;

	@Column(nullable = false, updatable = false, unique = true, length = EntityConstants.HASH_LENGTH)
	public String prevHash;

	@Column(nullable = false, updatable = false, unique = true, length = EntityConstants.HASH_LENGTH)
	public String merkleHash;

	@Column(nullable = false, updatable = false)
	public long timestamp;

	@Column(nullable = false, updatable = false)
	public long bits;

	@Column(nullable = false, updatable = false)
	public long nonce;

	@Column(nullable = false, updatable = false)
	public long version;

	@Column(nullable = false, updatable = false, unique = true)
	public long height;

	@Column(nullable = false, updatable = false)
	public long size;
}
