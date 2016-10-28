package com.itranswarp.bitcoin.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.bitcoin.BitcoinConstants;
import com.itranswarp.bitcoin.message.BlockMessage;
import com.itranswarp.bitcoin.store.model.BlockEntity;
import com.itranswarp.bitcoin.store.model.TxEntity;
import com.itranswarp.bitcoin.store.model.UtxoEntity;
import com.itranswarp.bitcoin.struct.TxIn;
import com.itranswarp.bitcoin.struct.TxOut;
import com.itranswarp.bitcoin.util.HashUtils;

public class BlockChainStore {

	static final byte[] GENESIS_BLOCK_DATA = HashUtils.toBytes(
			"0100000000000000000000000000000000000000000000000000000000000000000000003ba3edfd7a7b12b27ac72c3e67768f617fc81bc3888a51323a9fb8aa4b1e5e4a29ab5f49ffff001d1dac2b7c0101000000010000000000000000000000000000000000000000000000000000000000000000ffffffff4d04ffff001d0104455468652054696d65732030332f4a616e2f32303039204368616e63656c6c6f72206f6e206272696e6b206f66207365636f6e64206261696c6f757420666f722062616e6b73ffffffff0100f2052a01000000434104678afdb0fe5548271967f1a67130b7105cd6a828e03909a67962e0ea1f61deb649f6bc3f4cef38c4f35504e51ec112de5c384df7ba0b8d578a4c702b6bf11d5fac00000000");

	final Log log = LogFactory.getLog(getClass());
	final Database database;

	public BlockChainStore(String dbfile) {
		log.info("Using block chain db: " + dbfile);
		this.database = Database.init(dbfile);
		// init genesis block:
		if (null == this.database.getById(BlockEntity.class, BitcoinConstants.GENESIS_HASH)) {
			try {
				addBlock(new BlockMessage(GENESIS_BLOCK_DATA));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void addBlock(BlockMessage msg) {
		if (!msg.validateHash()) {
			throw new ValidateException("Validate block failed.");
		}
		final int height = database.queryForInt(BlockEntity.class, "count(*)", null);
		final String blockHash = HashUtils.toHexStringAsLittleEndian(msg.getBlockHash());
		log.info("Add block #" + height + ": " + blockHash);
		// check prev block:
		if (height != 0) {
			final String prevHash = HashUtils.toHexStringAsLittleEndian(msg.header.prevHash);
			BlockEntity lastBlock = getLastBlock();
			if (lastBlock == null) {
				throw new ValidateException("prevHash not found.");
			}
			if (lastBlock.blockHash.equals(prevHash)) {
				// add as last block:
				BlockEntity block = new BlockEntity();
				block.blockHeight = height;
				block.blockHash = blockHash;
				block.previousHash = prevHash;
				block.bits = msg.header.bits;
				block.nonce = msg.header.nonce;
				block.timestamp = msg.header.timestamp;
				block.merkleHash = HashUtils.toHexStringAsLittleEndian(msg.header.merkleHash);
				block.numOfTx = msg.txns.length;

				// process tx in this block:
				List<TxEntity> txs = new ArrayList<TxEntity>();
				List<TxIn> txins = new ArrayList<TxIn>();
				List<TxOut> txouts = new ArrayList<TxOut>();
				List<UtxoEntity> spendTxos = new ArrayList<UtxoEntity>();
				List<UtxoEntity> unspendTxos = new ArrayList<UtxoEntity>();
				AtomicBoolean coinBase = new AtomicBoolean(false);
				Arrays.stream(msg.txns).forEach((tx) -> {
					TxEntity txe = new TxEntity();
					txe.txHash = HashUtils.toHexStringAsLittleEndian(tx.getTxHash());
					txe.inputs = tx.tx_ins.length;
					txe.outputs = tx.tx_outs.length;
					txe.blockHash = blockHash;
					txe.version = tx.version;
					txe.lockTime = tx.lock_time;
					txs.add(txe);

					// check txin:
					AtomicLong inAmount = new AtomicLong(0);
					AtomicLong outAmount = new AtomicLong(0);
					Arrays.stream(tx.tx_ins).forEach((txin) -> {
						String utxoHash = HashUtils.toHexStringAsLittleEndian(txin.previousOutput.hash);
						if (utxoHash.equals(BitcoinConstants.ZERO_HASH)) {
							// coin base:
							if (coinBase.get()) {
								throw new ValidateException("Multiple coin base input.");
							}
							coinBase.set(true);
						} else {
							UtxoEntity utxo = this.database.getById(UtxoEntity.class, utxoHash);
							if (utxo == null) {
								throw new ValidateException("previous output not found: " + utxoHash);
							}
							spendTxos.add(utxo);
							inAmount.addAndGet(utxo.amount);
						}
					});
					// check txout:
					Arrays.stream(tx.tx_outs).forEach((txout) -> {
						UtxoEntity utxo = new UtxoEntity();
						utxo.blockId = blockHash;
						utxo.txHash = txe.txHash;
						utxo.amount = txout.value;
						unspendTxos.add(utxo);
						outAmount.addAndGet(utxo.amount);
					});
				});
				database.insert(block);
			} else {
				log.warn("prevHash not match last block.");
				// check:
				BlockEntity target = database.getById(BlockEntity.class, prevHash);
				if (target == null) {
					throw new ValidateException("previous block not exist.");
				}
				// block chain may forking:
				log.warn("block chain may forking!");
			}
		}
	}

	public BlockEntity getLastBlock() {
		List<BlockEntity> entities = database.queryForList(BlockEntity.class, null, "height desc limit 1");
		if (entities.isEmpty()) {
			return null;
		}
		return entities.get(0);
	}

	public String getLastBlockHash() {
		BlockEntity block = getLastBlock();
		return block == null ? BitcoinConstants.GENESIS_HASH : block.blockHash;
	}

	public void removeBlock() {
		//
	}

	public BlockEntity getLatestBlock() {
		return null;
	}

	public long getUtxoAmount() {
		return 0;
	}

}
