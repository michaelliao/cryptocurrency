package com.itranswarp.bitcoin.struct;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.util.ClasspathUtils;
import com.itranswarp.bitcoin.util.HashUtils;
import com.itranswarp.bitcoin.util.JsonUtils;

public class TransactionTest {

	@Test
	public void testTx1() throws IOException {
		// real data from:
		// https://webbtc.com/tx/069d1cdb3fe70af7289ce0d7f08577fe6a72f75ddba30f84fa54392a6f67ec72
		String txhash = "069d1cdb3fe70af7289ce0d7f08577fe6a72f75ddba30f84fa54392a6f67ec72";
		byte[] txdata = ClasspathUtils.loadAsBytes("/tx-" + txhash + ".dat"); 
		try (BitcoinInput input = new BitcoinInput(txdata)) {
			Transaction tx = new Transaction(input);
			JsonUtils.printJson(tx);
			assertEquals(txhash, HashUtils.toHexStringAsLittleEndian(tx.getTxHash()));
		}
	}

	@Test
	public void testTx2() throws IOException {
		// real data from:
		// https://webbtc.com/tx/582a10734982c74693eadc53b7b1bdbed0840aeec568b6f890e685f08cf79473
		String txhash = "582a10734982c74693eadc53b7b1bdbed0840aeec568b6f890e685f08cf79473";
		byte[] txdata = ClasspathUtils.loadAsBytes("/tx-" + txhash + ".dat");

		try (BitcoinInput input = new BitcoinInput(txdata)) {
			Transaction tx = new Transaction(input);
			JsonUtils.printJson(tx);
			System.out.println(HashUtils.toHexString(tx.tx_ins[0].sigScript));
			assertEquals(txhash, HashUtils.toHexStringAsLittleEndian(tx.getTxHash()));
		}
	}

}
