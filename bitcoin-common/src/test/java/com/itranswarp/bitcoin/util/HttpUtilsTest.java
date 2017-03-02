package com.itranswarp.bitcoin.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class HttpUtilsTest {

	@Test
	public void testGetJson() {
		String url = "https://blockchain.info/address/143RDGMuZjNkcotCE3CkGeE4mu9HnNahit?format=json";
		String json = HttpUtils.getJson(url);
		assertTrue(json.contains("215d6b843662b3b12f6cebf991870b8d653ddb31"));
		assertTrue(json.contains("143RDGMuZjNkcotCE3CkGeE4mu9HnNahit"));
		assertTrue(json.contains("final_balance"));
		System.out.println(json);
	}

	@Test
	public void testGetJsonWithParams() {
		String url = "https://blockchain.info/address/143RDGMuZjNkcotCE3CkGeE4mu9HnNahit";
		String json = HttpUtils.getJson(url, MapUtils.of("format", "json"));
		assertTrue(json.contains("215d6b843662b3b12f6cebf991870b8d653ddb31"));
		assertTrue(json.contains("143RDGMuZjNkcotCE3CkGeE4mu9HnNahit"));
		assertTrue(json.contains("final_balance"));
		System.out.println(json);
	}

}
