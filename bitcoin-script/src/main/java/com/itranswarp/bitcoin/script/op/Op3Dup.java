package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;

/**
 * Duplicates the top three stack items.
 * 
 * @author liaoxuefeng
 */
public class Op3Dup extends Op {

	public Op3Dup() {
		super(0x6f, "OP_3DUP");
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
		byte[] top3 = context.pop();
		if (top3 == null) {
			return false;
		}
		context.push(top3);
		context.push(top2);
		context.push(top1);
		context.push(top3);
		context.push(top2);
		context.push(top1);
		return true;
	}

}
