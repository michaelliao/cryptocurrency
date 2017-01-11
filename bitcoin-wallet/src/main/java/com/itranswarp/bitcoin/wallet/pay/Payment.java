package com.itranswarp.bitcoin.wallet.pay;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.bitcoin.BitcoinException;
import com.itranswarp.bitcoin.constant.BitcoinConstants;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.bitcoin.struct.Transaction;
import com.itranswarp.bitcoin.struct.TxOut;
import com.itranswarp.bitcoin.util.HashUtils;
import com.itranswarp.bitcoin.util.Secp256k1Utils;

public class Payment {

	final Log log = LogFactory.getLog(getClass());

	List<UTxO> outs = new ArrayList<>();
	List<PayTo> pays = new ArrayList<>();

	public Payment() {
		//
	}

	/**
	 * Collect UTXO for spend bitcoin.
	 * 
	 * @param tx
	 * @param outputIndex
	 * @return
	 */
	public Payment alloc(Transaction tx, int outputIndex, BigInteger privateKey) {
		this.outs.add(new UTxO(tx, outputIndex, privateKey));
		return this;
	}

	public Payment payTo(byte[] address, long amount) {
		PayTo p = new PayTo(address, amount);
		this.pays.add(p);
		return this;
	}

	public byte[] pay() {
		// verify:
		long total_unspend = outs.stream().mapToLong((utxo) -> {
			return utxo.out.value;
		}).sum();
		long total_spend = pays.stream().mapToLong((pay) -> {
			return pay.amount;
		}).sum();
		if (total_unspend < total_spend) {
			log.warn("Cannot create transaction for inputs < outputs.");
			throw new BitcoinException("Cannot create transaction for inputs < outputs.");
		}
		// build transaction:
		return buildRawTransaction();
	}

	byte[] buildRawTransaction() {
		BitcoinOutput output = new BitcoinOutput();
		// tx version:
		output.writeInt(BitcoinConstants.TX_VERSION);
		// number of inputs:
		output.writeVarInt(this.outs.size());
		// each inputs:
		for (UTxO utxo : this.outs) {
			// tx hash and index:
			output.write(utxo.tx.getTxHash());
			output.writeUnsignedInt(utxo.index);
			// script sig:
			output.write(createScriptSig(utxo));
			// sequence always be 0xffffff:
			output.writeUnsignedInt(0xffffffffL);
		}
		// number of outputs:
		output.writeVarInt(this.pays.size());
		// each output:
		for (PayTo pay : this.pays) {
			// btc:
			output.writeLong(pay.amount);
			// standard output script:
			// OP_DUP OP_HASH160 <pubkey> OP_EQUALVERIFY OP_CHECKSIG
			output.write(generateOutputScript(pay));
			// lock time:
			output.writeInt(0);
		}
		// sign type: SIGHASH_ALL
		output.writeInt(BitcoinConstants.SIGHASH_ALL);
		return output.toByteArray();
	}

	byte[] createScriptSig(UTxO thisUtxo) {
		// create payload:
		BitcoinOutput output = new BitcoinOutput();
		// tx version:
		output.writeInt(BitcoinConstants.TX_VERSION);
		// number of inputs:
		output.writeVarInt(this.outs.size());
		// each inputs:
		for (UTxO utxo : this.outs) {
			// tx hash and index:
			output.write(utxo.tx.getTxHash());
			output.writeUnsignedInt(utxo.index);
			if (thisUtxo == utxo) {
				// pk script:
				output.writeVarInt(utxo.out.pk_script.length);
				output.write(utxo.out.pk_script);
			} else {
				output.writeVarInt(0);
			}
			// sequence always be 0xffffff:
			output.writeUnsignedInt(0xffffffffL);
		}
		// number of outputs:
		output.writeVarInt(this.pays.size());
		// each output:
		for (PayTo pay : this.pays) {
			// btc:
			output.writeLong(pay.amount);
			// standard output script:
			// OP_DUP OP_HASH160 <pubkey> OP_EQUALVERIFY OP_CHECKSIG
			output.write(generateOutputScript(pay));
			// lock time:
			output.writeInt(0);
		}
		// sign type: SIGHASH_ALL
		output.writeInt(BitcoinConstants.SIGHASH_ALL);
		byte[] payload = output.toByteArray();
		byte[] dhash = HashUtils.doubleSha256(payload);
		byte[] sig = Secp256k1Utils.sign(dhash, thisUtxo.privateKey);
		byte[] pubkey = Secp256k1Utils.toPublicKey(thisUtxo.privateKey);
		BitcoinOutput sigOutput = new BitcoinOutput();
		sigOutput.writeVarInt(sig.length + 1 + pubkey.length);
		sigOutput.write(sig);
		sigOutput.writeByte(BitcoinConstants.SIGHASH_ALL);
		sigOutput.write(pubkey);
		return sigOutput.toByteArray();
	}

	byte[] generateOutputScript(PayTo pay) {
		BitcoinOutput output = new BitcoinOutput();
		// 25 bytes of scripts:
		output.writeVarInt(25);
		// OP_DUP:
		output.writeByte(0x76);
		// OP_HASH160:
		output.writeByte(0xa9);
		// 20 byte pubkey hash160:
		output.write(HashUtils.ripeMd160(HashUtils.sha256(pay.address)));
		// OP_EQUALVERIFY:
		output.writeByte(0x88);
		// OP_CHECKSIG:
		output.writeByte(0xac);
		return output.toByteArray();
	}
}

class UTxO {
	final Transaction tx;
	final int index;
	final TxOut out;
	final BigInteger privateKey;

	public UTxO(Transaction tx, int index, BigInteger privateKey) {
		this.tx = tx;
		this.index = index;
		this.out = tx.tx_outs[index];
		this.privateKey = privateKey;
	}

}

class PayTo {
	final byte[] address;
	final long amount;

	public PayTo(byte[] address, long amount) {
		this.address = address;
		this.amount = amount;
	}
}
