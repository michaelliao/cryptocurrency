package com.itranswarp.bitcoin.message;

/**
 * Build P2P message:
 * https://en.bitcoin.it/wiki/Protocol_documentation#Message_structure
 * 
 * @author liaoxuefeng
 */
public class VersionMessage extends Message {

	public VersionMessage() {
		super("version");
	}

	protected byte[] getPayload() {
		// TODO Auto-generated method stub
		return null;
	}
}
