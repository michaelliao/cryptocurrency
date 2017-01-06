package com.itranswarp.bitcoin.p2p;

import com.itranswarp.bitcoin.p2p.message.Message;

public interface MessageSender {

	void sendMessage(Message message);

}
