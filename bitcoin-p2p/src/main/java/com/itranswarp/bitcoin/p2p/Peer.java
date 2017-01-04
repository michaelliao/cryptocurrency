package com.itranswarp.bitcoin.p2p;

import java.util.Objects;

public class Peer implements Comparable<Peer> {

	public String ip;
	public int score;
	transient boolean using;

	public Peer() {
	}

	public Peer(String ip) {
		this.ip = ip;
	}

	public void inc() {
		this.score++;
	}

	public void decr() {
		this.score--;
	}

	@Override
	public int compareTo(Peer o) {
		if (this.score > o.score) {
			return -1;
		}
		return 1;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Peer) {
			Peer p = (Peer) o;
			return Objects.equals(this.ip, p.ip);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.ip);
	}
}
