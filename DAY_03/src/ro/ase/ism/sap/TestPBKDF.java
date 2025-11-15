package ro.ase.ism.sap;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.text.Utilities;


public class TestPBKDF {
	
	public static byte[] getPBKDFValue(
			String password, 
			String salt, 
			int noIterations, 
			int outputNoOfBits,
			String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		PBEKeySpec pbkdfSpec = new PBEKeySpec(
				password.toCharArray(),
				salt.getBytes(), 
				noIterations, 
				outputNoOfBits);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
		byte[] result  = keyFactory.generateSecret(pbkdfSpec).getEncoded();
		return result;
		
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
		
		// performance testing of PBKDF functions
		
		String password = "1234";
		String salt = "ism25";
		
		double tStart = System.currentTimeMillis();
		byte[] storedHash = getPBKDFValue(password, 
				salt, 10000, 128, "PBKDF2WithHmacSHA1");
		double tStop = System.currentTimeMillis();
		
		System.out.println(String.format("Value is %s in %f ms", 
				Utility.toHexString(storedHash), tStop-tStart));
		
		tStart = System.currentTimeMillis();
		storedHash = getPBKDFValue(password, 
				salt, 1000000, 128, "PBKDF2WithHmacSHA1"); //PBKDF2WithHmacSHA256
		tStop = System.currentTimeMillis();
		
		System.out.println(String.format("Value is %s in %f ms", 
				Utility.toHexString(storedHash), tStop-tStart));
		
		for (Provider provider: Security.getProviders()) {
			  System.out.println(provider.getName());
			  for (String key: provider.stringPropertyNames())
			    System.out.println("\t" + key);
			}
		
	}

}






