package com.itranswarp.bitcoin.p2p.message;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import com.itranswarp.bitcoin.io.BitcoinInput;

public class VersionMessageTest {

	@Test
	public void testParseByteArray() throws Exception {
		byte[] data = { -7, -66, -76, -39, 118, 101, 114, 115, 105, 111, 110, 0, 0, 0, 0, 0, 102, 0, 0, 0, 14, -79, 30,
				62, 126, 17, 1, 0, 5, 0, 0, 0, 0, 0, 0, 0, 15, 114, 9, 88, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, -1, -1, 106, 39, 114, 66, -21, 67, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, -1, -1, 85, -42, 90, 1, 32, -115, -82, -89, 69, -11, 120, 13, -37, 97, 16, 47, 83, 97, 116, 111,
				115, 104, 105, 58, 48, 46, 49, 51, 46, 48, 47, -22, -93, 6, 0, 1 };
		VersionMessage msg = Message.Builder.parseMessage(new BitcoinInput(new ByteArrayInputStream(data)));
		assertEquals(70014, msg.protocolVersion);
		assertEquals("/Satoshi:0.13.0/", msg.subVersion);
	}

}
