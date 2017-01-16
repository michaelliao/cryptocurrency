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
@Table(name = "output", indexes = { @Index(name = "idx_txout_hash", columnList = "txoutHash"),
		@Index(name = "idx_txin_hash", columnList = "txinHash"), @Index(name = "idx_address", columnList = "address") })
public class OutputEntity {

	/**
	 * Output hash = txout hash + '#' + txout index: xxx...xxx#12
	 */
	@Id
	@Column(nullable = false, updatable = false, length = EntityConstants.HASH_LENGTH + 10)
	public String outputHash;

	/**
	 * Tx out hash as reference.
	 */
	@Column(nullable = false, updatable = false, length = EntityConstants.HASH_LENGTH)
	public String txoutHash;

	/**
	 * index starts from 0.
	 */
	@Column(nullable = false, updatable = false)
	public long txoutIndex;

	/**
	 * Spent tx (tx-in) hash as reference.
	 */
	@Column(nullable = false, length = EntityConstants.HASH_LENGTH)
	public String txinHash;

	/**
	 * index starts from 0.
	 */
	@Column(nullable = false)
	public long txinIndex;

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
	@Column(nullable = false, length = 5000)
	public String sigScript;

	/**
	 * Is this output spent?
	 */
	@Transient
	public boolean isSpent() {
		return txinHash != null && !txinHash.isEmpty();
	}

}
