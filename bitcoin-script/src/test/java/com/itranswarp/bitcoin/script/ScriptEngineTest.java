package com.itranswarp.bitcoin.script;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.struct.Transaction;
import com.itranswarp.bitcoin.struct.TxIn;
import com.itranswarp.bitcoin.struct.TxOut;
import com.itranswarp.bitcoin.util.ClasspathUtils;
import com.itranswarp.bitcoin.util.HashUtils;

public class ScriptEngineTest {

	@Test
	public void testExecute() throws Exception {
		// pizza transaction:
		// https://webbtc.com/tx/cca7507897abc89628f450e8b1e0c6fca4ec3f7b34cccf55f3f531c659ff4d79
		String pizzaTxHash = "cca7507897abc89628f450e8b1e0c6fca4ec3f7b34cccf55f3f531c659ff4d79";
		String prevTxHash = "a1075db55d416d3ca199f55b6084e2115b9345e16c5cf302fc80e9d5fbf5d48d";
		byte[] pizzaTxData = ClasspathUtils.loadAsBytes("/tx-" + pizzaTxHash + ".dat");
		byte[] prevTxData = ClasspathUtils.loadAsBytes("/tx-" + prevTxHash + ".dat");
		Transaction pizzaTx = null;
		Transaction prevTx = null;
		try (BitcoinInput input = new BitcoinInput(pizzaTxData)) {
			pizzaTx = new Transaction(input);
		}
		try (BitcoinInput input = new BitcoinInput(prevTxData)) {
			prevTx = new Transaction(input);
		}
		assertEquals(1, pizzaTx.getTxInCount());
		TxIn txIn = pizzaTx.tx_ins[0];
		assertEquals(139, txIn.sigScript.length);
		byte[] sigScript = txIn.sigScript;
		assertEquals(
				"4830450221009908144ca6539e09512b9295c8a27050d478fbb96f8addbc3d075544dc41328702201aa528be2b907d316d2da068dd9eb1e23243d97e444d59290d2fddf25269ee0e0141042e930f39ba62c6534ee98ed20ca98959d34aa9e057cda01cfd422c6bab3667b76426529382c23f42b9b08d7832d4fee1d6b437a8526e59667ce9c4e9dcebcabb",
				HashUtils.toHexString(sigScript));
		// find previous output:
		assertEquals(1, prevTx.getTxOutCount());
		TxOut txOut = prevTx.tx_outs[0];
		byte[] outScript = txOut.pk_script;
		assertEquals("76a91446af3fb481837fadbb421727f9959c2d32a3682988ac", HashUtils.toHexString(outScript));
		// execute:
		ScriptEngine engine = ScriptEngine.parse(sigScript, outScript);

		System.out.println(engine);
		Map<String, TxOut> prevUtxos = new HashMap<>();
		for (int i = 0; i < prevTx.getTxOutCount(); i++) {
			prevUtxos.put(HashUtils.toHexStringAsLittleEndian(prevTx.getTxHash()) + "#" + i, prevTx.tx_outs[i]);
		}
		assertTrue(engine.execute(pizzaTx, 0, prevUtxos));
		assertEquals("17SkEw2md5avVNyYgj6RiXuQKNwkXaxFyQ", engine.getExtractAddress());
		// modify signature and execute again:
		byte[] sigScript2 = Arrays.copyOf(sigScript, sigScript.length);
		sigScript2[10] = 0x1f;
		ScriptEngine engine2 = ScriptEngine.parse(sigScript2, outScript);
		assertFalse(engine2.execute(pizzaTx, 0, prevUtxos));
	}

