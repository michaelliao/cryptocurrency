package com.itranswarp.bitcoin.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;

/**
 * Build P2P message:
 * https://en.bitcoin.it/wiki/Protocol_documentation#Message_structure
 * 
 * @author liaoxuefeng
 */
public class AddrMessage extends Message {

	long count; // var_int
	TimestampNetworkAddress[] addr_list; // (uint32_t + net_addr)[]

	public AddrMessage() {
		super("addr");
		this.count = 0;
		this.addr_list = new TimestampNetworkAddress[0];
	}

	public AddrMessage(byte[] payload) throws IOException {
		super("addr");
		try (BitcoinInput input = new BitcoinInput(new ByteArrayInputStream(payload))) {
			this.count = input.readVarInt();
			this.addr_list = new TimestampNetworkAddress[(int) this.count];
			for (int i = 0; i < this.count; i++) {
				addr_list[i] = new TimestampNetworkAddress(input);
			}
		}
	}

	@Override
	protected byte[] getPayload() {
		BitcoinOutput output = new BitcoinOutput();
		output.writeVarInt(this.count);
		for (int i = 0; i < this.count; i++) {
			TimestampNetworkAddress taddr = this.addr_list[i];
			output.writeInt(taddr.timestamp);
			output.write(taddr.address.toByteArray(false));
		}
		return output.toByteArray();
	}

	@Override
	public String toString() {
		return "AddrMessage(count=" + this.count + ")";
	}

}
