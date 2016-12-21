package com.itranswarp.bitcoin.util;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class ClasspathUtilsTest {

	@Test
	public void testLoadAsBytes() throws IOException {
		byte[] data = ClasspathUtils.loadAsBytes("/commons-logging.properties");
		String txt = new String(data, "UTF-8");
		assertTrue(txt.contains("org.apache.commons.logging.Log"));
	}

}
