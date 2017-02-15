package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;

/**
 * Puts the input onto the top of the alt stack. Removes it from the main stack.
 * 
 * @author liaoxuefeng
 */
public class OpToAltStack extends Op {

	public OpToAltStack() {
		super(0x6b, "OP_TOALTSTACK");
	}

	@Override
	public boolean execute(ScriptContext context) {
		throw new UnsupportedOperationException(name);
	}
}
