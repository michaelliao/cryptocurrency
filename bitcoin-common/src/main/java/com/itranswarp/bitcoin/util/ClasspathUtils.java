package com.itranswarp.bitcoin.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
}
