package ro.ase.ism.sap;

import java.util.Arrays;

public class TestBitwiseOps {

	public static void main(String[] args) {
		
		byte binaryValue = (byte) 0b10001111;
		binaryValue = (byte) 0x8F;
		binaryValue = (byte)(1 << 7 | 1 << 3 | 1 << 2| 1 << 1 | 1);
		
		System.out.println(Integer.toBinaryString(binaryValue));
		
		byte value = 8;
		byte result = (byte)(value >> 1); //equivalent of a division by 2^1
		System.out.println(result);
		result = (byte)(value >> 3); //equivalent of a division by 2^3
		System.out.println(result);
		
		byte byteValue = 0b00010011;
		result = (byte) (byteValue >> 1); //you get 00001001 -> 9
		System.out.println(result);
		byteValue = (byte) 0b11110010;
		System.out.println(byteValue);
		result = (byte) (byteValue >> 1); //you get 01111001
		System.out.println(result); //we actually get 11111001 - Java preserves the bit sign
		
		System.out.println(Integer.toBinaryString(result));
		
		result = (byte) ((0xFF & byteValue) >>> 1); //you get 01111001
		System.out.println(result);
		
		System.out.println(Integer.toBinaryString(result));
		
		byte[] key1 = {(byte)0xFA, (byte)0x13, (byte)0x14};
		byte[] key2 = {(byte)0xFA, (byte)0x13, (byte)0x14};
		
		//DON't
		if(key1 == key2) {
			System.out.println("Same keys");
		}
		else {
			System.out.println("Different keys");
		}
		
		if(Arrays.equals(key1, key2)) {
			System.out.println("Same keys");
		}
		else {
			System.out.println("Different keys");
		}
		
		//checking the value of a particular bit in a byte
		byteValue = (byte) 0b10011101;
		
		//we count bits from right
		//we count from 1 (1st)
		//check if the 5th bit is 1 or 0
		
		//create a mask with all bits 0 except the one that you want to check
		byte mask = 1 << 4;
		if((mask & byteValue) == 0) {
			System.out.println("The 5th bit is 0");
		}
		else {
			System.out.println("The 5th bit is 1");
		}
		
		
		
	}

}
