package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;

/**
 * Same as OP_CHECKSIG, but OP_VERIFY is executed afterward.
 * 
 * @author liaoxuefeng
 */
public class OpCheckSigVerify extends Op {

	public OpCheckSigVerify() {
		super(0xad, "OP_CHECKSIGVERIFY");
	}

	@Override
	public boolean execute(ScriptContext context) {
		if (!OpCheckSig.executeCheckSig(context)) {
			return false;
		}
		return OpVerify.executeVerify(context);
	}
}
