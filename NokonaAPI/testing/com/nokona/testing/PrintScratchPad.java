package com.nokona.testing;

import java.util.Arrays;

import com.nokona.utilities.BarCodeUtilities;

public class PrintScratchPad {

	public static void main(String[] args) {
//		System.out.println(replaceLeadingWithUnderScores(String.format("%06d", 1230)));
//		System.out.println((int)(Math.ceil(12 / 3.0)));
//		System.out.println((int)(Math.ceil(13 / 3.0)));
//		System.out.println((int)(Math.ceil(14 / 3.0)));
//		System.out.println((int)(Math.ceil(15 / 3.0)));
		String name = "Mark Waggoner From Texas";
		System.out.println(name.replace(" ", "_"));
		System.out.println(String.format("%02d", 4));
		System.out.println(BarCodeUtilities.convertBarCode2Of5("1234"));
		System.out.println(BarCodeUtilities.convertBarCode2Of5("12345"));
		System.out.println(BarCodeUtilities.convertBarCode2Of5("123456"));
		System.out.println(BarCodeUtilities.convertBarCode2Of5("1234567"));
		System.out.println(BarCodeUtilities.convertBarCode2Of5("1234567"));
		System.out.println(BarCodeUtilities.convertBarCode2Of5("12345678"));
		
		for (int x = 0 to )
		
	}

	public static String replaceLeadingWithUnderScores(String input) {

		boolean stillReplacing = true;
		String out = "";
		for (char in : input.toCharArray()) {
			if (stillReplacing) {
				if (in == '0') {
					out += '_';
				} else {
					stillReplacing = false;
					out += in;
				}
			} else {
				out += in;
			}
		}
		return out;
	}

}
