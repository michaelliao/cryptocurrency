package com.itranswarp.bitcoin.p2p;

import static org.junit.Assert.*;

import org.junit.Test;

public class PeerTest {
	@Test
	public void testEquals() {
		assertEquals(new Peer("abc", 1), new Peer("abc", 2));
		assertEquals(new Peer("xyz", 1), new Peer("xyz", 2));
		assertEquals(new Peer("abc", 1).hashCode(), new Peer("abc", 2).hashCode());
		assertEquals(new Peer("xyz", 1).hashCode(), new Peer("xyz", 2).hashCode());
	}

}
