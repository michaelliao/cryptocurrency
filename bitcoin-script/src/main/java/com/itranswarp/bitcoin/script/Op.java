package com.itranswarp.bitcoin.script;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class Op {

	protected final Log log = LogFactory.getLog(getClass());

	private String name;

	public Op(String name) {
		this.name = name;
	}

	public abstract boolean execute(ScriptContext context);

	@Override
	public String toString() {
		return name;
	}
}
