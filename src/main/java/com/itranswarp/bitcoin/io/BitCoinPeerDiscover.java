package com.itranswarp.bitcoin.io;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.cryptocurrency.common.Discover;

/**
 * Discover full nodes by DNS query:
 * https://bitcoin.org/en/developer-guide#peer-discovery
 * 
 * @author liaoxuefeng
 */
public class BitCoinPeerDiscover implements Discover {

	static Log log = LogFactory.getLog(BitCoinPeerDiscover.class);

	@Override
	public String[] lookup() throws IOException {
		InetAddress[] addrs = InetAddress.getAllByName("bitseed.xf2.org");
		return Arrays.asList(addrs).stream().map((addr) -> {
			return addr.getHostAddress();
		}).toArray(String[]::new);
	}

	public static void main(String[] args) throws Exception {
		String[] nodes = new BitCoinPeerDiscover().lookup();
		for (String node : nodes) {
			log.info("Found node: " + node);
		}
	}
}
