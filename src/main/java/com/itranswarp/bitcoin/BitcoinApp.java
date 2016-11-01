package com.itranswarp.bitcoin;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.bitcoin.io.BitcoinFileManager;
import com.itranswarp.bitcoin.message.BlockMessage;
import com.itranswarp.bitcoin.store.BlockChainStore;
import com.itranswarp.bitcoin.store.ValidateException;

public class BitcoinApp {

	final Log log = LogFactory.getLog(BitcoinApp.class);
	final Queue<BlockMessage> queue;
	final BlockChainStore store;

	private BitcoinApp() {
		String dbfile = BitcoinFileManager.getInstance().getFile("chain.db").getAbsolutePath();
		this.store = new BlockChainStore(dbfile);
		this.queue = new LinkedBlockingQueue<>();
	}

	public void run() {
		PeerThread peerThread = new PeerThread(this.store, this.queue);
		peerThread.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				log.info("Stoping peer...");
				peerThread.stopPeer();
				log.info("Exit.");
			}
		});
		for (;;) {
			BlockMessage msg = this.queue.poll();
			if (msg != null) {
				try {
					this.store.addBlock(msg);
				} catch (ValidateException e) {
					log.warn("ValidateException", e);
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				log.warn("InterruptedException", e);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		BitcoinApp app = new BitcoinApp();
		app.run();
	}

}
