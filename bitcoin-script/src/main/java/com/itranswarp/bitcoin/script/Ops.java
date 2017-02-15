package com.itranswarp.bitcoin.script;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.bitcoin.BitcoinException;
import com.itranswarp.bitcoin.util.ClasspathUtils;

/**
 * Script ops: https://en.bitcoin.it/wiki/Script
 * 
 * @author liaoxuefeng
 */
public class Ops {

	final Log Log = LogFactory.getLog(getClass());

	public static Op getOp(Integer code) {
		return OPS.get(code);
	}

	// holds all ops:
	static final Map<Integer, Op> OPS = scanOps();

	static Map<Integer, Op> scanOps() {
		Map<Integer, Op> map = new HashMap<>();
		try {
			for (Class<?> clazz : ClasspathUtils.getClasses(Ops.class.getPackage().getName() + ".op")) {
				Op op = (Op) clazz.newInstance();
				map.put(op.code, op);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new BitcoinException(e);
		}
		return map;
	}

}
