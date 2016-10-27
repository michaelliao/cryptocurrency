package com.itranswarp.bitcoin.struct;

import java.io.IOException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.cryptocurrency.common.TimestampSerializer;

public class TimestampNetworkAddress {

	@JsonSerialize(using = TimestampSerializer.class)
	public long timestamp; // uint32

	public NetworkAddress address;

	public TimestampNetworkAddress(BitcoinInput input) throws IOException {
		this.timestamp = input.readUnsignedInt();
		this.address = new NetworkAddress(input, true);
	}
}
