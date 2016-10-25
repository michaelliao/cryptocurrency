package com.itranswarp.cryptocurrency.common;

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JsonUtil {

	public static String toJson(Object o) {

		ObjectMapper mapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.registerModule(MODULE);
		try {
			return mapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
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

	static final SimpleModule MODULE;

	static {
		MODULE = new SimpleModule("CustomJSONModule", Version.unknownVersion(),
				Arrays.asList(new ByteArraySerializer()));
	}
}

class ByteArraySerializer extends JsonSerializer<byte[]> {

	@Override
	public Class<byte[]> handledType() {
		return byte[].class;
	}

	@Override
	public void serialize(byte[] value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		gen.writeString(Hash.toHexStringAsLittleEndian(value));
	}
}
