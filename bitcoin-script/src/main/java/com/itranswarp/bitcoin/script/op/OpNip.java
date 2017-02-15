package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;

/**
 * Removes the second-to-top stack item.
 * 
 * @author liaoxuefeng
 */
public class OpNip extends Op {

	public OpNip() {
		super(0x77, "OP_NIP");
	}

	@Override
	public boolean execute(ScriptContext context) {
		byte[] top1 = context.pop();
		if (top1 == null) {
			return false;
		}
		byte[] top2 = context.pop();
		if (top2 == null) {
			return false;
		}
		context.push(top1);
		return true;
	}
}
