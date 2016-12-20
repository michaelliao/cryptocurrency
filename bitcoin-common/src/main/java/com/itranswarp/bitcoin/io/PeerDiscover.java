package com.itranswarp.bitcoin.io;

import java.io.IOException;

public interface PeerDiscover {

	/**
	 * Lookup and return at lease one ip address.
	 * 
	 * @return Ip addresses.
	 * @throws IOException
	 */
	String[] lookup() throws IOException;

}
