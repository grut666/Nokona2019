package com.nokona.testing;

public class PrintScratchPad {

	public static void main(String[] args) {
//		System.out.println(replaceLeadingWithUnderScores(String.format("%06d", 1230)));
		System.out.println((int)(Math.ceil(12 / 3.0)));
		System.out.println((int)(Math.ceil(13 / 3.0)));
		System.out.println((int)(Math.ceil(14 / 3.0)));
		System.out.println((int)(Math.ceil(15 / 3.0)));
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