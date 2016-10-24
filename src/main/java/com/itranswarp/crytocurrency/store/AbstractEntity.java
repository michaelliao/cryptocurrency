package com.itranswarp.crytocurrency.store;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

	protected static final int HASH32_LENGTH = 64;

	public abstract String getId();
}
