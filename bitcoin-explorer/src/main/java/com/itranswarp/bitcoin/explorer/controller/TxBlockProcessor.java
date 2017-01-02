package com.itranswarp.bitcoin.explorer.controller;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itranswarp.bitcoin.explorer.domain.BlockEntity;
import com.itranswarp.bitcoin.explorer.repository.BlockRepository;
import com.itranswarp.bitcoin.struct.Block;
import com.itranswarp.bitcoin.util.HashUtils;

@Component
public class TxBlockProcessor {

	@Autowired
	BlockRepository blockRepository;

	@Transactional
	public void validateBlock(Block block) {
		//
	}

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public void processBlock(Block block) {
		BlockEntity blockEntity = new BlockEntity();
		blockEntity.blockHash = block.getBlockHash();
		blockEntity.bits = block.header.bits;
		blockEntity.merkleHash = HashUtils.toHexStringAsLittleEndian(block.header.merkleHash);
		blockEntity.nonce = block.header.nonce;
		blockEntity.prevHash = HashUtils.toHexStringAsLittleEndian(block.header.prevHash);
		blockEntity.timestamp = block.header.timestamp;
		blockEntity.version = block.header.version;
		blockRepository.save(blockEntity);
	}
}
