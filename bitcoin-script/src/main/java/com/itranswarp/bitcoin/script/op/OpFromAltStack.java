package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;

/**
 * Puts the input onto the top of the main stack. Removes it from the alt stack.
 * 
 * @author liaoxuefeng
 */
public class OpFromAltStack extends Op {

	public OpFromAltStack() {
		super(0x6c, "OP_FROMALTSTACK");
	}

	@Override
	public boolean execute(ScriptContext context) {
		throw new UnsupportedOperationException(name);
	}
}
