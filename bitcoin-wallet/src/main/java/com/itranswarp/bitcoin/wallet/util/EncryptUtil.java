package com.itranswarp.bitcoin.wallet.util;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class EncryptUtil {

	static final String ALG = "PBEWithSHA256And256BitAES-CBC-BC";
	static final int COUNT = 999;

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static byte[] secureSalt() throws GeneralSecurityException {
		return SecureRandom.getInstanceStrong().generateSeed(32);
	}

	public static byte[] encrypt(char[] password, byte[] salt, byte[] data) throws GeneralSecurityException {
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, COUNT);
		PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
		SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBEWithSHA256And256BitAES-CBC-BC");
		SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);
		Cipher encryptionCipher = Cipher.getInstance("PBEWithSHA256And256BitAES-CBC-BC");
		encryptionCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
		return encryptionCipher.doFinal(data);
	}

	public static byte[] decrypt(char[] password, byte[] salt, byte[] encrypted) throws GeneralSecurityException {
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, COUNT);
		PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
		SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBEWithSHA256And256BitAES-CBC-BC");
		SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);
		Cipher encryptionCipher = Cipher.getInstance("PBEWithSHA256And256BitAES-CBC-BC");
		encryptionCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
		return encryptionCipher.doFinal(encrypted);
	}
}
