package com.itranswarp.bitcoin.script;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.bitcoin.constant.BitcoinConstants;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.bitcoin.struct.Transaction;
import com.itranswarp.bitcoin.struct.TxIn;
import com.itranswarp.bitcoin.struct.TxOut;
import com.itranswarp.bitcoin.util.BytesUtils;
import com.itranswarp.bitcoin.util.HashUtils;
import com.itranswarp.bitcoin.util.Secp256k1Utils;

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

	static final byte[] TRUE = new byte[] { 1 };
	static final byte[] FALSE = new byte[] { 0 };

	static final Map<Integer, Op> OPS;

	static {
		Map<Integer, Op> map = new HashMap<>();

		map.put(0x61, new Op("OP_NOP") {
			@Override
			public boolean execute(ScriptContext context) {
				return false;
			}
		});
		map.put(0x69, new Op("OP_VERIFY") {
			@Override
			public boolean execute(ScriptContext context) {
				byte[] top = context.pop();
				if (top == null) {
					return false;
				}
				return BytesUtils.equals(top, TRUE);
			}
		});

		map.put(0x6b, new Op("OP_TOALTSTACK") {
			@Override
			public boolean execute(ScriptContext context) {
				return false;
			}
		});

		map.put(0x6c, new Op("OP_FROMALTSTACK") {
			@Override
			public boolean execute(ScriptContext context) {
				return false;
			}
		});

		map.put(0x76, new Op("OP_DUP") {
			@Override
			public boolean execute(ScriptContext context) {
				byte[] top = context.pop();
				if (top == null) {
					return false;
				}
				context.push(top);
				context.push(top);
				return true;
			}
		});

		map.put(0x87, new Op("OP_EQUAL") {
			@Override
			public boolean execute(ScriptContext context) {
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
		});

		map.put(0x88, new Op("OP_EQUALVERIFY") {
			@Override
			public boolean execute(ScriptContext context) {
				byte[] b1 = context.pop();
				if (b1 == null) {
					return false;
				}
				byte[] b2 = context.pop();
				if (b2 == null) {
					return false;
				}
				return BytesUtils.equals(b1, b2);
			}
		});

		map.put(0xa9, new Op("OP_HASH160") {
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
		});

		map.put(0xaa, new Op("OP_HASH256") {
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
		});

		map.put(0xac, new Op("OP_CHECKSIG") {
			@Override
			public boolean execute(ScriptContext context) {
				byte[] pkData = context.pop();
				if (pkData == null) {
					return false;
				}
				byte[] sigData = context.pop();
				if (sigData == null || sigData.length < 1) {
					return false;
				}
				byte[] sig = Arrays.copyOfRange(sigData, 0, sigData.length - 1);
				byte sigType = sigData[sigData.length - 1];
				byte[] message = null;
				switch (sigType) {
				case BitcoinConstants.SIGHASH_ALL:
					message = sigHashAll(context);
					break;
				default:
					log.warn("Unsupported sighash: " + sigType);
					return false;
				}
				log.info("payload: " + HashUtils.toHexString(message));
				byte[] msgHash = HashUtils.doubleSha256(message);
				log.info("payload hash: " + HashUtils.toHexString(msgHash));
				log.info("public key: " + HashUtils.toHexString(pkData));
				log.info("sig: " + sig.length + ": " + HashUtils.toHexString(sig));
				return Secp256k1Utils.verify(msgHash, sig, pkData);
			}
		});

		map.put(0000, new Op("???") {
			@Override
			public boolean execute(ScriptContext context) {
				return false;
			}
		});

		OPS = map;
	}

	static byte[] sigHashAll(ScriptContext ctx) {
		Transaction tx = ctx.getTransaction();
		int index = ctx.getTxInIndex();
		BitcoinOutput buffer = new BitcoinOutput();
		buffer.writeInt(tx.version);
		buffer.writeVarInt(tx.tx_ins.length);
		for (int i = 0; i < tx.tx_ins.length; i++) {
			TxIn in = tx.tx_ins[i];
			buffer.write(in.previousOutput.toByteArray());
			if (i == index) {
				// replace sigScript with output script:
				String txHash = HashUtils.toHexStringAsLittleEndian(in.previousOutput.hash);
				long utxoIndex = in.previousOutput.index;
				TxOut out = ctx.getUTXO(txHash, utxoIndex);
				buffer.writeVarInt(out.pk_script.length);
				buffer.write(out.pk_script);
			} else {
				buffer.writeVarInt(0);
			}
			// don't forget sequence:
			buffer.writeUnsignedInt(in.sequence);
		}
		buffer.writeVarInt(tx.tx_outs.length);
		for (int i = 0; i < tx.tx_outs.length; i++) {
			buffer.write(tx.tx_outs[i].toByteArray());
		}
		buffer.writeUnsignedInt(tx.lock_time);
		// append SIGHASH_ALL as uint:
		buffer.writeUnsignedInt(BitcoinConstants.SIGHASH_ALL);
		return buffer.toByteArray();
	}
}
