package com.itranswarp.bitcoin;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.cryptocurrency.common.JsonUtil;

public class BlockChainImporter {

	final Log log = LogFactory.getLog(getClass());

	static final Pattern pattern = Pattern.compile("^blk(\\d+)\\.dat$");

	public void importFromDir(String path) throws IOException {
		File d = new File(path);
		if (!d.isDirectory()) {
			throw new IOException("Dir is not exist: " + path);
		}
		String[] fs = d.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return pattern.matcher(name).matches();
			}
		});
		Arrays.sort(fs, new Comparator<String>() {
			@Override
			public int compare(String f1, String f2) {
				return f1.compareTo(f2);
			}
		});
		for (String f : fs) {
			File file = new File(d, f);
			importFromFile(file);
		}
	}

	void importFromFile(File file) throws IOException {
		log.info("Import blocks from file: " + file.getAbsolutePath() + "...");
		try (BitcoinInput input = new BitcoinInput(new BufferedInputStream(new FileInputStream(file)))) {
			for (int i = 0; i < 2; i++) {
				// read magic number: 0xd9b4bef9
				int magic = input.readInt();
				if (magic != BitcoinConstants.MAGIC) {
					throw new RuntimeException("Bad magic number.");
				}
				Block block = new Block(input);
				log.info(JsonUtil.toJson(block));
			}
		} catch (EOFException eof) {
			// ignore
		}
	}
}
