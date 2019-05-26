package com.nokona.utilities;

public class BarCodeUtilities {
	public static final int DEFAULT_PAGE_QUANTITY = 1;
	public static char[] strCodeTable = new char[99];
	public static boolean isBuilt = false;

	public static String formatBarCode(String strIn) {
		if (!isBuilt) {
			loadStrCodeTable();
		}
		int strLen = strIn.length();
		if (strLen < 8) {
			strIn = new String(new char[8 - strLen]).replace("\0", "0") + strIn;
		} else if (strLen % 2 == 1) {
			strIn = "0" + strIn;
		}
		return strIn;
	}

	public static String convertBarCode2of5(String strIn) {
		StringBuilder strBarCode = new StringBuilder("");
		for (int i = 0; i < strIn.length(); i += 2) {
			String subStr = strIn.substring(i, i + 2);
			if (Integer.parseInt(subStr) == 0) {
				strBarCode.append("!");
			} else {
				strBarCode.append(strCodeTable[i]);
			}
		}
		strBarCode.insert(0, (char) 171).append((char) 172);
		return strBarCode.toString();
	}

	private static void loadStrCodeTable() {
		int index = 0;

		for (int i = 34; i <= 122; i++) {
			strCodeTable[index++] = (char) i;
		}
		for (int i = 161; i <= 170; i++) {
			strCodeTable[index++] = (char) i;
		}
	}
}
