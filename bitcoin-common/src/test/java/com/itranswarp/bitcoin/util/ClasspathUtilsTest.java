package com.itranswarp.bitcoin.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class ClasspathUtilsTest {

	@Test
	public void testLoadAsBytes() throws IOException {
		byte[] data = ClasspathUtils.loadAsBytes("/commons-logging.properties");
		String txt = new String(data, "UTF-8");
		assertTrue(txt.contains("org.apache.commons.logging.Log"));
	}

	@Test
	public void testGetResources() throws IOException, ClassNotFoundException {
		String pkg = "com.itranswarp.bitcoin.util.sub";
		List<String> cls = ClasspathUtils.getResources("/" + pkg.replace('.', '/'), new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".class");
			}
		});
		assertEquals(3, cls.size());
		assertTrue(cls.contains("A.class"));
		assertTrue(cls.contains("B.class"));
		assertTrue(cls.contains("C.class"));
		// try load classes:
		Class.forName(pkg + "." + "A");
		Class.forName(pkg + "." + "B");
		Class.forName(pkg + "." + "C");
	}
}
