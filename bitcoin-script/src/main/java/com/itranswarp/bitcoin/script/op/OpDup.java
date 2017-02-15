package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;

/**
 * Duplicates the top stack item.
 * 
 * @author liaoxuefeng
 */
public class OpDup extends Op {

	public OpDup() {
		super(0x76, "OP_DUP");
	}

	@Override
	public boolean execute(ScriptContext context) {
		byte[] top = context.pop();
		if (top == null) {
			return false;
		}
		context.push(top);
		context.push(top);
		return true;
	}
}
