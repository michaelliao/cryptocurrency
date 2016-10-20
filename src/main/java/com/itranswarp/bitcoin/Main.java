package com.itranswarp.bitcoin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.itranswarp.bitcoin.io.BitCoinInput;
import com.itranswarp.cryptocurrency.common.Hash;

public class Main {

	public static void main(String[] args) throws Exception {
		String path = "/Users/liaoxuefeng/Bitcoin/blocks";
		BlockChainImporter importer = new BlockChainImporter();
		// importer.importFromDir(path);
		importer.importFromFile(new File("/Users/liaoxuefeng/Bitcoin/blocks/blk00000.dat"));
		// try (LittleEndianDataInputStream input = new
		// LittleEndianDataInputStream(
		// new BufferedInputStream(new FileInputStream(file)))) {
		// Block block = new Block(input);
		// System.out.println(toJson(block));
		// // block.calculateNonce();
		// }
	}

	static String toJson(Object o) throws Exception {
		SimpleModule testModule = new SimpleModule("MyModule", Version.unknownVersion());
		testModule.addSerializer(new ByteArraySerializer());
		ObjectMapper mapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.registerModule(testModule);
		return mapper.writeValueAsString(o);
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
		gen.writeString(Hash.toHexString(value));
	}
}
