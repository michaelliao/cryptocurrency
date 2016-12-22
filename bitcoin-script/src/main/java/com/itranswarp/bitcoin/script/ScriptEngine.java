package com.itranswarp.bitcoin.script;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.struct.Transaction;
import com.itranswarp.bitcoin.struct.TxOut;
import com.itranswarp.bitcoin.util.BytesUtils;
import com.itranswarp.bitcoin.util.HashUtils;

public class ScriptEngine {

	static final Log log = LogFactory.getLog(ScriptEngine.class);

	private final List<Op> ops;

	ScriptEngine(List<Op> ops) {
		this.ops = ops;
	}

	/**
	 * Execute the script.
	 */
	public boolean execute(Transaction currentTx, int txInIndex, Map<String, TxOut> prevUtxos) {
		log.info("execute script...");
		ScriptContext context = new ScriptContextImpl(currentTx, txInIndex, prevUtxos);
		for (Op op : this.ops) {
			log.info("> " + op);
			if (!op.execute(context)) {
				return false;
			}
			log.info("ok");
		}
		return true;
	}

	void printOp(Op op, Deque<byte[]> stack) {
		log.info("exec: " + op);
		stack.forEach((data) -> {
			log.info("  " + HashUtils.toHexString(data));
		});
	}

	/**
	 * Parse BitCoin script: https://en.bitcoin.it/wiki/Script
	 */
	public static ScriptEngine parse(byte[] sigScript, byte[] outScript) {
		int n = 0;
		List<Op> list = new ArrayList<>();
		try (BitcoinInput input = new BitcoinInput(new ByteArrayInputStream(BytesUtils.concat(sigScript, outScript)))) {
			while ((n = input.read()) != (-1)) {
				if (n >= 0x01 && n <= 0x4b) {
					byte[] data = input.readBytes(n);
					Op op = new DataOp(data);
					list.add(op);
					log.info("OP: " + op);
				} else {
					Op op = Ops.getOp(n);
					if (op == null) {
						throw new UnsupportedOperationException(String.format("Unsupported OP: 0x%02x", n));
					}
					list.add(op);
					log.info("OP: " + op);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new ScriptEngine(list);
	}

	@Override
	public String toString() {
		List<String> list = this.ops.stream().map((op) -> {
			return op.toString();
		}).collect(Collectors.toList());
		return "-- BEGIN ----\n" + String.join("\n", list) + "\n-- END ----";
	}
}

class ScriptContextImpl implements ScriptContext {

	private final Transaction transaction;
	private final int txInIndex;
	private final Map<String, TxOut> prevUtxos;
	private final Deque<byte[]> stack = new ArrayDeque<>();

	public ScriptContextImpl(Transaction transaction, int txInIndex, Map<String, TxOut> prevUtxos) {
		this.transaction = transaction;
		this.txInIndex = txInIndex;
		this.prevUtxos = prevUtxos;
	}

	@Override
	public void push(byte[] data) {
		stack.push(data);
	}

	@Override
	public byte[] pop() {
		return stack.pop();
	}

	@Override
	public Transaction getTransaction() {
		return this.transaction;
	}

	@Override
	public Map<String, TxOut> getPreviousTxOutAsMap() {
		return this.prevUtxos;
	}

	@Override
	public int getTxInIndex() {
		return this.txInIndex;
	}
}

class DataOp extends Op {

	final byte[] data;

	DataOp(byte[] data) {
		super("DATA(" + HashUtils.toHexString(data) + ")");
		this.data = data;
	}

	@Override
	public boolean execute(ScriptContext context) {
		log.info("push data: " + HashUtils.toHexString(data));
		context.push(data);
		return true;
	}
}
