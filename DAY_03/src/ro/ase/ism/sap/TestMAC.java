package ro.ase.ism.sap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TestMAC {
	
	public static byte[] getMAC(String fileName, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
		 File file = new File(fileName);
		 if(!file.exists())
			 throw new RuntimeException("**** NO FILE *****");
		 
		 //init the MAC
		 Mac mac = Mac.getInstance("HmacSHA1");
		 SecretKeySpec macKey = new SecretKeySpec(
				 secretKey.getBytes(), "HmacSHA1");
		 mac.init(macKey);
		 
		 
		 FileInputStream fis = new FileInputStream(file);
		 
		 // process the file in blocks
		 byte[] buffer = new byte[4];
		 while(true) {
			 int noBytesFromFile = fis.read(buffer);
			 if(noBytesFromFile == -1)
				 break;
			 mac.update(buffer, 0, noBytesFromFile);
		 }
		 
		 fis.close();
		 
		 return mac.doFinal();
	}

	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
		
		String password = "ismsecret";
		byte[] hmac = getMAC("message.txt", password);
		
		System.out.println("HMAC: " + Utility.toHexString(hmac));
		
	}

}
