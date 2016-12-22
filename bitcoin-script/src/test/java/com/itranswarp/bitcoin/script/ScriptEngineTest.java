package com.itranswarp.bitcoin.script;

import static org.junit.Assert.*;

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
	}

}
