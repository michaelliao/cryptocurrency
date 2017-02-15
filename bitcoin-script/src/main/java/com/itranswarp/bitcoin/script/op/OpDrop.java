package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;

/**
 * Removes the top stack item.
 * 
 * @author liaoxuefeng
 */
public class OpDrop extends Op {

	public OpDrop() {
		super(0x75, "OP_DROP");
	}

	@Override
	public boolean execute(ScriptContext context) {
		byte[] top = context.pop();
		if (top == null) {
			return false;
		}
		return true;
	}
}
