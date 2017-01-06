package com.itranswarp.bitcoin.p2p;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.bitcoin.constant.BitcoinConstants;
import com.itranswarp.bitcoin.p2p.message.BlockMessage;
import com.itranswarp.bitcoin.p2p.message.GetBlocksMessage;
import com.itranswarp.bitcoin.p2p.message.GetDataMessage;
import com.itranswarp.bitcoin.p2p.message.InvMessage;
import com.itranswarp.bitcoin.p2p.message.Message;
import com.itranswarp.bitcoin.p2p.message.PingMessage;
import com.itranswarp.bitcoin.p2p.message.PongMessage;
import com.itranswarp.bitcoin.p2p.message.VerAckMessage;
import com.itranswarp.bitcoin.p2p.message.VersionMessage;
import com.itranswarp.bitcoin.struct.InvVect;
import com.itranswarp.bitcoin.util.HashUtils;

/**
 * Test peer connection.
 * 
 * @author liaoxuefeng
 */
public class PeerConnectionPoolTest {

	public static void main(String[] args) throws Exception {
		MessageHandler handler = new MessageHandler();
		PeerConnectionPool pool = new PeerConnectionPool(handler);
		pool.start();
		Thread.sleep(120000);
		pool.close();
		pool.join();
	}

	static class MessageHandler implements MessageListener {

		String lastBlockHash = com.itranswarp.bitcoin.constant.BitcoinConstants.GENESIS_HASH;

		final Log log = LogFactory.getLog(getClass());

		@Override
		public void onMessage(MessageSender sender, Message msg) {
			if (msg instanceof PingMessage) {
				sender.sendMessage(new PongMessage(((PingMessage) msg).getNonce()));
				return;
			}
			if (msg instanceof VersionMessage) {
				sender.sendMessage(new VerAckMessage());
				sender.sendMessage(new GetBlocksMessage(HashUtils.toBytesAsLittleEndian(this.lastBlockHash),
						BitcoinConstants.ZERO_HASH_BYTES));
				return;
			}
			if (msg instanceof InvMessage) {
				InvMessage inv = (InvMessage) msg;
				byte[][] hashes = inv.getBlockHashes();
				if (hashes.length > 0) {
					for (byte[] hash : hashes) {
						log.info("InvMessage::block hash: " + HashUtils.toHexStringAsLittleEndian(hash));
					}
					sender.sendMessage(new GetDataMessage(InvVect.MSG_BLOCK, hashes));
				}
			}
			if (msg instanceof BlockMessage) {
				BlockMessage blockMsg = (BlockMessage) msg;
				log.info("Get block data: " + HashUtils.toHexStringAsLittleEndian(blockMsg.block.getBlockHash()));
			}
		}
	}
}
