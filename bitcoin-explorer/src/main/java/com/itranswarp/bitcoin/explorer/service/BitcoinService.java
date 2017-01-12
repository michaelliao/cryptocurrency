package com.itranswarp.bitcoin.explorer.service;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.itranswarp.bitcoin.BitcoinException;
import com.itranswarp.bitcoin.constant.BitcoinConstants;
import com.itranswarp.bitcoin.explorer.domain.BlockEntity;
import com.itranswarp.bitcoin.explorer.repository.BlockRepository;
import com.itranswarp.bitcoin.explorer.repository.OutRepository;
import com.itranswarp.bitcoin.explorer.repository.TxRepository;
import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.p2p.MessageListener;
import com.itranswarp.bitcoin.p2p.MessageSender;
import com.itranswarp.bitcoin.p2p.PeerConnectionPool;
import com.itranswarp.bitcoin.p2p.message.BlockMessage;
import com.itranswarp.bitcoin.p2p.message.GetBlocksMessage;
import com.itranswarp.bitcoin.p2p.message.GetDataMessage;
import com.itranswarp.bitcoin.p2p.message.InvMessage;
import com.itranswarp.bitcoin.p2p.message.Message;
import com.itranswarp.bitcoin.p2p.message.PingMessage;
import com.itranswarp.bitcoin.p2p.message.PongMessage;
import com.itranswarp.bitcoin.p2p.message.VerAckMessage;
import com.itranswarp.bitcoin.p2p.message.VersionMessage;
import com.itranswarp.bitcoin.struct.Block;
import com.itranswarp.bitcoin.struct.InvVect;
import com.itranswarp.bitcoin.util.HashUtils;
import com.itranswarp.bitcoin.util.LRUCache;

@Component
public class BitcoinService implements MessageListener {

	final Log log = LogFactory.getLog(getClass());

	/**
	 * 全局锁，在分布式环境中请替换为Hazelcast Distributed Lock.
	 */
	final Lock lock = new ReentrantLock();

	/**
	 * 最新Block的hash，在分布式环境中请替换为Hazelcast Distributed Map.
	 */
	private volatile String lastBlockHash = BitcoinConstants.ZERO_HASH;

	/**
	 * 按序存储待处理的BlockMessage
	 */
	private Deque<Block> deque = new LinkedList<>();

	/**
	 * 缓存不能处理的BlockMessage，key=prevHash
	 */
	private Map<String, Block> cache = new LRUCache<>();

	/**
	 * Connection Pool
	 */
	private PeerConnectionPool pool;

	@Autowired
	BlockProcessor blockProcessor;

	@Autowired
	BlockRepository blockRepository;

	@Autowired
	TxRepository txRepository;

	@Autowired
	OutRepository outRepository;

	@PostConstruct
	public void init() throws IOException {
		initBlockData();
		initConnectionPool();
	}

	private void initBlockData() throws IOException {
		BlockEntity block = blockRepository.findFirstByOrderByHeightDesc();
		if (block == null) {
			// add genesis block:
			log.info("Add genesis block...");
			try (BitcoinInput input = new BitcoinInput(BitcoinConstants.GENESIS_BLOCK_DATA)) {
				Block gb = new Block(input);
				processNextBlock(gb);
			}
		}
		// get last block:
		BlockEntity last = blockRepository.findFirstByOrderByHeightDesc();
		this.lastBlockHash = last.blockHash;
		log.info("Last block: " + this.lastBlockHash + " created at "
				+ Instant.ofEpochSecond(last.timestamp).atZone(ZoneId.systemDefault()).toString());
		long n = (Instant.now().getEpochSecond() - last.timestamp) / 600;
		log.info("Needs to synchronize about " + n + " blocks...");
	}

	private void initConnectionPool() {
		this.pool = new PeerConnectionPool(this);
		this.pool.start();
	}

