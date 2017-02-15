package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;

/**
 * Same as OP_EQUAL, but runs OP_VERIFY afterward.
 * 
 * @author liaoxuefeng
 */
public class OpEqualVerify extends Op {

	public OpEqualVerify() {
		super(0x88, "OP_EQUALVERIFY");
	}

	@Override
	public boolean execute(ScriptContext context) {
		boolean r = OpEqual.executeEqual(context);
		if (!r) {
			return false;
		}
		return OpVerify.executeVerify(context);
	}
}
