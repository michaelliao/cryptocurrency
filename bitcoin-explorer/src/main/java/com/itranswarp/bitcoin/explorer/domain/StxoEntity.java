package com.itranswarp.bitcoin.explorer.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Spent tx output.
 * 
 * @author michael
 */
@Entity
public class StxoEntity extends AbstractTxoEntity {

	@Column(nullable = false, updatable = false, length = 2000)
	public String sigScript;

}
