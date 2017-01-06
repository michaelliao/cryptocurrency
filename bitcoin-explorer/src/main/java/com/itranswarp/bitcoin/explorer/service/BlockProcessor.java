package com.itranswarp.bitcoin.explorer.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itranswarp.bitcoin.explorer.domain.BlockEntity;
import com.itranswarp.bitcoin.explorer.domain.TxEntity;
import com.itranswarp.bitcoin.explorer.repository.BlockRepository;
import com.itranswarp.bitcoin.explorer.repository.TxEntityRepository;
import com.itranswarp.bitcoin.struct.Block;
import com.itranswarp.bitcoin.struct.Transaction;
import com.itranswarp.bitcoin.util.HashUtils;

@Component
public class BlockProcessor {

	@Autowired
	BlockRepository blockRepository;

	@Autowired
	TxEntityRepository txEntityRepository;

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public BlockEntity processBlock(Block block) {
		BlockEntity blockEntity = new BlockEntity();
		blockEntity.blockHash = HashUtils.toHexStringAsLittleEndian(block.getBlockHash());
		blockEntity.bits = block.header.bits;
		blockEntity.merkleHash = HashUtils.toHexStringAsLittleEndian(block.header.merkleHash);
		blockEntity.nonce = block.header.nonce;
		blockEntity.prevHash = HashUtils.toHexStringAsLittleEndian(block.header.prevHash);
		blockEntity.timestamp = block.header.timestamp;
		blockEntity.version = block.header.version;
		blockRepository.save(blockEntity);
		// store tx:
		int txIndex = 0;
		for (Transaction tx : block.txns) {
			String txHash = HashUtils.toHexStringAsLittleEndian(tx.getTxHash());
			TxEntity txEntity = new TxEntity();
			txEntity.blockHash = blockEntity.blockHash;
			txEntity.txHash = txHash;
			txEntity.txIndex = txIndex;
			txEntity.inputCount = tx.getTxInCount();
			txEntity.outputCount = tx.getTxOutCount();
			txEntity.lockTime = tx.lock_time;
			txEntity.version = tx.version;
			txEntityRepository.save(txEntity);
			txIndex++;
		}
		// move utxo to stxo:
		// TODO:
		return blockEntity;
	}
}
