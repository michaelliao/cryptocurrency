package com.itranswarp.bitcoin.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ScriptParserTest {

	ScriptParser parser;

	@Before
	public void setUp() throws Exception {
		parser = new ScriptParser();
	}

	@Test
	public void testParseScriptSig() {
		byte[] data = HashUtils.toBytes(
				"3046022100f493d504a670e6280bc76ee285accf2796fd6a630659d4fa55dccb793fc9346402210080ecfbe069101993eb01320bb1d6029c138b27835e20ad54c232c36ffe10786301");
		System.out.println(parser.parse(data));
	}

	@Test
	public void testParseScriptPubKey() {
		byte[] data = HashUtils.toBytes("76a914cca1bb8ccdeb3e621d69a295c9e2b1cd9517d1b988ac");
		System.out.println(parser.parse(data));
	}

}