	@PreDestroy
	public void destroy() {
		if (this.pool != null) {
			this.pool.close();
		}
	}

	/**
	 * Get last (latest) block hash.
	 * 
	 * @return Block hash.
	 */
	public String getLastBlockHash() {
		return this.lastBlockHash;
	}

	/**
	 * Get block entity by hash.
	 * 
	 * @param hash
	 *            block hash.
	 * @return Block or null if not found.
	 */
	public BlockEntity getBlock(String hash) {
		return checkNonNull(blockRepository.findOne(hash), "Block not found.");
	}

	/**
	 * process next block from queue.
	 */
	@Scheduled(initialDelay = 10_000, fixedRate = 1_000)
	public void processPendingBlock() {
		lock.lock();
		try {
			Block block = this.deque.pollFirst();
			if (block == null) {
				log.info("scheduled process: nothing to do.");
				return;
			}
			this.processNextBlock(block);
		} finally {
			lock.unlock();
		}
	}

	public void processBlockFromPeer(Block block) {
		final String hash = HashUtils.toHexStringAsLittleEndian(block.getBlockHash());
		log.info("process block " + hash + " from peer...");
		lock.lock();
		try {
			// already processed?
			if (this.blockRepository.exists(hash)) {
				log.info("block " + hash + " was already processed.");
				return;
			}
			// add to cache first:
			String prevHash = HashUtils.toHexStringAsLittleEndian(block.header.prevHash);
			cache.put(prevHash, block);
			// get last hash:
			String lastHash = deque.isEmpty() ? this.lastBlockHash
					: HashUtils.toHexStringAsLittleEndian(deque.peekLast().getBlockHash());
			// try get all to queue:
			while (true) {
				Block next = cache.remove(lastHash);
				if (next == null) {
					break;
				}
				this.deque.offerLast(next);
				lastHash = HashUtils.toHexStringAsLittleEndian(next.getBlockHash());
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Process new block.
	 * 
	 * @param block
	 */
	public void processNextBlock(Block block) {
		String hash = HashUtils.toHexStringAsLittleEndian(block.getBlockHash());
		log.info("Process next block " + hash + "...");
		lock.lock();
		try {
			// check prevHash:
			String prevHash = HashUtils.toHexStringAsLittleEndian(block.header.prevHash);
			if (!this.lastBlockHash.equals(prevHash)) {
				log.warn("Validate block failed: expected prevHash = " + this.lastBlockHash + ", actual = " + prevHash);
				// cannot continue process:
				this.deque.clear();
				System.exit(1);
				return;
			}
			// check merkle root:
			String actualMerkle = HashUtils.toHexStringAsLittleEndian(block.calculateMerkleHash());
			String expectedMerkle = HashUtils.toHexStringAsLittleEndian(block.header.merkleHash);
			if (!actualMerkle.equals(expectedMerkle)) {
				log.error("Invalid merkle hash: expected = " + expectedMerkle + ", actual = " + actualMerkle);
				// cannot continue process:
				this.deque.clear();
				System.exit(1);
				return;
			}
			// check transactions:
			if (!checkTransactions(block)) {
				log.error("Check transactions failed.");
				this.deque.clear();
				System.exit(1);
				return;
			}
			blockProcessor.processBlock(block);
			this.lastBlockHash = hash;
			log.info("Added block: " + hash);
		} finally {
			lock.unlock();
		}
	}

	boolean checkTransactions(Block block) {
		//
		return true;
	}

	<T> T checkNonNull(T object, String message) {
		if (object == null) {
			throw new BitcoinException(message);
		}
		return object;
	}

	/**
	 * Handle received message from peer.
	 */
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
			sender.setTimeout(60_000);
			BlockMessage blockMsg = (BlockMessage) msg;
			log.info("Get block data: " + HashUtils.toHexStringAsLittleEndian(blockMsg.block.getBlockHash()));
			processBlockFromPeer(blockMsg.block);
		}
	}

}
