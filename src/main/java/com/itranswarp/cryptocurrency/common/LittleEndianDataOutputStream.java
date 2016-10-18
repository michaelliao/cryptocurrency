package com.itranswarp.cryptocurrency.common;

import java.io.DataOutput;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class LittleEndianDataOutputStream extends FilterOutputStream implements DataOutput {

	public LittleEndianDataOutputStream(OutputStream out) {
		super(out);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

	@Override
	public void writeBoolean(boolean v) throws IOException {
		out.write(v ? 1 : 0);
	}

	@Override
	public void writeByte(int v) throws IOException {
		out.write(v);
	}

	@Override
	public void writeBytes(String s) throws IOException {
		throw new UnsupportedOperationException("Does not support write String.");
	}

	@Override
	public void writeChar(int v) throws IOException {
		throw new UnsupportedOperationException("Does not support write char.");
	}

	@Override
	public void writeChars(String s) throws IOException {
		throw new UnsupportedOperationException("Does not support write chars.");
	}

	@Override
	public void writeDouble(double v) throws IOException {
		writeLong(Double.doubleToLongBits(v));
	}

	@Override
	public void writeFloat(float v) throws IOException {
		writeInt(Float.floatToIntBits(v));
	}

	@Override
	public void writeInt(int v) throws IOException {
		out.write(0xff & v);
		out.write(0xff & (v >> 8));
		out.write(0xff & (v >> 16));
		out.write(0xff & (v >> 24));
	}

	@Override
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

	@Override
	public void writeShort(int v) throws IOException {
		out.write(0xff & v);
		out.write(0xff & (v >> 8));
	}

	@Override
	public void writeUTF(String str) throws IOException {
		throw new UnsupportedOperationException("Does not support write UTF.");
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

}
