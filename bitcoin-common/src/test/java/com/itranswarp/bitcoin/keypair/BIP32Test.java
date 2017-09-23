package com.itranswarp.bitcoin.keypair;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.itranswarp.bitcoin.keypair.BIP32.BIP32Key;

@RunWith(Parameterized.class)
public class BIP32Test {

	@Parameters
	public static Collection<Object> bip32TestData() {
		Object[] data = new Object[][] {
				{ "000102030405060708090a0b0c0d0e0f",
						"xprv9s21ZrQH143K3QTDL4LXw2F7HEK3wJUD2nW2nRk4stbPy6cq3jPPqjiChkVvvNKmPGJxWUtg6LnF5kejMRNNU3TGtRBeJgk33yuGBxrMPHi" },
				{ "fffcf9f6f3f0edeae7e4e1dedbd8d5d2cfccc9c6c3c0bdbab7b4b1aeaba8a5a29f9c999693908d8a8784817e7b7875726f6c696663605d5a5754514e4b484542",
						"xprv9s21ZrQH143K31xYSDQpPDxsXRTUcvj2iNHm5NUtrGiGG5e2DtALGdso3pGz6ssrdK4PFmM8NSpSBHNqPqm55Qn3LqFtT2emdEXVYsCzC2U" },
				{ "4b381541583be4423346c643850da4b320e46a87ae3d2a4e6da11eba819cd4acba45d239319ac14f863b8d5ab5a0d0c64d2e8a1e7d1457df2e5a3c51c73235be",
						"xprv9s21ZrQH143K25QhxbucbDDuQ4naNntJRi4KUfWT7xo4EKsHt2QJDu7KXp1A3u7Bi1j8ph3EGsZ9Xvz9dGuVrtHHs7pXeTzjuxBrCmmhgC6" }, };
		return Arrays.asList(data);
	}

	String seed;
	String extPrivate;

	public BIP32Test(String seed, String extPrivate) {
		this.seed = seed;
		this.extPrivate = extPrivate;
	}

	@Test
	public void testGenerateMasterKey() {
		BigInteger seed = new BigInteger(this.seed, 16);
		BIP32Key key = BIP32.generateMasterKey(seed);
		String encoded = key.serialize();
		assertEquals(this.extPrivate, encoded);
		BIP32Key recover = BIP32Key.deserialize(encoded);
		assertEquals(this.extPrivate, recover.serialize());
	}

}
