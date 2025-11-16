package ro.ase.ism.sap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Enumeration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

public class TestKeyStore {
	
	public static KeyStore getKeyStore(
			String ksFileName, String ksPass) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		File ksFile = new File(ksFileName);
		if(!ksFile.exists())
			throw new RuntimeException("NO KS file !!!");
		FileInputStream fis = new FileInputStream(ksFile);
		
		KeyStore ks = KeyStore.getInstance("pkcs12");
		ks.load(fis,ksPass.toCharArray());
		
		fis.close();
		
		return ks;		
	}
	
	
	public static void printKeyStoreContent(KeyStore ks) throws KeyStoreException {
		if(ks != null) {
			Enumeration<String> items = ks.aliases();
			while(items.hasMoreElements()) {
				String item = items.nextElement();
				System.out.println("Item: " + item);
				if(ks.isCertificateEntry(item)) {
					System.out.println("-- is a public key");
				}
				if(ks.isKeyEntry(item)) {
					System.out.println(" -- is a pair of keys");
				}
			}
		}
	}
	
	public static PublicKey getPublicKey(KeyStore ks, String alias) throws KeyStoreException {
		if(ks == null || !ks.containsAlias(alias))
			throw new RuntimeException("*** Missing KS or alias ***");
		
		PublicKey publicKey = 
				(PublicKey) ks.getCertificate(alias).getPublicKey();
		return publicKey;
	}
	
	// alias Pass is the same as KS Pass for pkcs12 
	public static PrivateKey getPrivateKey(
			KeyStore ks, 
			String alias, String aliasPass) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		
		if(ks == null || !ks.containsAlias(alias))
			throw new RuntimeException("*** NO KS or alias ***");
		
		//In production don't - use the private key inside the HSM
		PrivateKey pk = (PrivateKey) ks.getKey(alias, aliasPass.toCharArray());
		return pk;
		
	}
	
	public static PublicKey getPublicKeyFromX509Certificate(String certFileName) throws CertificateException, IOException {
		File certFile = new File(certFileName);
		if(!certFile.exists())
			throw new RuntimeException("*** NO X509 certificate file ***");
		FileInputStream fis = new FileInputStream(certFile);
		
		CertificateFactory certFactory = 
				CertificateFactory.getInstance("X.509");
		X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(fis);
		
		fis.close();
		
		return certificate.getPublicKey();
		
	}
	
	public static byte[] encryptRSA(Key key, byte[] content) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(content);
	}
	
	public static byte[] decryptRSA(Key key, byte[] content) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(content);
	}
	
	public static byte[] generateSecureRandomKey(
			String algorithm, int noBits) throws NoSuchAlgorithmException {
		
		KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
		keyGenerator.init(noBits);
		return keyGenerator.generateKey().getEncoded();
		
	}
	
	public static byte[] generateDigitalSignature(
			String fileName, PrivateKey privKey) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {
		File file = new File(fileName);
		if(!file.exists())
			throw new RuntimeException("*** NO File ***");
		FileInputStream fis = new FileInputStream(file);
		
		Signature signature = Signature.getInstance("SHA1withRSA");
		signature.initSign(privKey);
		
		byte[] buffer = fis.readAllBytes();
		fis.close();
		
		signature.update(buffer);
		return signature.sign();	
	}
	
	public static boolean validateSignature(
			String fileName, 
			byte[] digitalSignature,
			PublicKey pubKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException {
					
		File file = new File(fileName);
		if(!file.exists())
			throw new RuntimeException("*** NO File ***");
		FileInputStream fis = new FileInputStream(file);
		
		Signature signature = Signature.getInstance("SHA1withRSA");
		signature.initVerify(pubKey);
		
		byte[] buffer = fis.readAllBytes();
		fis.close();
		
		signature.update(buffer);
		
		return signature.verify(digitalSignature);
	}
	
	
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, SignatureException {
		KeyStore ks = getKeyStore("ismkeystore.ks", "passks");
		printKeyStoreContent(ks);
		
		PublicKey ismaseroPub = 
				getPublicKey(ks, "ismasero");
		System.out.println("Public key:");
		System.out.println(
				Base64.getEncoder().encodeToString(ismaseroPub.getEncoded()));
		
		PublicKey ismKey1Pub = 
				getPublicKey(ks, "ismkey1");
		System.out.println("ISM Key 1 Public key:");
		System.out.println(
				Base64.getEncoder().encodeToString(ismKey1Pub.getEncoded()));
		
		PrivateKey ismKey1Priv = 
				getPrivateKey(ks, "ismkey1", "passks");
		System.out.println("ISM Key 1 Private key:");
		System.out.println(
				Base64.getEncoder().encodeToString(ismKey1Priv.getEncoded()));
		
		
		PublicKey ismKey1PubCert = 
				getPublicKeyFromX509Certificate("ISMCertificateX509.cer");
		System.out.println("ISM Key 1 Public key:");
		System.out.println(
				Base64.getEncoder().encodeToString(ismKey1PubCert.getEncoded()));
		
		//test RSA encryption and decryption
		String password = "ismsecret";
		byte[] encryptedPass = encryptRSA(ismKey1PubCert, password.getBytes());
		
		System.out.println("Encrypted password with RSA: ");
		System.out.println(Base64.getEncoder().encodeToString(encryptedPass));
		
		byte[] decryptedPass = decryptRSA(ismKey1Priv, encryptedPass);
		System.out.println("Initial password: " + new String(decryptedPass));
		
		//part of HTTPS handshake
		byte[] randomAESSessionKey = generateSecureRandomKey("AES", 256);
		//encrypt with server/destination public key
		byte[] encryptedSessionKey = encryptRSA(ismaseroPub, randomAESSessionKey);
		
		//digital signature
		byte[] msgDigitalSignature = 
				generateDigitalSignature("Message.txt", ismKey1Priv);
		System.out.println("Digital Signature: ");
		System.out.println(Base64.getEncoder().encodeToString(msgDigitalSignature));
		
		if(validateSignature("Message.txt", 
				msgDigitalSignature, ismKey1PubCert)) {
			System.out.println("The file is valid !!!");
		}
		
		if(validateSignature("MessageCopy.txt", 
				msgDigitalSignature, ismKey1PubCert)) {
			System.out.println("The file is valid !!!");
		}
		else {
			System.out.println("The file is NOT valid !!!");
		}
		
	}
}







