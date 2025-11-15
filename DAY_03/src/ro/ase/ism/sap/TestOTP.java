package ro.ase.ism.sap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class TestOTP {
	
	// implement the Vernam principle for One Time Pad
	public static void encrypt(
			String inputFileName, 
			String outputFileName, 
			String keyFileName) throws IOException, NoSuchAlgorithmException {
		
		File inputFile = new File(inputFileName);
		if(!inputFile.exists())
			throw new RuntimeException("**** NO FILE *****");
		File outputFile = new File(outputFileName);
		if(!outputFile.exists())
			outputFile.createNewFile();
		File keyFile = new File(keyFileName);
		if(!keyFile.exists())
			keyFile.createNewFile();
		
		FileInputStream fis = new FileInputStream(inputFile);
		FileOutputStream fosCipher = new FileOutputStream(outputFile);
		FileOutputStream fosKey = new FileOutputStream(keyFile);
		
		//read 1 byte, generate 1 key byte
		
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		byte[] buffer = new byte[1];
		byte[] key = new byte[1];
		byte[] cipher = new byte[1];
		
		while(true) {
			int noBytes = fis.read(buffer);
			if(noBytes == -1 || noBytes == 0)
				break;
			secureRandom.nextBytes(key);
			cipher[0] = (byte) (buffer[0] ^ key[0]);
			
			fosCipher.write(cipher);
			fosKey.write(key);
		}
		
		fis.close();
		fosCipher.close();
		fosKey.close();
	}
	
	public static void decrypt(
			String cipherFileName, 
			String keyFileName,
			String outputFileName) throws IOException {
		
		File cipherFile = new File(cipherFileName);
		if(!cipherFile.exists())
			throw new RuntimeException("**** NO CIPHER FILE *****");
		File keyFile = new File(keyFileName);
		if(!keyFile.exists())
			throw new RuntimeException("**** NO KEY FILE *****");
		File outputFile = new File(outputFileName);
		if(!outputFile.exists())
			outputFile.createNewFile();
		
		FileInputStream fisKey = new FileInputStream(keyFile);
		FileInputStream fisCipher = new FileInputStream(cipherFile);
		FileOutputStream fos = new FileOutputStream(outputFile);
		
		byte[] key = new byte[1];
		byte[] cipher = new byte[1];
		byte[] plaintext = new byte[1];
		
		while(true) {
			int noBytes = fisKey.read(key);
			if(noBytes == -1 || noBytes == 0)
				break;
			fisCipher.read(cipher);
			
			plaintext[0] = (byte) (key[0] ^ cipher[0]);
			
			fos.write(plaintext);
		}
		
		fisCipher.close();
		fisKey.close();
		fos.close();
		
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		encrypt("message.txt", "message.otp", "key.otp");
		decrypt("message.otp", "key.otp", "message6.txt");
		System.out.println("Done");
	}

}
