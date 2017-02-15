package com.itranswarp.bitcoin.script.op;

import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;

/**
 * Removes the top two stack items.
 * 
 * @author liaoxuefeng
 */
public class Op2Drop extends Op {

	public Op2Drop() {
		super(0x6d, "OP_2DROP");
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
		return true;
	}

}
