package com.itranswarp.bitcoin.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class BitCoinOutput {

	ByteArrayOutputStream out;

	public BitCoinOutput() {
		this.out = new ByteArrayOutputStream(1024);
	}

	public BitCoinOutput write(byte[] bytes) {
		try {
			out.write(bytes);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public BitCoinOutput writeByte(int v) {
		out.write(v);
		return this;
	}

	public BitCoinOutput writeShort(short v) {
		out.write(0xff & v);
		out.write(0xff & (v >> 8));
		return this;
	}

	public BitCoinOutput writeInt(int v) {
		out.write(0xff & v);
		out.write(0xff & (v >> 8));
		out.write(0xff & (v >> 16));
		out.write(0xff & (v >> 24));
		return this;
	}

	public BitCoinOutput writeLong(long v) {
		out.write((int) (0xff & v));
		out.write((int) (0xff & (v >> 8)));
		out.write((int) (0xff & (v >> 16)));
		out.write((int) (0xff & (v >> 24)));
		out.write((int) (0xff & (v >> 32)));
		out.write((int) (0xff & (v >> 40)));
		out.write((int) (0xff & (v >> 48)));
		out.write((int) (0xff & (v >> 56)));
		return this;
	}

	public BitCoinOutput writeVarInt(long n) {
		if (n < 0xfd) {
			writeByte((int) n);
		} else if (n <= 0xffff) {
			writeByte(0xfd);
			writeByte((int) (n & 0xff));
			writeByte((int) ((n >> 8) & 0xff));
		} else if (n <= 0xffffffff) {
			writeByte(0xfe);
			writeInt((int) n);
		} else {
			writeByte(0xff);
			writeLong(n);
		}
		return this;
	}

	public BitCoinOutput writeUnsignedInt(long ln) {
		int n = (int) (0xffffffff & ln);
		writeInt(n);
		return this;
	}

	public BitCoinOutput writeUnsignedShort(int i) {
		short n = (short) (0xffff & i);
		writeShort(n);
		return this;
	}

	public BitCoinOutput writeString(String str) {
		byte[] bs = str.getBytes(StandardCharsets.UTF_8);
		writeVarInt(bs.length);
		write(bs);
		return this;
	}

	public byte[] toByteArray() {
		return out.toByteArray();
	}

}
