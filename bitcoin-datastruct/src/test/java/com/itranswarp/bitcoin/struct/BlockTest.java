package com.itranswarp.bitcoin.struct;

import static org.junit.Assert.*;

import org.junit.Test;

import com.itranswarp.bitcoin.constant.BitcoinConstants;
import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.util.ClasspathUtils;
import com.itranswarp.bitcoin.util.HashUtils;

public class BlockTest {

	@Test
	public void testBlock1() throws Exception {
		// data from:
		// https://webbtc.com/block/000000000000000000f061205567dc79c4e718209a568879d66132e016968ac6
		String blockHash = "000000000000000000f061205567dc79c4e718209a568879d66132e016968ac6";
		byte[] blockdata = ClasspathUtils.loadAsBytes("/block-" + blockHash + ".dat");
		try (BitcoinInput input = new BitcoinInput(blockdata)) {
			Block block = new Block(input);
			assertEquals(blockHash, HashUtils.toHexStringAsLittleEndian(block.getBlockHash()));
			assertEquals("7f6c9636d722c9e0fe7021ae26235b97150a26533c5cb297779590aed8a02c13",
					HashUtils.toHexStringAsLittleEndian(block.calculateMerkleHash()));
			assertEquals("000000000000000002b1502dc9a00036e66790c4cde07df425c6cbae3e0d8eca",
					HashUtils.toHexStringAsLittleEndian(block.header.prevHash));
			assertEquals(351, block.txns.length);
		}
	}

	@Test
	public void testBlock2() throws Exception {
		// data from:
		// https://webbtc.com/block/000000000000000003231f54a41b3197c0216d035a97056728c0f70b1406a1e8
		String blockHash = "000000000000000003231f54a41b3197c0216d035a97056728c0f70b1406a1e8";
		byte[] blockdata = ClasspathUtils.loadAsBytes("/block-" + blockHash + ".dat");
		try (BitcoinInput input = new BitcoinInput(blockdata)) {
			Block block = new Block(input);
			assertEquals(blockHash, HashUtils.toHexStringAsLittleEndian(block.getBlockHash()));
			assertEquals("fa4136e2f18581aa35828e7d7034ee64fcc32d918c2c2e5b2fb56eba830cdf25",
					HashUtils.toHexStringAsLittleEndian(block.calculateMerkleHash()));
			assertEquals("000000000000000003aa48932553bf2e0bc5a6e8f7d57ff1897f64d8ce840659",
					HashUtils.toHexStringAsLittleEndian(block.header.prevHash));
			assertEquals(1011, block.txns.length);
		}
	}

	@Test
	public void testGenesisBlock() throws Exception {
		try (BitcoinInput input = new BitcoinInput(BitcoinConstants.GENESIS_BLOCK_DATA)) {
			Block block = new Block(input);
			assertEquals(BitcoinConstants.GENESIS_HASH, HashUtils.toHexStringAsLittleEndian(block.getBlockHash()));
			assertEquals(1, block.txns.length);
		}
	}
}
