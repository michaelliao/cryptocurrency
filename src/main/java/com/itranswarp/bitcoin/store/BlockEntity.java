package com.itranswarp.bitcoin.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.itranswarp.crytocurrency.store.AbstractEntity;

@Entity
public class BlockEntity extends AbstractEntity {

	@Id
	@Column(length = HASH32_LENGTH)
	public String blockHash;

	@Column(length = HASH32_LENGTH, unique = true)
	public String previousHash;

	@Column(length = HASH32_LENGTH, unique = true)
	public String merkleHash;

	@Column(unique = true)
	public long blockHeight;

	@Column
	public long numOfTx;

	@Column
	public long timestamp;

	@Column
	public long difficulty;

	@Column
	public long bits;

	@Column
	public long nonce;

	@Override
	public String getId() {
		return this.blockHash;
	}

}
