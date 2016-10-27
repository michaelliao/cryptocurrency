package com.itranswarp.bitcoin;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinPeer;
import com.itranswarp.bitcoin.message.BlockMessage;
import com.itranswarp.bitcoin.message.GetBlocksMessage;
import com.itranswarp.bitcoin.message.GetDataMessage;
import com.itranswarp.bitcoin.message.InvMessage;
import com.itranswarp.bitcoin.message.Message;
import com.itranswarp.bitcoin.message.PingMessage;
import com.itranswarp.bitcoin.message.PongMessage;
import com.itranswarp.bitcoin.message.VerAckMessage;
import com.itranswarp.bitcoin.message.VersionMessage;
import com.itranswarp.bitcoin.store.model.BlockChainStore;
import com.itranswarp.bitcoin.struct.InvVect;
import com.itranswarp.bitcoin.util.HashUtils;

public class PeerThread extends Thread {

	private final Log log = LogFactory.getLog(PeerThread.class);

	private BlockChainStore store;

	private final Queue<BlockMessage> queue;

	private volatile boolean running = true;

	private Map<String, byte[]> pendingBlockHashes = new ConcurrentHashMap<>();

	public PeerThread(BlockChainStore store, Queue<BlockMessage> queue) {
		this.store = store;
		this.queue = queue;
	}

	public void stopPeer() {
		this.running = false;
		try {
			this.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void run() {
		try (BitcoinPeer peer = new BitcoinPeer()) {
			while (this.running) {
				String node = peer.getPeer();
				if (node == null) {
					// wait for DNS lookup:
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// ignore
					}
				} else {
					try {
						connectTo(node);
					} catch (SocketTimeoutException | SocketException e) {
						peer.removePeer(node);
					} catch (EOFException e) {
						peer.putAtLast(node);
					} catch (IOException e) {
						peer.putAtLast(node);
					}
				}
			}
		}
	}

	private void connectTo(String node) throws IOException {
		// try connect:
		log.info("Try connect to node: " + node);
		try (Socket sock = new Socket()) {
			sock.connect(new InetSocketAddress(node, BitcoinConstants.PORT), 3000);
			try (InputStream input = sock.getInputStream()) {
				try (OutputStream output = sock.getOutputStream()) {
					VersionMessage vmsg = new VersionMessage(0, sock.getInetAddress());
					log.info("=> " + vmsg);
					output.write(vmsg.toByteArray());
					// receive msg:
					while (true) {
						BitcoinInput in = new BitcoinInput(input);
						Message msg = Message.Builder.parseMessage(in);
						log.info("<= " + msg);
						Message resp = handleMessage(msg);
						if (resp != null) {
							log.info("=> " + resp);
							output.write(resp.toByteArray());
						} else {
							if (this.pendingBlockHashes.isEmpty()) {
								Message blks = new GetBlocksMessage(
										HashUtils.toBytesAsLittleEndian(this.store.getLastBlockHash()),
										BitcoinConstants.ZERO_HASH);
								log.info("=> " + blks);
								output.write(blks.toByteArray());
							}
						}
					}
				}
			}
		}
	}

	private Message handleMessage(Message msg) {
		if (msg instanceof PingMessage) {
			return new PongMessage(((PingMessage) msg).getNonce());
		}
		if (msg instanceof VersionMessage) {
			return new VerAckMessage();
		}
		if (msg instanceof InvMessage) {
			InvMessage inv = (InvMessage) msg;
			byte[][] hashes = inv.getBlockHashes();
			if (hashes.length > 0) {
				for (byte[] hash : hashes) {
					this.pendingBlockHashes.put(HashUtils.toHexStringAsLittleEndian(hash), hash);
				}
				return new GetDataMessage(InvVect.MSG_BLOCK, hashes);
			}
		}
		if (msg instanceof BlockMessage) {
			log.info("Process block...");
			BlockMessage block = (BlockMessage) msg;
			if (block.validateHash()) {
				this.pendingBlockHashes.remove(HashUtils.toHexStringAsLittleEndian(block.getBlockHash()));
				this.queue.add(block);
			} else {
				log.error("Validate merckle hash failed.");
			}
		}
		return null;
	}

}
