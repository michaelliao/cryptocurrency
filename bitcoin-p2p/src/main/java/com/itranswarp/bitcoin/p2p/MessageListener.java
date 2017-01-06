package com.itranswarp.bitcoin.p2p;

import com.itranswarp.bitcoin.p2p.message.Message;

public interface MessageListener {

	void onMessage(MessageSender sender, Message message);

}
