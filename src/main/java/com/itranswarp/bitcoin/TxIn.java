package com.itranswarp.bitcoin;

import java.io.IOException;

import com.itranswarp.cryptocurrency.common.LittleEndianDataInputStream;
import com.itranswarp.cryptocurrency.common.LittleEndianDataOutputStream;

public class TxIn {

	OutPoint previousOutput;
	long signatureLength;
	byte[] signature;
	long sequence; // uint32, Transaction version as defined by the sender.
					// Intended for "replacement" of transactions when
					// information is updated before inclusion into a block.

	public TxIn(LittleEndianDataInputStream input) throws IOException {
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

	public void dump(LittleEndianDataOutputStream output) throws IOException {
		this.previousOutput.dump(output);
		output.writeVarInt(this.signatureLength);
		output.write(this.signature);
		output.writeUnsignedInt(this.sequence);
	}

}
