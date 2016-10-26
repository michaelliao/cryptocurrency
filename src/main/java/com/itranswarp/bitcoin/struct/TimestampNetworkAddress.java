package com.itranswarp.bitcoin.struct;

import java.io.IOException;

import com.itranswarp.bitcoin.io.BitcoinInput;

public class TimestampNetworkAddress {

	public long timestamp; // uint32
	public NetworkAddress address;

	public TimestampNetworkAddress(BitcoinInput input) throws IOException {
		this.timestamp = input.readUnsignedInt();
		this.address = new NetworkAddress(input, true);
	}
}
