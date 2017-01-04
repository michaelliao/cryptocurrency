package com.itranswarp.bitcoin.p2p.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.struct.Block;

/**
 * Build P2P message:
 * https://en.bitcoin.it/wiki/Protocol_documentation#Message_structure
 * 
 * @author liaoxuefeng
 */
public class BlockMessage extends Message {

	static final Log log = LogFactory.getLog(BlockMessage.class);

	public Block block;

	public BlockMessage() {
		super("block");
	}

	public BlockMessage(byte[] payload) throws IOException {
		super("block");
		try (BitcoinInput input = new BitcoinInput(new ByteArrayInputStream(payload))) {
			this.block = new Block(input);
		}
	}

	@Override
	protected byte[] getPayload() {
		return this.block.toByteArray();
	}

	/**
	 * Validate block hash.
	 */
	public boolean validateHash() {
		byte[] merkleHash = this.block.calculateMerkleHash();
		if (!Arrays.equals(merkleHash, this.block.header.merkleHash)) {
			log.error("Validate merckle hash failed.");
			return false;
		}
		// TODO: validate bits:
		return true;
	}

	@Override
	public String toString() {
		return "BlockMessage(txnCount=" + this.block.txns.length + ")";
	}

}
