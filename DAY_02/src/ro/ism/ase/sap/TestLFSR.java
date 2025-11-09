package ro.ism.ase.sap;

import java.util.Arrays;

public class TestLFSR {
	
	//implement a 32 bit register LFSR with the next tap sequence
	// x^31 + x^7 + x^5 + x^3 + x^2 + x^1 + 1

	
	//2 possible solutions
	// A. register is a 32 bit integer
	// B. register is an array of 4 bytes
	// C. register is a BitSet

	//Option B
	
	public static void initRegister(byte[] register, byte[] values) {
		for(int i = 0 ;i < register.length; i++)
			register[i] = values[i];
	}
	
	public static String printRegister(byte[] register) {
		StringBuilder sb = new StringBuilder();
		for(byte b : register) {
			//sb.append(Integer.toBinaryString(0xFF & b) + " ");
			sb.append(
					String.format("%8s",Integer.toBinaryString(0xFF & b))
					.replace(" ", "0") + " "
					);
		}
		return sb.toString();
	}
	
	// index is from 1 -> 8
	public static byte getBitAtIndex(byte value, int index) {
		//we count bits from right to left starting at 1
		byte mask = (byte) (1 << (index - 1));
		if((mask & value) == 0) {
			return 0;
		} else {
			return 1;
		}
	}
	
	public static byte applyTapSequence(byte[] register) {
		// x^31 + x^7 + x^5 + x^3 + x^2 + x^1 + x^0
		//bits locations are hardcoded
		byte result = (byte) (getBitAtIndex(register[0], 8) ^ 
				getBitAtIndex(register[3], 8) ^ 
				getBitAtIndex(register[3], 6) ^ 
				getBitAtIndex(register[3], 4) ^ 
				getBitAtIndex(register[3], 3) ^ 
				getBitAtIndex(register[3], 2) ^ 
				getBitAtIndex(register[3], 1));
		return result;
	}
	
	// returns the shifted value
	public static byte shiftToRight(byte value, byte input) {
		value = (byte)((0xFF & value) >>> 1);
		byte inputMask = (byte) (input << 7);
		value = (byte) (value | inputMask);
		return value;
	}
	
	public static byte shiftRegister(byte[] register, byte input) {
		byte outputBit = 0;
		for(int i = 0; i < register.length; i++) {
			outputBit = getBitAtIndex(register[i], 1);
			register[i] = shiftToRight(register[i], input);
			input = outputBit;
		}
		return outputBit;
	}
	
	public static byte[] getPseudoRandomBits(byte[] register, int noIterations) {
		byte[] result = new byte[noIterations];
		for(int i = 0; i < noIterations; i++) {
			byte tapBit = applyTapSequence(register);
			result[i] = shiftRegister(register, tapBit);
		}
		return result;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//the register
		byte[] register = new byte[4];
		
		//initial value
		byte[] initialValue = {
				(byte)0b10101010,
				(byte)0b11110000,
				(byte)0b00001111,
				(byte)0b11001100};
		
		initRegister(register, initialValue);
		System.out.println(printRegister(register));
		
//		System.out.println(getBitAtIndex(register[0], 1));
//		System.out.println(getBitAtIndex(register[0], 2));
//		System.out.println(applyTapSequence(register));
//		
//		register[0] = shiftToRight(register[0], (byte)1);
//		System.out.println(printRegister(register));
//		register[0] = shiftToRight(register[0], (byte)0);
//		System.out.println(printRegister(register));
		
		byte[] result = getPseudoRandomBits(register, 64);
		System.out.println(Arrays.toString(result));
		
	}

}
