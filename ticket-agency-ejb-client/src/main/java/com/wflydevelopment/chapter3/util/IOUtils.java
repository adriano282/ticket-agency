package com.wflydevelopment.chapter3.util;

import java.util.Scanner;

public class IOUtils {
	private static Scanner scanner = new Scanner(System.in);
	
	public static String readLine(String string) {
		System.out.print(string);
		if (scanner.hasNext()) 
			return scanner.next();
		
		return "";
	}

	public static int readInt(String string) throws NumberFormatException {
		System.out.print(string);
		if (scanner.hasNext())
			return Integer.parseInt(scanner.next());
				
		return 0;		
	}
}
