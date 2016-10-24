package com.itranswarp.bitcoin.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Instant;

import com.itranswarp.bitcoin.BitcoinConstants;
import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.bitcoin.util.NetworkUtils;

/**
 * Build P2P message:
 * https://en.bitcoin.it/wiki/Protocol_documentation#Message_structure
 * 
 * @author liaoxuefeng
 */
public class VersionMessage extends Message {

	int protocolVersion;
	long services;
	long timestamp;

	NetworkAddress recipientAddress;
	NetworkAddress senderAddress;

	long nonce;
	String subVersion;

	int lastBlock;
	boolean relay;

	public VersionMessage(byte[] payload) throws IOException {
		super("version");
		try (BitcoinInput input = new BitcoinInput(new ByteArrayInputStream(payload))) {
			this.protocolVersion = input.readInt();
			this.services = input.readLong();
			this.timestamp = input.readLong();
			this.recipientAddress = NetworkAddress.parse(input, true);
			if (this.protocolVersion >= 106) {
				this.senderAddress = NetworkAddress.parse(input, true);
				this.nonce = input.readLong();
				this.subVersion = input.readString();
				this.lastBlock = input.readInt();
				if (this.protocolVersion >= 70001) {
					this.relay = input.readByte() != 0;
				}
			}
		}
	}

	public VersionMessage() {
		super("version");
	}

	public VersionMessage(int lastBlock, InetAddress recipientAddr) {
		super("version");
		this.protocolVersion = BitcoinConstants.PROTOCOL_VERSION;
		this.services = BitcoinConstants.NETWORK_SERVICES;
		this.timestamp = Instant.now().getEpochSecond();
		this.recipientAddress = new NetworkAddress(recipientAddr);
		this.senderAddress = new NetworkAddress(NetworkUtils.getLocalInetAddress());
		this.nonce = BitcoinConstants.NODE_ID;
		this.subVersion = BitcoinConstants.SUB_VERSION;
		this.lastBlock = lastBlock;
		this.relay = true;
	}

	@Override
	protected byte[] getPayload() {
		BitcoinOutput output = new BitcoinOutput();
		output.writeInt(this.protocolVersion) // protocol
				.writeLong(this.services) // services
				.writeLong(timestamp) // timestamp
				.write(this.recipientAddress.toByteArray(true)); // recipient-address
		if (this.protocolVersion >= 106) {
			output.write(this.senderAddress.toByteArray(true)) // sender-address
					.writeLong(this.nonce) // nodeId
					.writeString(this.subVersion) // sub-version-string
					.writeInt(this.lastBlock); // # of last block
			if (this.protocolVersion >= 70001) {
				output.writeByte(1);
			}
		}
		return output.toByteArray();
	}

	@Override
	public String toString() {
		return "VersionMessage(lastBlock=" + this.lastBlock + ", protocol=" + this.protocolVersion + ", timestamp="
				+ this.timestamp + ")";
	}
}
