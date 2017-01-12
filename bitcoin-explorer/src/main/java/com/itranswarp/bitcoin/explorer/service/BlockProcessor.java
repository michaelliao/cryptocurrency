package com.itranswarp.bitcoin.explorer.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itranswarp.bitcoin.constant.BitcoinConstants;
import com.itranswarp.bitcoin.explorer.domain.BlockEntity;
import com.itranswarp.bitcoin.explorer.domain.OutEntity;
import com.itranswarp.bitcoin.explorer.domain.TxEntity;
import com.itranswarp.bitcoin.explorer.repository.BlockRepository;
import com.itranswarp.bitcoin.explorer.repository.OutRepository;
import com.itranswarp.bitcoin.explorer.repository.TxRepository;
import com.itranswarp.bitcoin.struct.Block;
import com.itranswarp.bitcoin.struct.Transaction;
import com.itranswarp.bitcoin.struct.TxIn;
import com.itranswarp.bitcoin.struct.TxOut;
import com.itranswarp.bitcoin.util.HashUtils;

@Component
public class BlockProcessor {

	@Autowired
	BlockRepository blockRepository;

	@Autowired
	TxRepository txRepository;

	@Autowired
	OutRepository outRepository;

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	public BlockEntity processBlock(Block block) {
		final String prevHash = HashUtils.toHexStringAsLittleEndian(block.header.prevHash);
		long height = 0;
		if (!BitcoinConstants.ZERO_HASH.equals(prevHash)) {
			BlockEntity prevEntity = this.blockRepository.findOne(prevHash);
			height = prevEntity.height + 1;
		}
		final String blockHash = HashUtils.toHexStringAsLittleEndian(block.getBlockHash());
		BlockEntity blockEntity = new BlockEntity();
		blockEntity.blockHash = blockHash; // pk
		blockEntity.bits = block.header.bits;
		blockEntity.merkleHash = HashUtils.toHexStringAsLittleEndian(block.header.merkleHash);
		blockEntity.nonce = block.header.nonce;
		blockEntity.prevHash = prevHash;
		blockEntity.height = height;
		blockEntity.timestamp = block.header.timestamp;
		blockEntity.version = block.header.version;
		blockRepository.save(blockEntity);
		// store tx:
		for (int txIndex = 0; txIndex < block.txns.length; txIndex++) {
			final Transaction tx = block.txns[txIndex];
			final String txHash = HashUtils.toHexStringAsLittleEndian(tx.getTxHash());
			// mark previous outputs as spent:
			long totalInput = 0;
			for (TxIn txin : tx.tx_ins) {
				final OutEntity out = outRepository
						.findOne(HashUtils.toHexStringAsLittleEndian(txin.previousOutput.hash) + "#"
								+ txin.previousOutput.index);
				out.spent = true;
				out.sigScript = HashUtils.toHexString(txin.sigScript);
				outRepository.save(out);
				totalInput += out.amount;
			}
			// save new outputs as unspent:
			long totalOutput = 0;
			for (int outputIndex = 0; outputIndex < tx.tx_outs.length; outputIndex++) {
				final TxOut txout = tx.tx_outs[outputIndex];
				final OutEntity o = new OutEntity();
				o.txoHash = txHash + "#" + outputIndex;
				o.amount = txout.value;
				o.pkScript = HashUtils.toHexString(txout.pk_script);
				o.sigScript = "";
				o.spent = false;
				outRepository.save(o);
				totalOutput += o.amount;
			}
			final TxEntity txEntity = new TxEntity();
			txEntity.txHash = txHash; // pk
			txEntity.blockHash = blockHash;
			txEntity.txIndex = txIndex;
			txEntity.inputCount = tx.getTxInCount();
			txEntity.outputCount = tx.getTxOutCount();
			txEntity.totalInput = totalInput;
			txEntity.totalOutput = totalOutput;
			txEntity.lockTime = tx.lock_time;
			txEntity.version = tx.version;
			txRepository.save(txEntity);
		}
		return blockEntity;
	}
}
