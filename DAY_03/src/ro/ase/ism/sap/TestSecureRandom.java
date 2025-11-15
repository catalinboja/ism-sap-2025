package ro.ase.ism.sap;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class TestSecureRandom {

	public static void main(String[] args) throws NoSuchAlgorithmException {

		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		byte[] secretSeed = {(byte)0b10101010, (byte)0b11110000};
		secureRandom.setSeed(secretSeed);
		
		byte[] randomSessionKey = new byte[16]; // AES key on 128 bits
		secureRandom.nextBytes(randomSessionKey);
		
		System.out.println("Random key: " + Utility.toBase64(randomSessionKey));
		System.out.println("Random key: " + Utility.toHexString(randomSessionKey));
		
	}

}
