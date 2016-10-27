package com.itranswarp.bitcoin.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BitcoinFileManager {

	final Log log = LogFactory.getLog(getClass());

	final String baseDir;

	private BitcoinFileManager() {
		String path = null;
		try {
			path = new File(".").getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		File baseDir = new File(path, "bitcoin.datastore");
		if (!baseDir.isDirectory()) {
			if (!baseDir.mkdir()) {
				log.error("Cannot create dir for bitcoin data: " + baseDir.getAbsolutePath());
				throw new RuntimeException("Cannot create dir for bitcoin data: " + baseDir.getAbsolutePath());
			}
		}
		this.baseDir = baseDir.getAbsolutePath();
		log.info("Set current data dir: " + this.baseDir);
	}

	public File getFile(String relativePath) {
		return new File(baseDir, relativePath);
	}

	public void writeFile(String relativePath, String content) throws IOException {
		File f = getFile(relativePath);
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"))) {
			writer.write(content);
		}
	}

	public String loadFile(String relativePath) throws IOException {
		File f = getFile(relativePath);
		int n;
		char[] buffer = new char[1024];
		CharArrayWriter writer = new CharArrayWriter();
		try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		}
		return writer.toString();
	}

	private static BitcoinFileManager INSTANCE = new BitcoinFileManager();

	public static BitcoinFileManager getInstance() {
		return INSTANCE;
	}

}
