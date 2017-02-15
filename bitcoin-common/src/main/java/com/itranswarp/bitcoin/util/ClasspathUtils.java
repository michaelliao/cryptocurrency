package com.itranswarp.bitcoin.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ClasspathUtils {

	public static byte[] loadAsBytes(String classpath) throws IOException {
		try (InputStream input = ClasspathUtils.class.getResourceAsStream(classpath)) {
			if (input == null) {
				throw new IOException("Resource not found: " + classpath);
			}
			int n;
			byte[] buffer = new byte[1024];
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			while ((n = input.read(buffer)) != (-1)) {
				output.write(buffer, 0, n);
			}
			return output.toByteArray();
		}
	}

	/**
	 * Get resource names under specific path.
	 * 
	 * @param path
	 * @param filter
	 * @return
	 * @throws IOException
	 */
	public static List<String> getResources(String path, FilenameFilter filter) throws IOException {
		try (InputStream input = ClasspathUtils.class.getResourceAsStream(path)) {
			if (input == null) {
				throw new IOException("Resource " + path + " not found in classpath.");
			}
			try (BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
				String resource = null;
				File dir = new File(path);
				List<String> filenames = new ArrayList<>();
				while ((resource = br.readLine()) != null) {
					if (filter.accept(dir, resource)) {
						filenames.add(resource);
					}
				}
				return filenames;
			}
		}
	}
}
