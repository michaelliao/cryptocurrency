package com.itranswarp.bitcoin.keypair;

import java.util.Scanner;

public class PrettyAddressGenerator {

	public static void main(String[] args) {
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.print("Enter the prefix (e.g. Bob): ");
			String prefix = "1" + scanner.nextLine();
			System.out.println("Find address starts with prefix: " + prefix);
			long n = 0;
			long found = 0;
			for (;;) {
				if (n % 10000 == 0) {
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
					if (found == 10) {
						break;
					}
				}
			}
			System.out.println("Found " + found + " addresses.");
		}
	}
}
