package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;
import com.itranswarp.bitcoin.util.BytesUtils;

/**
 * Returns 1 if the inputs are exactly equal, 0 otherwise.
 * 
 * @author liaoxuefeng
 */
public class OpEqual extends Op {

	public OpEqual() {
		super(0x87, "OP_EQUAL");
	}

	static boolean executeEqual(ScriptContext context) {
		byte[] b1 = context.pop();
		if (b1 == null) {
			return false;
		}
		byte[] b2 = context.pop();
		if (b2 == null) {
			return false;
		}
		boolean eq = BytesUtils.equals(b1, b2);
		context.push(eq ? TRUE : FALSE);
		return true;
	}

	@Override
	public boolean execute(ScriptContext context) {
		return executeEqual(context);
	}
}
