package com.itranswarp.bitcoin.message;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Instant;
import java.util.Arrays;

import com.itranswarp.bitcoin.BitcoinConstants;
import com.itranswarp.bitcoin.io.BitCoinInput;
import com.itranswarp.bitcoin.io.BitCoinOutput;
import com.itranswarp.bitcoin.util.NetworkUtils;

/**
 * Build P2P message:
 * https://en.bitcoin.it/wiki/Protocol_documentation#Message_structure
 * 
 * @author liaoxuefeng
 */
public class VersionMessage extends Message {

	int protocolVersion;
	int lastBlock;
	InetAddress toAddr;

	public static VersionMessage parse(BitCoinInput input) throws IOException {
		byte[] payload = Message.parsePayload("version", input);
		return null;
	}

	public VersionMessage() {
		super("version");
	}

	public VersionMessage(int lastBlock, InetAddress toAddr) {
		super("version");
		this.protocolVersion = BitcoinConstants.PROTOCOL_VERSION;
		this.lastBlock = lastBlock;
		this.toAddr = toAddr;
	}

	protected byte[] getPayload() {
		return new BitCoinOutput().writeInt(this.protocolVersion) // protocol
				.writeLong(BitcoinConstants.NETWORK_SERVICES) // services
				.writeLong(Instant.now().getEpochSecond()) // timestamp
				.write(new NetworkAddress(this.toAddr).toByteArray(true)) // recipient-address
				.write(new NetworkAddress(NetworkUtils.getLocalInetAddress()).toByteArray(true)) // sender-address
				.writeLong(BitcoinConstants.NODE_ID) // nodeId
				.writeString("/Satoshi:0.7.2/") // sub-version-string
				.writeInt(this.lastBlock) // # of last block
				.toByteArray();
	}
}
