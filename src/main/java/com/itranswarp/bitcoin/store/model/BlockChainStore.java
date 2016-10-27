package com.itranswarp.bitcoin.store.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.bitcoin.message.BlockMessage;
import com.itranswarp.bitcoin.store.Database;

public class BlockChainStore {

	final Log log = LogFactory.getLog(getClass());
	final Database database;

	public BlockChainStore(String dbfile) {
		log.info("Using block chain db: " + dbfile);
		this.database = Database.init(dbfile);
	}

	public void addBlock(BlockMessage msg) {
		//
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

	public String getLastBlockHash() {
		// TODO Auto-generated method stub
		return null;
	}

}
