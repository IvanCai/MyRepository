package com.antilost.util;

public class Parser {
	public static int byteToInt2(byte[] b) {
		if(b==null)
			return 0;
		int mask = 0xff;
		int temp = 0;
		int n = 0;
		for (int i = 0; i < 4; i++) {
			n <<= 8;
			temp = b[i] & mask;
			n |= temp;
		}
		return n;
	}
}
