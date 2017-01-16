package com.itranswarp.bitcoin.explorer.service;

import javax.transaction.Transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itranswarp.bitcoin.constant.BitcoinConstants;
import com.itranswarp.bitcoin.explorer.domain.BlockEntity;
import com.itranswarp.bitcoin.explorer.domain.OutputEntity;
import com.itranswarp.bitcoin.explorer.domain.TxEntity;
import com.itranswarp.bitcoin.explorer.repository.BlockRepository;
import com.itranswarp.bitcoin.explorer.repository.OutputRepository;
import com.itranswarp.bitcoin.explorer.repository.TxRepository;
import com.itranswarp.bitcoin.script.ScriptEngine;
import com.itranswarp.bitcoin.struct.Block;
import com.itranswarp.bitcoin.struct.Transaction;
import com.itranswarp.bitcoin.struct.TxIn;
import com.itranswarp.bitcoin.struct.TxOut;
import com.itranswarp.bitcoin.util.HashUtils;

@Component
public class BlockProcessor {

	final Log log = LogFactory.getLog(getClass());

	@Autowired
	BlockRepository blockRepository;

	@Autowired
	TxRepository txRepository;

	@Autowired
	OutputRepository outputRepository;

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
		blockEntity.txCount = block.txns.length; // how many tx
		blockEntity.bits = block.header.bits;
		blockEntity.merkleHash = HashUtils.toHexStringAsLittleEndian(block.header.merkleHash);
		blockEntity.nonce = block.header.nonce;
		blockEntity.prevHash = prevHash;
		blockEntity.height = height;
		blockEntity.timestamp = block.header.timestamp;
		blockEntity.version = block.header.version;
		blockEntity.size = block.toByteArray().length;
		blockRepository.save(blockEntity);
		// store tx:
		for (int txIndex = 0; txIndex < block.txns.length; txIndex++) {
			final Transaction tx = block.txns[txIndex];
			final String txHash = HashUtils.toHexStringAsLittleEndian(tx.getTxHash());
			// mark previous outputs as spent:
			long totalInput = 0;
			long inputCount = 0;
			for (int txinIndex = 0; txinIndex < tx.tx_ins.length; txinIndex++) {
				TxIn txin = tx.tx_ins[txinIndex];
				// ignore coin base:
				if (!HashUtils.toHexStringAsLittleEndian(txin.previousOutput.hash).equals(BitcoinConstants.ZERO_HASH)) {
					final OutputEntity out = outputRepository
							.findOne(HashUtils.toHexStringAsLittleEndian(txin.previousOutput.hash) + "#"
									+ txin.previousOutput.index);
					// spent:
					out.txinHash = txHash;
					out.txinIndex = txinIndex;
					out.sigScript = HashUtils.toHexString(txin.sigScript);
					outputRepository.save(out);
					log.info("Mark output " + out.outputHash + " as spent...");
					totalInput += out.amount;
					inputCount++;
				}
			}
			// save new outputs as unspent:
			long totalOutput = 0;
			for (int txoutIndex = 0; txoutIndex < tx.tx_outs.length; txoutIndex++) {
				final TxOut txout = tx.tx_outs[txoutIndex];
				final OutputEntity o = new OutputEntity();
				o.outputHash = txHash + "#" + txoutIndex;
				o.txoutHash = txHash;
				o.txoutIndex = txoutIndex;
				o.amount = txout.value;
				o.pkScript = HashUtils.toHexString(txout.pk_script);
				o.sigScript = "";
				o.txinHash = "";
				o.txinIndex = 0;
				o.address = ScriptEngine.parse(EMPTY_BYTES, txout.pk_script).getExtractAddress();
				outputRepository.save(o);
				log.info("Create new unspent output " + o.outputHash + "...");
				totalOutput += o.amount;
			}
			final TxEntity txEntity = new TxEntity();
			txEntity.txHash = txHash; // pk
			txEntity.blockHash = blockHash;
			txEntity.txIndex = txIndex;
			txEntity.inputCount = inputCount;
			txEntity.outputCount = tx.getTxOutCount();
			txEntity.totalInput = totalInput;
			txEntity.totalOutput = totalOutput;
			txEntity.lockTime = tx.lock_time;
			txEntity.version = tx.version;
			txRepository.save(txEntity);
			log.info("Create new transaction " + txEntity.txHash + "...");
		}
		return blockEntity;
	}

	static final byte[] EMPTY_BYTES = new byte[0];
}
