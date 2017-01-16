package com.itranswarp.bitcoin.explorer.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "block", uniqueConstraints = { @UniqueConstraint(name = "uni_prev_hash", columnNames = "prevHash"),
		@UniqueConstraint(name = "uni_height", columnNames = "height") })
public class BlockEntity {

	@JsonProperty("hash")
	@Id
	@Column(nullable = false, updatable = false, length = EntityConstants.HASH_LENGTH)
	public String blockHash;

	@Column(nullable = false, updatable = false, length = EntityConstants.HASH_LENGTH)
	public String prevHash;

	@Column(nullable = false, updatable = false, length = EntityConstants.HASH_LENGTH)
	public String merkleHash;

	/**
	 * Epoch time in seconds.
	 */
	@Column(nullable = false, updatable = false)
	public long timestamp;

	@JsonProperty("n_tx")
	@Column(nullable = false, updatable = false)
	public long txCount;

	@Column(nullable = false, updatable = false)
	public long bits;

	@Column(nullable = false, updatable = false)
	public long nonce;

	@JsonProperty("ver")
	@Column(nullable = false, updatable = false)
	public long version;

	@Column(nullable = false, updatable = false)
	public long height;

	@Column(nullable = false, updatable = false)
	public long size;

	@Transient
	public List<TxEntity> txs;
}
