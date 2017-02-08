package com.itranswarp.bitcoin.wallet.pay;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.bitcoin.BitcoinException;
import com.itranswarp.bitcoin.constant.BitcoinConstants;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.bitcoin.keypair.ECDSAKeyPair;
import com.itranswarp.bitcoin.util.HashUtils;
import com.itranswarp.bitcoin.util.Secp256k1Utils;

public class Payment {

	final Log log = LogFactory.getLog(getClass());

	List<UTxO> utxos = new ArrayList<>();
	List<PayTo> payTos = new ArrayList<>();

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
	public Payment alloc(String privateKey, String txHash, int index, long amount, byte[] pkScript) {
		ECDSAKeyPair kp = ECDSAKeyPair.of(privateKey);
		this.utxos.add(new UTxO(kp.getPrivateKey(), kp.toPublicKey(), txHash, index, amount, pkScript));
		return this;
	}

	public Payment payTo(String address, long amount) {
		this.payTos.add(new PayTo(address, amount));
		return this;
	}

	public byte[] buildTransaction() {
		// verify:
		if (utxos.isEmpty()) {
			throw new IllegalArgumentException("No UTXO alloc.");
		}
		if (payTos.isEmpty()) {
			throw new IllegalArgumentException("No pay to specified.");
		}
		long total_unspend = utxos.stream().mapToLong((utxo) -> {
			return utxo.amount;
		}).sum();
		long total_spend = payTos.stream().mapToLong((pay) -> {
			return pay.amount;
		}).sum();
		if (total_unspend < total_spend) {
			log.warn("Cannot create transaction for inputs < outputs.");
			throw new BitcoinException("Cannot create transaction for inputs < outputs.");
		}
		log.info("Build transaction: " + toBtc(total_unspend) + " -> " + toBtc(total_spend) + " (fees: "
				+ toBtc(total_unspend - total_spend) + ")");
		// build transaction:
		return buildRawTransaction();
	}

	byte[] buildRawTransaction() {
		BitcoinOutput output = new BitcoinOutput();
		// tx version:
		output.writeInt(BitcoinConstants.TX_VERSION);
		// number of inputs:
		output.writeVarInt(this.utxos.size());
		// each inputs:
		for (UTxO utxo : this.utxos) {
			// tx hash and index:
			output.write(HashUtils.toBytesAsLittleEndian(utxo.txHash));
			output.writeUnsignedInt(utxo.index);
			// script sig:
			output.write(createScriptSig(utxo));
			// sequence always be 0xffffff:
			output.writeUnsignedInt(0xffffffffL);
		}
		// number of outputs:
		output.writeVarInt(this.payTos.size());
		// each output:
		for (PayTo pay : this.payTos) {
			// btc:
			output.writeLong(pay.amount);
			// standard output script:
			// OP_DUP OP_HASH160 <pubkey> OP_EQUALVERIFY OP_CHECKSIG
			output.write(generateOutputScript(pay));
		}
		// lock time:
		output.writeInt(0);
		return output.toByteArray();
	}

	byte[] createScriptSig(UTxO thisUtxo) {
		// create payload:
		BitcoinOutput output = new BitcoinOutput();
		// tx version:
		output.writeInt(BitcoinConstants.TX_VERSION);
		// number of inputs:
		output.writeVarInt(this.utxos.size());
		// each inputs:
		for (UTxO utxo : this.utxos) {
			// tx hash and index:
			output.write(HashUtils.toBytesAsLittleEndian(utxo.txHash));
			output.writeUnsignedInt(utxo.index);
			if (thisUtxo == utxo) {
				// pk script:
				output.writeVarInt(utxo.pkScript.length);
				output.write(utxo.pkScript);
			} else {
				output.writeVarInt(0);
			}
			// sequence always be 0xffffffff:
			output.writeUnsignedInt(0xffffffffL);
		}
		// number of outputs:
		output.writeVarInt(this.payTos.size());
		// each output:
		for (PayTo payTo : this.payTos) {
			// btc:
			output.writeLong(payTo.amount);
			// standard output script:
			// OP_DUP OP_HASH160 <pubkey> OP_EQUALVERIFY OP_CHECKSIG
			output.write(generateOutputScript(payTo));
		}
		// lock time:
		output.writeInt(0);
		// sign type: SIGHASH_ALL
		output.writeInt(BitcoinConstants.SIGHASH_ALL);
		// sign it:
		byte[] payload = output.toByteArray();
		byte[] dhash = HashUtils.doubleSha256(payload);
		byte[] sig = Secp256k1Utils.sign(dhash, thisUtxo.privateKey);
		byte[] pubkey = thisUtxo.publicKey;
		log.info("sig: " + HashUtils.toHexString(sig));
		log.info("pk: " + HashUtils.toHexString(pubkey));
		BitcoinOutput sigOutput = new BitcoinOutput();
		sigOutput.writeVarInt(sig.length + 3 + pubkey.length);
		sigOutput.writeByte(sig.length + 1);
		sigOutput.write(sig);
		sigOutput.writeByte(BitcoinConstants.SIGHASH_ALL);
		sigOutput.writeByte(pubkey.length);
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
		// DATA 20:
		output.writeByte(20);
		// 20 byte pubkey hash160:
		output.write(pay.address);
		// OP_EQUALVERIFY:
		output.writeByte(0x88);
		// OP_CHECKSIG:
		output.writeByte(0xac);
		return output.toByteArray();
	}

	String toBtc(long satoshi) {
		return "BTC " + BigDecimal.valueOf(satoshi).divide(N).toString();
	}

	final static BigDecimal N = BigDecimal.valueOf(100000000L);
}

class UTxO {
	final String txHash;
	final int index;
	final long amount;
	final byte[] pkScript;
	final BigInteger privateKey;
	final byte[] publicKey;

	public UTxO(BigInteger privateKey, byte[] publicKey, String txHash, int index, long amount, byte[] pkScript) {
		this.txHash = txHash;
		this.index = index;
		this.amount = amount;
		this.pkScript = pkScript;
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}

}

class PayTo {
	final byte[] address;
	final long amount;

	public PayTo(String address, long amount) {
		this.address = Secp256k1Utils.publicKeyAddressToBytes(address);
		this.amount = amount;
	}
}
