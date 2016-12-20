package com.itranswarp.bitcoin.struct;

import java.io.IOException;

import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.bitcoin.util.BytesUtils;

public class TxIn {

	public OutPoint previousOutput;
	public long signatureLength;
	public byte[] signature;

	/**
	 * uint32, Transaction version as defined by the sender. Intended for
	 * "replacement" of transactions when information is updated before
	 * inclusion into a block.
	 */
	public long sequence;

	public TxIn(BitcoinInput input) throws IOException {
		this.previousOutput = new OutPoint(input);
		this.signatureLength = input.readVarInt();
		this.signature = input.readBytes((int) signatureLength);
		this.sequence = input.readUnsignedInt();
	}

	public boolean isCoinbase() {
		return this.previousOutput != null && this.previousOutput.hash != null
				&& BytesUtils.isZeros(this.previousOutput.hash);
	}

	public byte[] toByteArray() {
		return new BitcoinOutput().write(this.previousOutput.toByteArray()).writeVarInt(this.signatureLength)
				.write(this.signature).writeUnsignedInt(this.sequence).toByteArray();
	}

	public void validate() {

		// throw new ValidateException("Verify signature failed.");
	}
}
