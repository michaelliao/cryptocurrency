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
		for (byte i = 0; i < 100; i++) {
			log.info(HashUtils.toHexString(HashUtils.doubleSha256(new byte[] { i, (byte) (i + 1), (byte) (i + 2),
					(byte) (i + 3), (byte) (i + 4), (byte) (i + 5), (byte) (i + 6), (byte) (i + 7) })));
		}
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
