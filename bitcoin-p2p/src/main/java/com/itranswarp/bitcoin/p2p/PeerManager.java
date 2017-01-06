package com.itranswarp.bitcoin.p2p;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.bitcoin.util.JsonUtils;

public class PeerManager {

	final int MAX_SIZE = 500;

	final Log log = LogFactory.getLog(getClass());
	final File cached;

	// peers
	final List<Peer> peers = new ArrayList<>();

	public PeerManager() {
		this(null);
	}

	public PeerManager(File cachedFile) {
		this.cached = cachedFile;
		Peer[] cachedPeers = loadPeers();
		addPeers(cachedPeers);
		if (peers.size() < 5) {
			// lookup from DNS:
			Thread t = new Thread() {
				public void run() {
					try {
						String[] ips = PeerDiscover.lookup();
						addPeers(ips);
					} catch (Exception e) {
						log.warn("Could not discover peers.", e);
					}
				}
			};
			t.setDaemon(true);
			t.start();
		}
	}

	public synchronized int peerCount() {
		return peers.size();
	}

	/**
	 * Return a peer ip to connect.
	 * 
	 * @return Ip or null if no peer available.
	 */
	public synchronized String getPeer() {
		log.info("Try get an unused peer from " + this.peers.size() + " peers...");
		this.peers.sort(new Comparator<Peer>() {
			@Override
			public int compare(Peer p1, Peer p2) {
				// TODO Auto-generated method stub
				return p1.score > p2.score ? -1 : 1;
			}
		});
		for (Peer p : this.peers) {
			if (!p.using) {
				p.using = true;
				return p.ip;
			}
		}
		return null;
	}

	/**
	 * Release a peer.
	 * 
	 * @param ip
	 *            The ip address.
	 * @param score
	 *            The score of peer.
	 */
	public synchronized void releasePeer(String ip, int score) {
		Peer target = null;
		for (Peer p : this.peers) {
			if (p.ip.equals(ip)) {
				target = p;
				break;
			}
		}
		if (target != null) {
			target.using = false;
			target.score += score;
			if (target.score < 0) {
				this.peers.remove(target);
			}
		}
		storePeers();
	}

	public synchronized void addPeers(String[] ips) {
		Peer[] ps = new Peer[ips.length];
		for (int i = 0; i < ps.length; i++) {
			ps[i] = new Peer(ips[i]);
		}
		addPeers(ps);
	}

	public synchronized void addPeers(Peer[] ps) {
		log.info("Add discovered " + ps.length + " peers...");
		for (Peer p : ps) {
			if (!this.peers.contains(p)) {
				this.peers.add(p);
			}
		}
		log.info("Total peers: " + this.peers.size());
		storePeers();
	}

	public synchronized void close() {
		storePeers();
	}

	Peer[] loadPeers() {
		if (this.cached != null) {
			try (InputStream input = new BufferedInputStream(new FileInputStream(this.cached))) {
				return JsonUtils.fromJson(Peer[].class, input);
			} catch (Exception e) {
				log.warn("Load cached peers from cached file failed: " + this.cached.getAbsolutePath());
			}
		}
		return new Peer[0];
	}

	void storePeers() {
		if (this.cached != null) {
			try (Writer writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(this.cached), "UTF-8"))) {
				Peer[] peerArray = this.peers.toArray(new Peer[0]);
				writer.write(JsonUtils.toJson(peerArray));
			} catch (Exception e) {
				log.warn("Write peers to cached file failed: " + this.cached.getAbsolutePath(), e);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		PeerManager manager = new PeerManager();
		for (int i = 0; i < 60; i++) {
			Thread.sleep(1000);
			System.out.print('.');
			if (manager.peerCount() > 0) {
				break;
			}
		}
		System.out.println("\n" + manager.peerCount() + " peers discovered.");
	}
}
