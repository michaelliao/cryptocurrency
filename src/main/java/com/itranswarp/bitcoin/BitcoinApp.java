package com.itranswarp.bitcoin;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.bitcoin.io.BitcoinFileManager;
import com.itranswarp.bitcoin.message.BlockMessage;
import com.itranswarp.bitcoin.store.model.BlockChainStore;

public class BitcoinApp {

	final Log log = LogFactory.getLog(BitcoinApp.class);
	final Queue<BlockMessage> queue = new LinkedBlockingQueue<>();

	private BitcoinApp() {
	}

	public void run() {
		String dbfile = BitcoinFileManager.getInstance().getFile("chain.db").getAbsolutePath();
		BlockChainStore store = new BlockChainStore(dbfile);
		PeerThread peerThread = new PeerThread(this.queue);

	}

	public static void main(String[] args) throws Exception {
		BitcoinApp app = new BitcoinApp();
		app.run();
	}

}
