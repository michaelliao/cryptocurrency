package com.itranswarp.bitcoin.io;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Discover full nodes by DNS query:
 * https://bitcoin.org/en/developer-guide#peer-discovery
 * 
 * @author liaoxuefeng
 */
public class BitcoinPeerDiscover implements PeerDiscover {

	Log log = LogFactory.getLog(BitcoinPeerDiscover.class);

	// https://en.bitcoin.it/wiki/Satoshi_Client_Node_Discovery#DNS_Addresses
	static final String[] DNS_SEEDS = { "bitseed.xf2.org", "dnsseed.bluematt.me", "seed.bitcoin.sipa.be",
			"dnsseed.bitcoin.dashjr.org", "seed.bitcoinstats.com" };

	static final InetAddress[] EMPTY_ADDRS = new InetAddress[0];

	private BitcoinPeerDiscover() {
	}

	@Override
	public String[] lookup() throws IOException {
		log.info("Lookup peers from DNS seed...");
		String[] ips = Arrays.stream(DNS_SEEDS).parallel().map((host) -> {
			try {
				return InetAddress.getAllByName(host);
			} catch (UnknownHostException e) {
				log.warn("Cannot look up host: " + host);
				return EMPTY_ADDRS;
			}
		}).flatMap(x -> Arrays.stream(x)).filter(addr -> addr instanceof Inet4Address).map(addr -> {
			return addr.getHostAddress();
		}).toArray(String[]::new);
		if (ips.length == 0) {
			throw new IOException("Cannot lookup pears from all DNS seeds.");
		}
		return ips;
	}

	private static final BitcoinPeerDiscover INSTANCE = new BitcoinPeerDiscover();

	public static BitcoinPeerDiscover getInstance() {
		return INSTANCE;
	}

}
