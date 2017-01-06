package com.itranswarp.bitcoin.explorer.service;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itranswarp.bitcoin.BitcoinException;
import com.itranswarp.bitcoin.constant.BitcoinConstants;
import com.itranswarp.bitcoin.explorer.domain.BlockEntity;
import com.itranswarp.bitcoin.explorer.repository.BlockRepository;
import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.struct.Block;
import com.itranswarp.bitcoin.util.HashUtils;

@Component
public class BitcoinService {

	final Log log = LogFactory.getLog(getClass());

	/**
	 * 全局锁，在分布式环境中请替换为Hazelcast Distributed Lock.
	 */
	final Lock lock = new ReentrantLock();

	/**
	 * 最新Block的hash，在分布式环境中请替换为Hazelcast Distributed Map.
	 */
	private volatile String lastBlockHash = BitcoinConstants.ZERO_HASH;

	@Autowired
	BlockProcessor blockProcessor;

	@Autowired
	BlockRepository blockRepository;

	@PostConstruct
	public void init() throws IOException {
		BlockEntity block = blockRepository.findFirstByOrderByHeightDesc();
		if (block == null) {
			// add genesis block:
			log.info("Add genesis block...");
			try (BitcoinInput input = new BitcoinInput(BitcoinConstants.GENESIS_BLOCK_DATA)) {
				Block gb = new Block(input);
				processBlock(gb);
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

	/**
	 * Get last (latest) block hash.
	 * 
	 * @return Block hash.
	 */
	public String getLastBlockHash() {
		return this.lastBlockHash;
	}

	/**
	 * Get block by hash.
	 * 
	 * @param hash
	 *            block hash.
	 * @return Block or null if not found.
	 */
	public Block getBlock(String hash) {
		//
		BlockEntity entity = checkNonNull(blockRepository.findOne(hash), "Block not found.");
		return null;
	}

	/**
	 * Process new block.
	 * 
	 * @param block
	 */
	public void processBlock(Block block) {
		String hash = HashUtils.toHexStringAsLittleEndian(block.getBlockHash());
		log.info("Process block " + hash + "...");
		lock.lock();
		try {
			validateBlock(block);
			blockProcessor.processBlock(block);
			this.lastBlockHash = hash;
			log.info("Added block: " + hash);
		} finally {
			lock.unlock();
		}
	}

	public boolean validateBlock(Block block) {
		String prevHash = HashUtils.toHexStringAsLittleEndian(block.header.prevHash);
		if (!this.lastBlockHash.equals(prevHash)) {
			log.warn("Validate block failed: expected prevHash = " + this.lastBlockHash + ", actual = " + prevHash);
			return false;
		}
		return true;
	}

	<T> T checkNonNull(T object, String message) {
		if (object == null) {
			throw new BitcoinException(message);
		}
		return object;
	}

}
