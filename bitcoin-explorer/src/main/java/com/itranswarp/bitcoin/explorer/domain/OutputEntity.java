package com.itranswarp.bitcoin.explorer.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Store transaction output.
 * 
 * An unspent output is stores as spent = false and sigScript = "".
 * 
 * A spent output is stores as spent = true and sigScript = "xxxxxx".
 * 
 * @author liaoxuefeng
 */
@Entity
@Table(name = "output", indexes = { @Index(name = "idx_address", columnList = "address") })
public class OutputEntity {

	/**
	 * Tx output hash and index: xxx...xxx#12
	 */
	@Id
	@Column(nullable = false, updatable = false, length = EntityConstants.HASH_LENGTH + 10)
	public String txoHash;

	@Column(nullable = false, updatable = false)
	public long amount;

	/**
	 * The pkScript stores as hex string.
	 */
	@Column(nullable = false, updatable = false, length = 10000)
	public String pkScript;

	/**
	 * Address from parsed script.
	 */
	@Column(nullable = false, updatable = false, length = 100)
	public String address;

	/**
	 * The sigScript stores as hex string.
	 */
	@Column(nullable = false, updatable = true, length = 5000)
	public String sigScript;

	/**
	 * Is this output spent?
	 */
	@Column(nullable = false, updatable = true)
	public boolean spent;

	@Transient
	public String getTxHash() {
		return this.txoHash.substring(0, EntityConstants.HASH_LENGTH);
	}

	@Transient
	public int getIndex() {
		return Integer.parseInt(this.txoHash.substring(EntityConstants.HASH_LENGTH + 1));
	}

}
