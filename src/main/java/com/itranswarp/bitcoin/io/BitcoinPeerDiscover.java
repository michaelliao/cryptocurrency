package com.itranswarp.bitcoin.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itranswarp.bitcoin.BitcoinConstants;
import com.itranswarp.bitcoin.message.GetBlocksMessage;
import com.itranswarp.bitcoin.message.InvMessage;
import com.itranswarp.bitcoin.message.Message;
import com.itranswarp.bitcoin.message.PingMessage;
import com.itranswarp.bitcoin.message.PongMessage;
import com.itranswarp.bitcoin.message.VerAckMessage;
import com.itranswarp.bitcoin.message.VersionMessage;
import com.itranswarp.cryptocurrency.common.Discover;

/**
 * Discover full nodes by DNS query:
 * https://bitcoin.org/en/developer-guide#peer-discovery
 * 
 * @author liaoxuefeng
 */
public class BitcoinPeerDiscover implements Discover {

	static Log log = LogFactory.getLog(BitcoinPeerDiscover.class);

	// https://en.bitcoin.it/wiki/Satoshi_Client_Node_Discovery#DNS_Addresses
	static final String[] DNS_SEEDS = { "bitseed.xf2.org", "dnsseed.bluematt.me", "seed.bitcoin.sipa.be",
			"dnsseed.bitcoin.dashjr.org", "seed.bitcoinstats.com" };

	static final InetAddress[] EMPTY_ADDRS = new InetAddress[0];

	@Override
	public String[] lookup() throws IOException {
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

	public static void main(String[] args) throws Exception {
		String[] nodes = new BitcoinPeerDiscover().lookup();
		for (String node : nodes) {
			log.info("Found node: " + node);
		}
		for (String node : nodes) {
			log.info("Try connect to node: " + node);
			try (Socket sock = new Socket()) {
				sock.connect(new InetSocketAddress(node, BitcoinConstants.PORT), 3000);
				try (InputStream input = sock.getInputStream()) {
					try (OutputStream output = sock.getOutputStream()) {
						VersionMessage vmsg = new VersionMessage(0, sock.getInetAddress());
						log.info(":=> " + vmsg);
						output.write(vmsg.toByteArray());
						// receive msg:
						byte[] firstBlock = BitcoinConstants.GENESIS_HASH;
						while (true) {
							BitcoinInput in = new BitcoinInput(input);
							Message msg = Message.Builder.parseMessage(in);
							log.info("<=: " + msg);
							Message resp = handleMessage(msg);
							if (resp != null) {
								log.info(":=> " + resp);
								output.write(resp.toByteArray());
							} else {
								if (firstBlock != null) {
									Message blks = new GetBlocksMessage(firstBlock, BitcoinConstants.ZERO_HASH);
									log.info(":=> " + blks);
									output.write(blks.toByteArray());
									firstBlock = null;
								}
							}
						}
					}
				}
			} catch (SocketTimeoutException | SocketException e) {
				// ignore
			}
		}
	}

	private static Message handleMessage(Message msg) {
		if (msg instanceof PingMessage) {
			return new PongMessage(((PingMessage) msg).getNonce());
		}
		if (msg instanceof VersionMessage) {
			return new VerAckMessage();
		}
		if (msg instanceof InvMessage) {
			InvMessage inv = (InvMessage) msg;
		}
		return null;
	}

}
