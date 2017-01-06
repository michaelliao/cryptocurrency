package com.itranswarp.bitcoin.p2p;

public class Peer {

	public String ip;
	public int score;
	volatile boolean using;

	public Peer() {
	}

	public Peer(String ip) {
		this.ip = ip;
	}

	public Peer(String ip, int score) {
		this.ip = ip;
		this.score = score;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Peer) {
			Peer p = (Peer) o;
			return this.ip.equals(p.ip);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.ip.hashCode();
	}
}
