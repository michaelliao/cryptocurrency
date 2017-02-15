package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;

/**
 * The top two items on the stack are swapped.
 * 
 * @author liaoxuefeng
 */
public class OpSwap extends Op {

	public OpSwap() {
		super(0x7c, "OP_SWAP");
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
		context.push(top2);
		return true;
	}
}
