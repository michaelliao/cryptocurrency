package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;

/**
 * Marks transaction as invalid if top stack value is not true.
 * 
 * @author liaoxuefeng
 */
public class OpVerify extends Op {

	public OpVerify() {
		super(0x69, "OP_VERIFY");
	}

	@Override
	public boolean execute(ScriptContext context) {
		return executeVerify(context);
	}

	public static boolean executeVerify(ScriptContext context) {
		byte[] top = context.pop();
		return isTrue(top);
	}

	static boolean isTrue(byte[] data) {
		if (data == null || data.length == 0) {
			return false;
		}
		boolean zero = true;
		for (int i = 0; i < data.length; i++) {
			if (data[i] != 0) {
				zero = false;
				break;
			}
		}
		return !zero;
	}
}
