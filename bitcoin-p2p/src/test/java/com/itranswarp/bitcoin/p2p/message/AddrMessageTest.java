package com.itranswarp.bitcoin.p2p.message;

import static org.junit.Assert.*;

import org.junit.Test;

public class AddrMessageTest {

	@Test
	public void testNewAddrMessageWithPayload() throws Exception {
		byte[] data = { 1, 120, 125, 13, 88, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 85, -42, 90,
				1, 32, -115 };
		AddrMessage msg = new AddrMessage(data);
		assertEquals(1, msg.addr_list.length);
	}
}
