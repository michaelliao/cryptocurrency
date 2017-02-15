package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;
import com.itranswarp.bitcoin.util.HashUtils;

/**
 * The input is hashed twice: first with SHA-256 and then with RIPEMD-160.
 * 
 * @author liaoxuefeng
 */
public class OpHash160 extends Op {

	public OpHash160() {
		super(0xa9, "OP_HASH160");
	}

	@Override
	public boolean execute(ScriptContext context) {
		byte[] top = context.pop();
		if (top == null) {
			return false;
		}
		byte[] hash = HashUtils.ripeMd160(HashUtils.sha256(top));
		context.push(hash);
		return true;
	}
}
