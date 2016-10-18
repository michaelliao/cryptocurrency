package com.itranswarp.cryptocurrency.common;

import java.io.DataInput;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class LittleEndianDataInputStream extends FilterInputStream implements DataInput {

	public LittleEndianDataInputStream(InputStream in) {
		super(in);
	}

	public final long readVarInt() throws IOException {
		byte[] buffer = new byte[1];
		if (in.read(buffer) == 0) {
			throw new EOFException();
		}
		int ch = 0xff & buffer[0];
		if (ch < 0xfd) {
			return ch;
		}
		if (ch == 0xfd) {
			int ch1 = in.read();
			int ch2 = in.read();
			if ((ch1 | ch2) < 0) {
				throw new EOFException();
			}
			return (ch2 << 8) + (ch1 << 0);
		}
		if (ch == 0xfe) {
			return readInt();
		}
		return readLong();
	}

	@Override
	public final int read(byte b[]) throws IOException {
		return in.read(b, 0, b.length);
	}

	@Override
	public final int read(byte b[], int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	@Override
	public final void readFully(byte b[]) throws IOException {
		readFully(b, 0, b.length);
	}

	@Override
	public final void readFully(byte b[], int off, int len) throws IOException {
		if (len < 0) {
			throw new IndexOutOfBoundsException();
		}
		int n = 0;
		while (n < len) {
			int count = in.read(b, off + n, len - n);
			if (count < 0) {
				throw new EOFException();
			}
			n += count;
		}
	}

	public final int skipBytes(int n) throws IOException {
		int total = 0;
		int cur = 0;
		while ((total < n) && ((cur = (int) in.skip(n - total)) > 0)) {
			total += cur;
		}
		return total;
	}

	public final boolean readBoolean() throws IOException {
		int ch = in.read();
		if (ch < 0) {
			throw new EOFException();
		}
		return (ch != 0);
	}

	public final byte readByte() throws IOException {
		int ch = in.read();
		if (ch < 0) {
			throw new EOFException();
		}
		return (byte) (ch);
	}

	public final int readUnsignedByte() throws IOException {
		int ch = in.read();
		if (ch < 0) {
			throw new EOFException();
		}
		return ch;
	}

	public final short readShort() throws IOException {
		int ch1 = in.read();
		int ch2 = in.read();
		if ((ch1 | ch2) < 0) {
			throw new EOFException();
		}
		return (short) ((ch2 << 8) + (ch1 << 0));
	}

	public final int readUnsignedShort() throws IOException {
		int ch1 = in.read();
		int ch2 = in.read();
		if ((ch1 | ch2) < 0) {
			throw new EOFException();
		}
		return (ch2 << 8) + (ch1 << 0);
	}

	public final char readChar() throws IOException {
		int ch1 = in.read();
		int ch2 = in.read();
		if ((ch1 | ch2) < 0) {
			throw new EOFException();
		}
		return (char) ((ch2 << 8) + (ch1 << 0));
	}

	public final int readInt() throws IOException {
		int ch1 = in.read();
		int ch2 = in.read();
		int ch3 = in.read();
		int ch4 = in.read();
		if ((ch1 | ch2 | ch3 | ch4) < 0) {
			throw new EOFException();
		}
		return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
	}

	public long readUnsignedInt() throws IOException {
		int ch1 = in.read();
		int ch2 = in.read();
		int ch3 = in.read();
		int ch4 = in.read();
		if ((ch1 | ch2 | ch3 | ch4) < 0) {
			throw new EOFException();
		}
		long ln4 = ch4 & 0x00000000ffffffffL;
		return (ln4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
	}

	private byte readBuffer[] = new byte[8];

	public final long readLong() throws IOException {
		readFully(readBuffer, 0, 8);
		return (((long) readBuffer[7] << 56) + ((long) (readBuffer[6] & 255) << 48)
				+ ((long) (readBuffer[5] & 255) << 40) + ((long) (readBuffer[4] & 255) << 32)
				+ ((long) (readBuffer[3] & 255) << 24) + ((readBuffer[2] & 255) << 16) + ((readBuffer[1] & 255) << 8)
				+ ((readBuffer[0] & 255) << 0));
	}

	public final float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	public final double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	@Override
	public final String readLine() throws IOException {
		throw new UnsupportedOperationException("do not support readLine()");
	}

	public final String readUTF() throws IOException {
		throw new UnsupportedOperationException("do not support readUTF()");
	}

	public String readString() throws IOException {
		long len = readVarInt();
		if (len == 0) {
			return "";
		}
		byte[] buffer = new byte[(int) len];
		if (read(buffer) != len) {
			throw new IOException("Should read " + len + " bytes");
		}
		return new String(buffer, StandardCharsets.UTF_8);
	}

	public byte[] readBytes(int len) throws IOException {
		if (len == 0) {
			return EMPTY_BYTES;
		}
		byte[] buffer = new byte[len];
		int n = read(buffer);
		if (n == (-1)) {
			throw new EOFException("EOF");
		}
		if (n != len) {
			throw new IOException("must read " + len + " bytes but actually read " + n + " bytes.");
		}
		return buffer;
	}

	static byte[] EMPTY_BYTES = new byte[0];
}
