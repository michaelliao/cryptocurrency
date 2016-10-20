package com.itranswarp.bitcoin.message;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Instant;

import com.itranswarp.bitcoin.BitcoinConstants;
import com.itranswarp.bitcoin.io.BitCoinInput;
import com.itranswarp.bitcoin.io.BitCoinOutput;
import com.itranswarp.bitcoin.util.NetworkUtils;

public class NetworkAddress {

	long time; // uint32, the Time (version >= 31402). Not present in version
				// message.
	long services; // uint64, same service(s) listed in version
	byte[] ipv6; // 16 bytes IPv6 address. Network byte order.
	// the IPv4 address is 12 bytes 00 00 00 00 00 00 00 00 00 00 FF FF,
	// followed by the 4 bytes of the IPv4 address
	int port; // uint16, port number

	public NetworkAddress(BitCoinInput input) throws IOException {
		this.time = input.readUnsignedInt();
		this.services = input.readLong();
		this.ipv6 = input.readBytes(16);
		this.port = input.readUnsignedShort();
	}

	public NetworkAddress() {
		this.time = Instant.now().getEpochSecond();
		this.services = 1;
		this.ipv6 = NetworkUtils.getIPv6(NetworkUtils.getLocalInetAddress());
		this.port = BitcoinConstants.PORT;
	}

	public NetworkAddress(InetAddress addr) {
		this.time = Instant.now().getEpochSecond();
		this.services = 1;
		this.ipv6 = NetworkUtils.getIPv6(addr);
		this.port = BitcoinConstants.PORT;
	}

	public byte[] toByteArray(boolean excludeTime) {
		BitCoinOutput output = new BitCoinOutput();
		if (!excludeTime) {
			output.writeUnsignedInt(time); // time
		}
		output.writeLong(this.services) // service
				.write(this.ipv6) // ipv6
				.writeUnsignedShort(this.port); // port
		return output.toByteArray();
	}

}
