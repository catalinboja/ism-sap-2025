package ro.ism.ase.sap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;


public class TestHash {
	
	public static String getHexString(byte[] input) {
		StringBuilder sb = new StringBuilder();
		for(byte value : input) {
			sb.append(String.format("%02X",  value));
		}
		return sb.toString();
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidKeySpecException {

		//test providers
		Provider bcProvider = new BouncyCastleProvider();
		final String BouncyCastleName = "BC";

		//check if Bouncy Castle or other provider is available
		if(Security.getProvider(BouncyCastleName) == null) {
			System.out.println("NO BouncyCastle available");
		}
		
		Security.addProvider(bcProvider);
		
		if(Security.getProvider(BouncyCastleName) == null) {
			System.out.println("NO BouncyCastle available");
		}else {
			System.out.println("We can use BouncyCastle");
		}
		
		
		if(Security.getProvider("SUN") == null) {
			System.out.println("NO SUN provider available");
		} else {
			System.out.println("We can use SUN provider");
		}
		
		//using hash/message digest algorithms
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		String myPass = "ism1234";
		
		byte[] passHash = md.digest(myPass.getBytes());
		
		System.out.println("Hash is " + getHexString(passHash));
		
		//computing the hash a file
		
		md = MessageDigest.getInstance("MD5","BC");
		
		File file = new File("result.txt");
		FileInputStream fis = new FileInputStream(file);
		
		byte[] inputBuffer = new byte[8];
		int noOfBytesWeGot = 0;
		noOfBytesWeGot = fis.read(inputBuffer);
		while(noOfBytesWeGot != -1) {
			md.update(inputBuffer, 0, noOfBytesWeGot);
			noOfBytesWeGot = fis.read(inputBuffer);
		}
		byte[] fileHash = md.digest();
		fis.close();
		
		System.out.println("File hash: " + getHexString(fileHash));
		
		// Base64 Encoding
		String filesHashBase64 = 
				Base64.getEncoder().encodeToString(fileHash);
		System.out.println("File hash in Base64: " + filesHashBase64);
		
		// Base64 Decoding
		byte[] initialFileHash = Base64.getDecoder().decode(filesHashBase64);
		if(Arrays.equals(fileHash, initialFileHash))
			System.out.println("Hash values are identical");
		
		if(getHexString(fileHash).equals(getHexString(initialFileHash)))
			System.out.println("Hash values are identical");
		
		//hashing the user password using pbkdf
		int outputSizeInBits = 128;
		PBEKeySpec pbkdfParams = 
				new PBEKeySpec(
						myPass.toCharArray(), 
						"ismsalt".getBytes(),
						1000000,
						outputSizeInBits
						);
		SecretKeyFactory keyFactory = 
				SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		byte[] saltedPassHash = 
				keyFactory.generateSecret(pbkdfParams).getEncoded();
		
		System.out.println("User pass with salt and iterations: " + 
				getHexString(saltedPassHash));
		
	}
}





