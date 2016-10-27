package com.itranswarp.bitcoin;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Queue;

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
import com.itranswarp.bitcoin.struct.InvVect;

public class PeerThread extends Thread {

	private final Log log = LogFactory.getLog(PeerThread.class);

	private final Queue<BlockMessage> queue;

	private volatile boolean running = true;

	private byte[] lastBlockHash = null;

	public PeerThread(Queue<BlockMessage> queue) {
		this.queue = queue;
	}

	synchronized byte[] getLastBlockHash() {
		return this.lastBlockHash;
	}

	public synchronized void setLastBlockHash(byte[] lastBlockHash) {
		this.lastBlockHash = lastBlockHash;
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
							byte[] lastBlockHash = getLastBlockHash();
							if (lastBlockHash != null) {
								setLastBlockHash(null);
								Message blks = new GetBlocksMessage(lastBlockHash, BitcoinConstants.ZERO_HASH);
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
				return new GetDataMessage(InvVect.MSG_BLOCK, hashes);
			}
		}
		if (msg instanceof BlockMessage) {
			BlockMessage block = (BlockMessage) msg;
			if (validateBlock(block)) {
				this.queue.add(block);
			}
		}
		return null;
	}

	private boolean validateBlock(BlockMessage block) {
		log.info("Process block...");
		byte[] merckleHash = block.calculateMerkleRoot();
		if (!Arrays.equals(merckleHash, block.header.merkleHash)) {
			log.error("Validate merckle hash failed.");
			return false;
		}
		return true;
	}
}
