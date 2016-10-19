package com.itranswarp.cryptocurrency.common;

import java.io.IOException;

public interface Discover {

	/**
	 * Lookup and return at lease one ip address.
	 * 
	 * @return Ip addresses.
	 * @throws IOException
	 */
	String[] lookup() throws IOException;

}
