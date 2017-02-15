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

	// holds all ops:
	static final Map<Integer, Op> OPS;

	static {
		Map<Integer, Op> map = new HashMap<>();

		// Does nothing:
		map.put(0x61, new Op("OP_NOP") {
			@Override
			public boolean execute(ScriptContext context) {
				return true;
			}
		});

		// Marks transaction as invalid if top stack value is not true:
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

		// Marks transaction as invalid:
		map.put(0x6a, new Op("OP_RETURN") {
			@Override
			public boolean execute(ScriptContext context) {
				return false;
			}
		});

		// Puts the input onto the top of the alt stack. Removes it from the
		// main stack:
		map.put(0x6b, new Op("OP_TOALTSTACK") {
			@Override
			public boolean execute(ScriptContext context) {
				throw new UnsupportedOperationException(this.name);
			}
		});

		// Puts the input onto the top of the main stack. Removes it from the
		// alt stack:
		map.put(0x6c, new Op("OP_FROMALTSTACK") {
			@Override
			public boolean execute(ScriptContext context) {
				throw new UnsupportedOperationException(this.name);
			}
		});

		// Removes the top two stack items:
		map.put(0x6d, new Op("OP_2DROP") {
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
		});

		// Duplicates the top two stack items:
		map.put(0x6e, new Op("OP_2DUP") {
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
				context.push(top2);
				context.push(top1);
				context.push(top2);
				context.push(top1);
				return true;
			}
		});

		// Duplicates the top three stack items:
		map.put(0x6f, new Op("OP_3DUP") {
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
		});

		// Removes the top stack item:
		map.put(0x75, new Op("OP_DROP") {
			@Override
			public boolean execute(ScriptContext context) {
				byte[] top = context.pop();
				if (top == null) {
					return false;
				}
				return true;
			}
		});

		// Duplicates the top stack item:
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

		// Removes the second-to-top stack item:
		map.put(0x77, new Op("OP_NIP") {
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
				context.push(top1);
				return true;
			}
		});

		// Copies the second-to-top stack item to the top:
		map.put(0x78, new Op("OP_OVER") {
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
				context.push(top2);
				context.push(top1);
				context.push(top2);
				return true;
			}
		});

		// The top two items on the stack are swapped:
		map.put(0x7c, new Op("OP_SWAP") {
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
				context.push(top1);
				context.push(top2);
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

		// The input is hashed twice: first with SHA-256 and then with
		// RIPEMD-160:
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

		// The input is hashed two times with SHA-256:
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
					log.warn("Unsupported sighash for this script engine: " + sigType);
					return false;
				}
				log.info("payload: " + HashUtils.toHexString(message));
				byte[] msgHash = HashUtils.doubleSha256(message);
				log.info("payload hash: " + HashUtils.toHexString(msgHash));
				log.info("public key: " + HashUtils.toHexString(pkData));
				log.info("sig: " + sig.length + ": " + HashUtils.toHexString(sig));
				boolean r = Secp256k1Utils.verify(msgHash, sig, pkData);
				context.push(r ? TRUE : FALSE);
				return true;
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
