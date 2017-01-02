package com.itranswarp.bitcoin.explorer.controller;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itranswarp.bitcoin.struct.Block;

@Component
public class BlockProcessor {

	/**
	 * 全局锁，在分布式环境中请替换为Hazelcast Distributed Lock.
	 */
	Lock lock = new ReentrantLock();

	@Autowired
	TxBlockProcessor txBlockProcessor;

	public void processBlock(Block block) {
		lock.lock();
		try {
			txBlockProcessor.validateBlock(block);
			txBlockProcessor.processBlock(block);
		} finally {
			lock.unlock();
		}
	}

}
