package ro.ase.ism.sap;

import java.util.Base64;

public class Utility {
	public static String toHexString(byte[] input) {
		StringBuilder sb = new StringBuilder();
		for(byte value : input) {
			sb.append(String.format("%02X", value));
		}
		return sb.toString();
	}
	
	public static String toBase64(byte[] input) {
		return Base64.getEncoder().encodeToString(input);
	}
	
	public static byte[] fromBase64(String input) {
		return Base64.getDecoder().decode(input);
	}
}










