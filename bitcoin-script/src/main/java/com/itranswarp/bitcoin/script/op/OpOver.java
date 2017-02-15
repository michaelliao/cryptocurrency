package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;

/**
 * Copies the second-to-top stack item to the top.
 * 
 * @author liaoxuefeng
 */
public class OpOver extends Op {

	public OpOver() {
		super(0x78, "OP_OVER");
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
		context.push(top2);
		context.push(top1);
		context.push(top2);
		return true;
	}
}
