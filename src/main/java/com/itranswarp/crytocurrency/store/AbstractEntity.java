package com.itranswarp.crytocurrency.store;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractEntity implements Serializable {

	protected static final int ID_LENGTH = 16;

	@Id
	@Column(length = ID_LENGTH)
	public String id;

}
