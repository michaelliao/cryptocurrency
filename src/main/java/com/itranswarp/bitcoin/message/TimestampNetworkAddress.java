package com.itranswarp.bitcoin.message;

import java.io.IOException;

import com.itranswarp.bitcoin.io.BitcoinInput;

public class TimestampNetworkAddress {

	int timestamp;
	NetworkAddress address;

	public TimestampNetworkAddress(BitcoinInput input) throws IOException {
		this.timestamp = input.readInt();
		this.address = new NetworkAddress(input, true);
	}
}
