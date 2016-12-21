package com.itranswarp.bitcoin.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {

	public static String toJson(Object o) {
		ObjectMapper mapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			return mapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void printJson(Object o) {
		System.out.println(toJson(o));
	}

	public static <T> T fromJson(Class<T> clazz, String s) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return mapper.readValue(s, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
