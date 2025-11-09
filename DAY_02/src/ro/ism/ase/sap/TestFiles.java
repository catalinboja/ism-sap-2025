package ro.ism.ase.sap;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class TestFiles {

	public static void main(String[] args) throws IOException {
		
		File file = new File("message.txt");
		if(!file.exists()) {
			System.out.println("************ NO FILE ***************");
		}
		
		File resultFile = new File("result.txt");
		if(!resultFile.exists()) {
			resultFile.createNewFile();
		}
		
		//going through multiple files
		File folder = new File("D:\\2025-2026\\ism-sap-2025");
		if(folder.exists()) {
			String[] entries = folder.list();
			for(String entry : entries) {
				File fileEntry = new File(folder,entry);
				
//				File fileEntry = new File(
//						folder.getAbsolutePath() + File.separator + entry);
//				
				System.out.println(fileEntry.getAbsolutePath());
				if(fileEntry.isDirectory()) {
					System.out.println("--- It is a folder");
					//TODO: dive into the folder and print content
				}
				if(fileEntry.isFile()) {
					System.out.println("--- It is a file");
				}
			}
		}
		
		//writing into binary files
		File binaryFile = new File("message.bin");
		if(!binaryFile.exists())
			binaryFile.createNewFile();
		
		FileOutputStream fos = new FileOutputStream(binaryFile);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		DataOutputStream dos = new DataOutputStream(bos);
		
		String message = "A secret message";
		int keySize = 128;
		float salt = (float) 23.5;
		boolean isEncrypted = true;
		byte iv = (byte)0b00000001;
		
		dos.writeInt(keySize);
		dos.writeByte(iv);
		dos.writeUTF(message);
		dos.writeFloat(salt);
		dos.writeBoolean(isEncrypted);
		
		dos.close();
		
		//read from binary files
		FileInputStream fis = new FileInputStream(binaryFile);
		DataInputStream dis = new DataInputStream(fis);
		
		keySize = dis.readInt();
		iv = dis.readByte();
		message = dis.readUTF();
		salt = dis.readFloat();
		isEncrypted = dis.readBoolean();
		
		dis.close();
		
		System.out.println(message);
		
		byte[] values = {10,13,20,30,40};
		File binaryValues = new File("values.bin");
		if(!binaryValues.exists())
			binaryValues.createNewFile();
		
		fos = new FileOutputStream(binaryValues);
		fos.write(values);
		fos.close();
		
		//use RandomAccessFile to read the 3rd value
		RandomAccessFile raf = 
				new RandomAccessFile(binaryValues,"r");
		raf.seek(2);
		byte value3 = raf.readByte();
		
		System.out.println("3rd value is " + value3);
		
		raf.close();
		
		//determine the file size
		raf = new RandomAccessFile(binaryValues,"r");
		System.out.println("File length: " + raf.length());

		raf.close();
		
		//text files
		FileWriter fw = new FileWriter(resultFile,true);
		PrintWriter pw = new PrintWriter(fw);
		pw.println("This is a secret message");
		pw.println("Don't tell anyone");
		pw.close();
		
		//read from text files
		FileReader fr = new FileReader(resultFile);
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		while(true) {
			line = br.readLine();
			if(line == null)
				break;
			System.out.println(line);
		}
		
		br.close();
		
		fr = new FileReader(resultFile);
		br = new BufferedReader(fr);
		
		List<String> lines = br.lines()
				.collect(Collectors.toList());
		for(String fileLine : lines) {
			System.out.println("*" + fileLine);
		}
		
		br.close();
		
	}

}







