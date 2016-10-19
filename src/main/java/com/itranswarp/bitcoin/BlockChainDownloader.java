package com.itranswarp.bitcoin;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BlockChainDownloader {

	final Log log = LogFactory.getLog(getClass());

	final File dataDir;

	public BlockChainDownloader() throws IOException {
		File dir = new File(new File(".").getCanonicalFile(), "blocks.data");
		if (!dir.isDirectory()) {
			if (!dir.mkdir()) {
				throw new IOException("Cannot mkdir: " + dir.getAbsolutePath());
			}
		}
		log.info("block data directory: " + dir.getAbsolutePath());
		this.dataDir = dir;
	}

	public void download(int index) {
		log.info("downloading block #" + index + "...");
	}

	public static void main(String[] args) throws Exception {
		BlockChainDownloader downloader = new BlockChainDownloader();
		for (int i = 0; i < 10; i++) {
			downloader.download(i);
		}
	}
}
