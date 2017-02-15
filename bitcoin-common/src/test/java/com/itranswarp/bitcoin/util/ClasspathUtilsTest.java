package com.itranswarp.bitcoin.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.itranswarp.bitcoin.util.sub.A;
import com.itranswarp.bitcoin.util.sub.B;
import com.itranswarp.bitcoin.util.sub.C;

public class ClasspathUtilsTest {

	@Test
	public void testLoadAsBytes() throws IOException {
		byte[] data = ClasspathUtils.loadAsBytes("/commons-logging.properties");
		String txt = new String(data, "UTF-8");
		assertTrue(txt.contains("org.apache.commons.logging.Log"));
	}

	@Test
	public void testGetClasses() throws IOException {
		String pkg = "com.itranswarp.bitcoin.util.sub";
		List<Class<?>> cls = ClasspathUtils.getClasses(pkg);
		assertEquals(3, cls.size());
		assertTrue(cls.contains(A.class));
		assertTrue(cls.contains(B.class));
		assertTrue(cls.contains(C.class));
	}

	@Test
	public void testGetClassesInJar() throws IOException {
		String pkg = "com.fasterxml.jackson.core.type";
		List<Class<?>> cls = ClasspathUtils.getClasses(pkg);
		assertEquals(2, cls.size());
		assertTrue(cls.contains(ResolvedType.class));
		assertTrue(cls.contains(TypeReference.class));

	}
}
