package com.itranswarp.bitcoin.store.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.itranswarp.bitcoin.store.AbstractEntity;

@Entity
public class BlockEntity extends AbstractEntity {

	@Id
	@Column(length = HASH32_LENGTH)
	public String blockHash;

	@Column(nullable = false, length = HASH32_LENGTH, unique = true)
	public String previousHash;

	@Column(nullable = false, length = HASH32_LENGTH, unique = true)
	public String merkleHash;

	@Column(nullable = false, unique = true)
	public long blockHeight;

	@Column(nullable = false)
	public long numOfTx;

	@Column(nullable = false)
	public long timestamp;

	@Column(nullable = false)
	public long difficulty;

	@Column(nullable = false)
	public long bits;

	@Column(nullable = false)
	public long nonce;

	@Override
	public String getId() {
		return this.blockHash;
	}

}
