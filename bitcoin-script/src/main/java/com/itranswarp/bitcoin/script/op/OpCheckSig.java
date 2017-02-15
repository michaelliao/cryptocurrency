package com.itranswarp.bitcoin.script.op;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.bitcoin.constant.BitcoinConstants;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.bitcoin.script.Op;
import com.itranswarp.bitcoin.script.ScriptContext;
import com.itranswarp.bitcoin.struct.Transaction;
import com.itranswarp.bitcoin.struct.TxIn;
import com.itranswarp.bitcoin.struct.TxOut;
import com.itranswarp.bitcoin.util.HashUtils;
import com.itranswarp.bitcoin.util.Secp256k1Utils;

/**
 * The entire transaction's outputs, inputs, and script (from the most
 * recently-executed OP_CODESEPARATOR to the end) are hashed. The signature used
 * by OP_CHECKSIG must be a valid signature for this hash and public key. If it
 * is, 1 is returned, 0 otherwise.
 * 
 * @author liaoxuefeng
 */
public class OpCheckSig extends Op {

	static final Log slog = LogFactory.getLog(OpCheckSig.class);

	public OpCheckSig() {
		super(0xac, "OP_CHECKSIG");
	}

	@Override
	public boolean execute(ScriptContext context) {
		return executeCheckSig(context);
	}

	public static boolean executeCheckSig(ScriptContext context) {
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
			slog.warn("Unsupported sighash for this script engine: " + sigType);
			return false;
		}
		byte[] msgHash = HashUtils.doubleSha256(message);
		slog.info("payload hash: " + HashUtils.toHexString(msgHash));
		slog.info("public key: " + HashUtils.toHexString(pkData));
		slog.info("sig: " + sig.length + ": " + HashUtils.toHexString(sig));
		boolean r = Secp256k1Utils.verify(msgHash, sig, pkData);
		context.push(r ? TRUE : FALSE);
		return true;
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
