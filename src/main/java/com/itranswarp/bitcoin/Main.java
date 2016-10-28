package com.itranswarp.bitcoin;

import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

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
import com.itranswarp.bitcoin.util.HashUtils;

public class Main {

	static final Log log = LogFactory.getLog(Main.class);

	public static void main(String[] args) throws Exception {
		try (BitcoinPeer peer = new BitcoinPeer()) {
			for (;;) {
				String node = peer.getPeer();
				if (node == null) {
					Thread.sleep(100);
				} else {
					// try connect:
					log.info("Try connect to node: " + node);
					try (Socket sock = new Socket()) {
						sock.connect(new InetSocketAddress(node, BitcoinConstants.PORT), 3000);
						try (InputStream input = sock.getInputStream()) {
							try (OutputStream output = sock.getOutputStream()) {
								VersionMessage vmsg = new VersionMessage(0, sock.getInetAddress());
								log.info(":=> " + vmsg);
								output.write(vmsg.toByteArray());
								// receive msg:
								byte[] firstBlock = BitcoinConstants.GENESIS_HASH_BYTES;
								while (true) {
									BitcoinInput in = new BitcoinInput(input);
									Message msg = Message.Builder.parseMessage(in);
									log.info("<=: " + msg);
									Message resp = handleMessage(msg);
									if (resp != null) {
										log.info(":=> " + resp);
										output.write(resp.toByteArray());
									} else {
										if (firstBlock != null) {
											Message blks = new GetBlocksMessage(firstBlock, BitcoinConstants.ZERO_HASH_BYTES);
											log.info(":=> " + blks);
											output.write(blks.toByteArray());
											firstBlock = null;
										}
									}
								}
							}
						}
					} catch (SocketTimeoutException | SocketException e) {
						peer.removePeer(node);
					} catch (EOFException e) {
						peer.putAtLast(node);
					}
				}
			}
		}
		// String path = "/Users/liaoxuefeng/Bitcoin/blocks";
		// BlockChainImporter importer = new BlockChainImporter();
		// importer.importFromDir(path);
		// importer.importFromFile(new
		// File("/Users/liaoxuefeng/Bitcoin/blocks/blk00000.dat"));
		// try (LittleEndianDataInputStream input = new
		// LittleEndianDataInputStream(
		// new BufferedInputStream(new FileInputStream(file)))) {
		// Block block = new Block(input);
		// System.out.println(toJson(block));
		// // block.calculateNonce();
		// }
	}

	private static Message handleMessage(Message msg) {
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
			processBlock(block);
		}
		return null;
	}

	private static boolean processBlock(BlockMessage block) {
		log.info("Process block...");
		// byte[] merckleHash = block.calculateMerkleRoot();
		// if (!Arrays.equals(merckleHash, block.header.merkleHash)) {
		// log.error("Validate merckle hash failed.");
		// return false;
		// }
		return true;
	}
}
