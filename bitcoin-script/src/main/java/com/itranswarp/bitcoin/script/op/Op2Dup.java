package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;

/**
 * Duplicates the top two stack items.
 * 
 * @author liaoxuefeng
 */
public class Op2Dup extends Op {

	public Op2Dup() {
		super(0x6e, "OP_2DUP");
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
		context.push(top1);
		return true;
	}

}
