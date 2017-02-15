package com.itranswarp.bitcoin.script;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class Op {

	public static final byte[] TRUE = new byte[] { 1 };
	public static final byte[] FALSE = new byte[] { 0 };

	protected final Log log = LogFactory.getLog(getClass());

	public final int code;
	public final String name;

	/**
	 * Op constructor.
	 * 
	 * @param name
	 *            Human-readable name.
	 */
	public Op(int code, String name) {
		this.code = code;
		this.name = name;
	}

	/**
	 * Execute the OP and return true if success.
	 * 
	 * @param context
	 *            ScriptContext object.
	 * @return True if success, otherwise false.
	 */
	public abstract boolean execute(ScriptContext context);

	@Override
	public String toString() {
		return name;
	}
}
