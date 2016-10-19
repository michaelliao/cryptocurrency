package com.itranswarp.bitcoin;

import java.io.IOException;

import com.itranswarp.bitcoin.io.BitCoinBlockDataInput;

public class NetworkAddress {
	long time; // uint32, the Time (version >= 31402). Not present in version
				// message.
	long services; // uint64, same service(s) listed in version
	byte[] ipv6; // 16 bytes IPv6 address. Network byte order.
	// the IPv4 address is 12 bytes 00 00 00 00 00 00 00 00 00 00 FF FF,
	// followed by the 4 bytes of the IPv4 address
	int port; // uint16, port number

	public NetworkAddress(BitCoinBlockDataInput input) throws IOException {
		this.time = input.readUnsignedInt();
		this.services = input.readLong();
		this.ipv6 = input.readBytes(16);
		this.port = input.readUnsignedShort();
	}

}
