package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;

/**
 * Marks transaction as invalid.
 * 
 * @author liaoxuefeng
 */
public class OpReturn extends Op {

	public OpReturn() {
		super(0x6a, "OP_RETURN");
	}

	@Override
	public boolean execute(ScriptContext context) {
		return false;
	}
}
