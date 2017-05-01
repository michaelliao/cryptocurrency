package com.itranswarp.bitcoin.struct;

import java.io.IOException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.serializer.TimestampSerializer;

/**
 * Timestamp and network address.
 * 
 * @author Michael Liao
 */
public class TimestampNetworkAddress {

	/**
	 * uint32
	 */
	@JsonSerialize(using = TimestampSerializer.class)
	public long timestamp;

	public NetworkAddress address;

	public TimestampNetworkAddress(BitcoinInput input) throws IOException {
		this.timestamp = input.readUnsignedInt();
		this.address = new NetworkAddress(input, true);
	}
}
