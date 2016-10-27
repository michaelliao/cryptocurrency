package com.itranswarp.bitcoin.io;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.cryptocurrency.common.JsonUtil;

public class BitcoinPeer implements AutoCloseable {

	final Log log = LogFactory.getLog(getClass());

	static final String ACTIVE_NODES_CACHE = "active-nodes.json";

	BitcoinFileManager fileManager = BitcoinFileManager.getInstance();

	Queue<String> activeNodes = new ArrayBlockingQueue<>(100);

	public BitcoinPeer() {
		try {
			@SuppressWarnings("unchecked")
			List<String> list = JsonUtil.fromJson(List.class, fileManager.loadFile(ACTIVE_NODES_CACHE));
			activeNodes.addAll(list);
		} catch (IOException e) {
			/* ignore */
		}
		if (activeNodes.size() < 5) {
			// load from DNS:
			Thread t = new Thread() {
				public void run() {
					PeerDiscover discover = BitcoinPeerDiscover.getInstance();
					try {
						String[] ips = discover.lookup();
						activeNodes.addAll(Arrays.asList(ips));
					} catch (IOException e) {
						log.warn("Could not discover peers.", e);
					}
				}
			};
			t.setDaemon(true);
			t.start();
		}
	}

	public String getPeer() {
		return activeNodes.peek();
	}

	public void removePeer(String node) {
		activeNodes.remove(node);
	}

	public void putAtLast(String node) {
		activeNodes.remove(node);
		activeNodes.add(node);
	}

	@Override
	public void close() {
		try {
			fileManager.writeFile(ACTIVE_NODES_CACHE, JsonUtil.toJson(activeNodes));
		} catch (IOException e) {
			log.error("Write file failed.", e);
		}
	}
}
