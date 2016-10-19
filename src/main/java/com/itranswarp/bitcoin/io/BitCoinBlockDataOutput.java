package com.itranswarp.bitcoin.io;

import java.io.IOException;
import java.io.OutputStream;

public final class BitCoinBlockDataOutput implements AutoCloseable {

	private OutputStream out;

	public BitCoinBlockDataOutput(OutputStream out) {
		this.out = out;
	}

	public void write(byte[] bytes) throws IOException {
		out.write(bytes);
	}

	public void writeByte(int v) throws IOException {
		out.write(v);
	}

	public void writeInt(int v) throws IOException {
		out.write(0xff & v);
		out.write(0xff & (v >> 8));
		out.write(0xff & (v >> 16));
		out.write(0xff & (v >> 24));
	}

	public void writeLong(long v) throws IOException {
		out.write((int) (0xff & v));
		out.write((int) (0xff & (v >> 8)));
		out.write((int) (0xff & (v >> 16)));
		out.write((int) (0xff & (v >> 24)));
		out.write((int) (0xff & (v >> 32)));
		out.write((int) (0xff & (v >> 40)));
		out.write((int) (0xff & (v >> 48)));
		out.write((int) (0xff & (v >> 56)));
	}

	public void writeVarInt(long n) throws IOException {
		if (n < 0xfd) {
			writeByte((int) n);
		} else if (n == 0xfd) {
			writeByte(0xfd);
			writeByte((int) (n & 0xff));
			writeByte((int) ((n >> 8) & 0xff));
		} else if (n == 0xfe) {
			writeByte(0xfe);
			writeInt((int) n);
		} else {
			writeByte(0xff);
			writeLong(n);
		}
	}

	public void writeUnsignedInt(long ln) throws IOException {
		int n = (int) (0xffffffff & ln);
		writeInt(n);
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

}