	@Test
	public void testTx2Inputs2Outputs() throws Exception {
		// transaction:
		// https://webbtc.com/tx/d7202bbc2bc3e1300217ec629ae902260a0440dcdb089f3dd90ce405268ccdf3
		String txHash = "d7202bbc2bc3e1300217ec629ae902260a0440dcdb089f3dd90ce405268ccdf3";
		byte[] txData = ClasspathUtils.loadAsBytes("/tx-" + txHash + ".dat");
		Transaction tx = null;
		try (BitcoinInput input = new BitcoinInput(txData)) {
			tx = new Transaction(input);
		}
		assertEquals(2, tx.getTxInCount());
		assertEquals(2, tx.getTxOutCount());
		// load prev txs:
		Transaction prevTx0 = null;
		Transaction prevTx1 = null;
		try (BitcoinInput input = new BitcoinInput(ClasspathUtils.loadAsBytes(
				"/tx-" + HashUtils.toHexStringAsLittleEndian(tx.tx_ins[0].previousOutput.hash) + ".dat"))) {
			prevTx0 = new Transaction(input);
		}
		try (BitcoinInput input = new BitcoinInput(ClasspathUtils.loadAsBytes(
				"/tx-" + HashUtils.toHexStringAsLittleEndian(tx.tx_ins[1].previousOutput.hash) + ".dat"))) {
			prevTx1 = new Transaction(input);
		}
		// load prev output:
		TxOut prevOutput0 = prevTx0.tx_outs[(int) tx.tx_ins[0].previousOutput.index];
		assertEquals(15879600L, prevOutput0.value);
		TxOut prevOutput1 = prevTx1.tx_outs[(int) tx.tx_ins[1].previousOutput.index];
		assertEquals(7950000L, prevOutput1.value);
		// execute:
		Map<String, TxOut> prevUtxos = new HashMap<>();
		prevUtxos.put(
				HashUtils.toHexStringAsLittleEndian(prevTx0.getTxHash()) + "#" + tx.tx_ins[0].previousOutput.index,
				prevOutput0);
		prevUtxos.put(
				HashUtils.toHexStringAsLittleEndian(prevTx1.getTxHash()) + "#" + tx.tx_ins[1].previousOutput.index,
				prevOutput1);
		ScriptEngine engine = null;
		// in0:
		engine = ScriptEngine.parse(tx.tx_ins[0].sigScript, prevOutput0.pk_script);
		assertTrue(engine.execute(tx, 0, prevUtxos));
		assertEquals("1A9WgSDNBvrgSvTT5CH9iYyZRANw5mo4pP", engine.getExtractAddress());
		// in1:
		engine = ScriptEngine.parse(tx.tx_ins[1].sigScript, prevOutput1.pk_script);
		assertTrue(engine.execute(tx, 1, prevUtxos));
		assertEquals("1LygMU2TCKLsmQe8Hd7XV4ZYJoKtVHdMm9", engine.getExtractAddress());
	}

	@Test
	public void testGetAddress() throws Exception {
		// pizza transaction:
		// https://webbtc.com/tx/cca7507897abc89628f450e8b1e0c6fca4ec3f7b34cccf55f3f531c659ff4d79
		String pizzaTxHash = "cca7507897abc89628f450e8b1e0c6fca4ec3f7b34cccf55f3f531c659ff4d79";
		byte[] pizzaTxData = ClasspathUtils.loadAsBytes("/tx-" + pizzaTxHash + ".dat");
		Transaction pizzaTx = null;
		try (BitcoinInput input = new BitcoinInput(pizzaTxData)) {
			pizzaTx = new Transaction(input);
		}
		assertEquals(1, pizzaTx.getTxInCount());
		assertEquals(2, pizzaTx.getTxOutCount());
		// output 1:
		TxOut out0 = pizzaTx.tx_outs[0];
		ScriptEngine engine0 = ScriptEngine.parse(new byte[0], out0.pk_script);
		assertEquals("1MLh2UVHgonJY4ZtsakoXtkcXDJ2EPU6RY", engine0.getExtractAddress());
		// output 2:
		TxOut out1 = pizzaTx.tx_outs[1];
		ScriptEngine engine1 = ScriptEngine.parse(new byte[0], out1.pk_script);
		assertEquals("13TETb2WMr58mexBaNq1jmXV1J7Abk2tE2", engine1.getExtractAddress());
	}
}
