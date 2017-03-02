package com.itranswarp.bitcoin.wallet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itranswarp.bitcoin.keypair.ECDSAKeyPair;
import com.itranswarp.bitcoin.wallet.util.EncryptUtil;

public class SecretKeyManager {

	static ObjectMapper mapper = new ObjectMapper();

	static final String DEFAULT_WALLET_FILE = "wallet.json";

	final Log log = LogFactory.getLog(getClass());

	final String file;
	final char[] password;
	final List<SecretKey> keys;

	public SecretKeyManager(char[] password) {
		this(password, DEFAULT_WALLET_FILE);
	}

	public SecretKeyManager(char[] password, String file) {
		this.password = password;
		this.file = file;
		this.keys = load();
	}

	public synchronized List<SecretKey> getKeys() {
		List<SecretKey> copiedKeys = new ArrayList<>(this.keys.size());
		for (SecretKey key : this.keys) {
			copiedKeys.add(key.copy());
		}
		return copiedKeys;
	}

	public synchronized void importKey(String name, String wif) {
		this.keys.add(new SecretKey(name, ECDSAKeyPair.parseWIF(wif)));
		store();
	}

	public synchronized void generateNewKey(String name) {
		this.keys.add(new SecretKey(name, ECDSAKeyPair.generatePrivateKey()));
		store();
	}

	synchronized List<SecretKey> load() {
		try (BufferedReader reader = new BufferedReader(new FileReader(this.file))) {
			EncryptedKeys eks = mapper.readValue(reader, EncryptedKeys.class);
			return eks.decrypt(this.password);
		} catch (FileNotFoundException e) {
			// ignore file not found:
			return new ArrayList<>();
		} catch (IOException | GeneralSecurityException e) {
			log.warn("Error when read wallet.", e);
			throw new RuntimeException("Invalid wallet.");
		}
	}

	synchronized void store() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.file))) {
			EncryptedKeys eks = toEncryptedKeys();
			String json = mapper.writeValueAsString(eks);
			writer.write(json);
		} catch (IOException | GeneralSecurityException e) {
			log.warn("Error when write wallet.", e);
			throw new RuntimeException("Save wallet failed.");
		}
	}

	EncryptedKeys toEncryptedKeys() throws GeneralSecurityException {
		final byte[] salt = EncryptUtil.secureSalt();
		List<EncryptedKey> list = new ArrayList<>(this.keys.size());
		for (SecretKey sk : this.keys) {
			list.add(toEncryptedKey(sk, salt));
		}
		EncryptedKeys eks = new EncryptedKeys();
		eks.salt = Base64.getEncoder().withoutPadding().encodeToString(salt);
		eks.keys = list;
		return eks;
	}

	EncryptedKey toEncryptedKey(SecretKey sk, byte[] salt) throws GeneralSecurityException {
		EncryptedKey ek = new EncryptedKey();
		ek.label = Base64.getEncoder().withoutPadding()
				.encodeToString(EncryptUtil.encrypt(this.password, salt, sk.label.getBytes(StandardCharsets.UTF_8)));
		ek.key = Base64.getEncoder().withoutPadding().encodeToString(EncryptUtil.encrypt(this.password, salt, sk.key));
		return ek;
	}

	public static class EncryptedKeys {

		public String salt;
		public List<EncryptedKey> keys;

		List<SecretKey> decrypt(char[] password) throws IOException, GeneralSecurityException {
			final byte[] bsalt = Base64.getDecoder().decode(this.salt);
			List<SecretKey> list = new ArrayList<>(this.keys.size());
			for (EncryptedKey ek : this.keys) {
				list.add(ek.decrypt(password, bsalt));
			}
			return list;
		}
	}

	public static class EncryptedKey {

		public String label;
		public String key;

		SecretKey decrypt(char[] password, byte[] salt) throws IOException, GeneralSecurityException {
			String label = new String(EncryptUtil.decrypt(password, salt, Base64.getDecoder().decode(this.label)),
					"UTF-8");
			byte[] k = EncryptUtil.decrypt(password, salt, Base64.getDecoder().decode(this.key));
			return new SecretKey(label, k);
		}
	}
}
