package com.itranswarp.bitcoin.struct;

import java.io.IOException;

import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;

public class TxIn {

	public OutPoint previousOutput;
	public long signatureLength;
	public byte[] signature;
	public long sequence; // uint32, Transaction version as defined by the
							// sender.
	// Intended for "replacement" of transactions when
	// information is updated before inclusion into a block.

	public TxIn(BitcoinInput input) throws IOException {
		this.previousOutput = new OutPoint(input);
		this.signatureLength = input.readVarInt();
		this.signature = input.readBytes((int) signatureLength);
		this.sequence = input.readUnsignedInt();
	}

	public OutPoint getPreviousOutput() {
		return previousOutput;
	}

	public void setPreviousOutput(OutPoint previousOutput) {
		this.previousOutput = previousOutput;
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	public long getSequence() {
		return sequence;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}

	public boolean isCoinbase() {
		return this.previousOutput != null && this.previousOutput.hash != null && isZero(this.previousOutput.hash);
	}

	public byte[] toByteArray() {
		return new BitcoinOutput().write(this.previousOutput.toByteArray()).writeVarInt(this.signatureLength)
				.write(this.signature).writeUnsignedInt(this.sequence).toByteArray();
	}

	private boolean isZero(byte[] bs) {
		for (byte b : bs) {
			if (b != 0) {
				return false;
			}
		}
		return true;
	}

}
