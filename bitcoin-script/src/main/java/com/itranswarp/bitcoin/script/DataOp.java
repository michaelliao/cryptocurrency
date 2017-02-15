package com.itranswarp.bitcoin.script;

import com.itranswarp.bitcoin.util.HashUtils;

/**
 * Data op is executed by script engine directly.
 * 
 * @author liaoxuefeng
 */
public class DataOp extends Op {

	final byte[] data;

	DataOp(int code, byte[] data) {
		super(code, "DATA(" + HashUtils.toHexString(data) + ")");
		this.data = data;
	}

	@Override
	public boolean execute(ScriptContext context) {
		log.info("push data: " + HashUtils.toHexString(data));
		context.push(data);
		return true;
	}
}
