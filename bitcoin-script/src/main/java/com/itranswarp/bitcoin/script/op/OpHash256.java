package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;
import com.itranswarp.bitcoin.util.HashUtils;

/**
 * The input is hashed two times with SHA-256.
 * 
 * @author liaoxuefeng
 */
public class OpHash256 extends Op {

	public OpHash256() {
		super(0xaa, "OP_HASH256");
	}

	@Override
	public boolean execute(ScriptContext context) {
		byte[] top = context.pop();
		if (top == null) {
			return false;
		}
		byte[] hash = HashUtils.doubleSha256(top);
		context.push(hash);
		return true;
	}
}
