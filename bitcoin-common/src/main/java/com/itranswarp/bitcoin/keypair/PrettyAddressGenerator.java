package com.itranswarp.bitcoin.keypair;

import java.util.Scanner;

/**
 * Generate pretty address starts with specific word. e.g. "BTC".
 * 
 * For example:
 * 
 * public key: 1BTC48PCwMQMhKr79xgjwdFJFe84iBTrdF private key:
 * Kzgnp4nUukjYuDuJBp9h1hSW4JVigyCAzUXVNvbx2MsLAWUwug5n
 * 
 * @author Michael Liao
 */
public class PrettyAddressGenerator {

	static final long NUM_OF_ADDR = 10;

	public static void main(String[] args) {
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.print("Enter the prefix (e.g. BTC): ");
			String prefix = "1" + scanner.nextLine();
			System.out.println("Find address starts with prefix: " + prefix);
			final long step = 1 << Math.min(60, prefix.length() * 11);
			long n = 0;
			long found = 0;
			for (;;) {
				if (n % step == 0) {
					System.out.print('.');
				}
				n++;
				ECDSAKeyPair keyPair = ECDSAKeyPair.createNewKeyPair();
				String cwif = keyPair.toCompressedWIF();
				String caddr = keyPair.toEncodedCompressedPublicKey();
				if (caddr.startsWith(prefix)) {
					found++;
					n = 0;
					System.out.println("\n" + caddr + " " + cwif);
					if (found == NUM_OF_ADDR) {
						break;
					}
				}
				if (n == Long.MAX_VALUE) {
					System.out.println("Ooops NO ADDRESS FOUND!");
					break;
				}
			}
			System.out.println("Found " + found + " addresses.");
		}
	}
}
