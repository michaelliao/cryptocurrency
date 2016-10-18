package com.itranswarp.bitcoin.store;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.itranswarp.crytocurrency.store.AbstractEntity;

@Entity
public class BlockEntity extends AbstractEntity {

	@Column(length = 64)
	public String blockHash;

	@Column
	public long blockNum;

}
