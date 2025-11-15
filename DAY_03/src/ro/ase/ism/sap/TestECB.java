package ro.ase.ism.sap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class TestECB {
	
	public static void encrypt(
			String inputFileName, 
			byte[] key, 
			String algorithm,
			String outputFileName) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		File inputFile = new File(inputFileName);
		if(!inputFile.exists())
			throw new RuntimeException("**** NO FILE *****");
		File outputFile = new File(outputFileName);
		if(!outputFile.exists())
			outputFile.createNewFile();
		
		FileInputStream fis = new FileInputStream(inputFile);
		FileOutputStream fos = new FileOutputStream(outputFile);
		
		//create the Cipher
		Cipher cipher = Cipher.getInstance(algorithm + "/ECB/PKCS5Padding");
		SecretKeySpec secretkey = new SecretKeySpec(key, algorithm);
		
		//init the cipher
		cipher.init(Cipher.ENCRYPT_MODE, secretkey);
		
		byte[] block = new byte[cipher.getBlockSize()];
		
		while(true) {
			int noBytes = fis.read(block);
			if(noBytes == -1)
				break;
			byte[] cipherBlock = cipher.update(block, 0, noBytes);
			fos.write(cipherBlock);
		}
		
		//IMPORTANT - get the last cipher block
		byte[] cipherBlock = cipher.doFinal();
		fos.write(cipherBlock);
		
		fis.close();
		fos.close();
	
	}
	
	public static void decrypt(String inputFileName, byte[] key,
			String algorithm, String outputFileName) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		File inputFile = new File(inputFileName);
		if(!inputFile.exists())
			throw new RuntimeException("********* NO Input file for decrypt *******");
		File outputFile = new File(outputFileName);
		if(!outputFile.exists())
			outputFile.createNewFile();
		
		FileInputStream fis = new FileInputStream(inputFile);
		FileOutputStream fos = new FileOutputStream(outputFile);
		
		Cipher cipher = Cipher.getInstance(algorithm+"/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, algorithm));
		
		byte[] block = new byte[cipher.getBlockSize()];
		
		while(true) {
			int noBytes = fis.read(block);
			if(noBytes == -1)
				break;
			byte[] cipherBlock = cipher.update(block,0,noBytes);
			fos.write(cipherBlock);
		}
		
		byte[] cipherBlock = cipher.doFinal();
		fos.write(cipherBlock);
		
		fis.close();
		fos.close();
		
	}

	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		
		String key = "password12345678********";
		encrypt("message.txt", key.getBytes(), "DESede", "message.enc");
		decrypt("message.enc", key.getBytes(), "DESede", "message2.txt");
		
		System.out.println("Done");
	}

}
