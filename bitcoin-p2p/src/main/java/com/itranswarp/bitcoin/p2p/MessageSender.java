package com.itranswarp.bitcoin.p2p;

import com.itranswarp.bitcoin.p2p.message.Message;

public interface MessageSender {

	void sendMessage(Message message);

	/**
	 * Set timeout must be called periodically to keep connection alive.
	 * 
	 * @param timeoutInMillis
	 */
	void setTimeout(long timeoutInMillis);

	void close();
}
