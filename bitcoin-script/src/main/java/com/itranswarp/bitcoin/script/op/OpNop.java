package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;

/**
 * Does nothing.
 * 
 * @author liaoxuefeng
 */
public class OpNop extends Op {

	public OpNop() {
		super(0x61, "OP_NOP");
	}

	@Override
	public boolean execute(ScriptContext context) {
		return true;
	}
}
