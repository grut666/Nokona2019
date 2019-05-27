package com.nokona.utilities;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import com.nokona.exceptions.NullInputDataException;
import com.nokona.model.Employee;

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
			int index = Integer.parseInt(subStr);
			if (index == 0) {
				strBarCode.append("!");
			} else {
				strBarCode.append(strCodeTable[index - 1]);
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

	public static PrintService getBarCodePrinter() {
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		PrintService barCodePrinter = null;
		if (services != null) {
			for (PrintService service : services) {
				if (service.getName().contains("P3010")) {
					barCodePrinter = service;
					break;
				}
			}
		}
		return barCodePrinter;
	}
	public static String generateLabels(Employee emp, int page_quantity) throws NullInputDataException {
		if (emp == null) {
			throw new NullInputDataException("Employee cannot be null");
		}
		char esc = 0x1b;
		int pointSize = 30;
		int tailEnd = page_quantity * 10;
		String fBarCode = emp.getBarCodeID() + "";
		fBarCode = BarCodeUtilities.formatBarCode(fBarCode);
		String employee = (emp.getFirstName() + " " + emp.getLastName() + " - " + emp.getEmpId()).replaceAll(" ", "_");
		String cvtBarCode = BarCodeUtilities.convertBarCode2of5(fBarCode);
		System.out.println(cvtBarCode + " : " + cvtBarCode.length());
		StringBuilder sb = new StringBuilder("");
		sb.append(esc).append( "&l0E"); // Top of Page is 0 lines down
		sb.append(esc).append("(0U").append(esc).append("(s1p10v0s0b16602T");
		sb.append(esc).append("&k330H").append(esc).append("&l48C"); // Column width and vertical height for 1 inch per increment
		sb.append(esc).append("&a1R"); // Set Vertical Coordinate
		sb.append(esc).append("&a1C"); // Set Horizontal Coordinate
		sb.append(esc).append("&k330H").append(esc).append("&l48C"); // Column width and vertical height)
		String formattedEmpID = fBarCode.substring(fBarCode.length() - 4);
		for (int intY = 0; intY < tailEnd; intY++) {
			for (int intX = 0 ; intX < 3; intX++) {
				sb.append(esc).append("&a").append(intX + 0.4).append("C").append(esc).append("&a").append(intY + 0.6)
				.append("R").append(formattedEmpID); //  Row 0, Column 0
			}
		}
		sb.append(esc).append("&a0R"); // Set vertical coordinate
		sb.append(esc).append("&a0C"); // set horizontal coordinate
		for (int intY = 0; intY < tailEnd; intY++) {
			for (int intX = 0 ; intX < 3; intX++) {
				sb.append(esc).append("&a").append(intX + 0.0).append("C").append(esc).append("&a").append(intY)
				.append("R").append(employee); //  Row 0, Column 0
			}
		}
		sb.append(esc).append("(3Y").append(esc).append("(s1p").append(pointSize).append("v0s0b28673T"); // ' Set Bar Code Font
		sb.append(esc).append("&k330H").append(esc).append("&l48C"); // Column width and vertical height for 1 inch per increment
		sb.append(esc).append("&a200V"); // Set X and Y at top left
		sb.append(esc).append("&a100H");
		for (int intY = 0; intY < tailEnd; intY++) {
			for (int intX = 0 ; intX < 3; intX++) {
				sb.append(esc).append("&a").append(intX + 0.3).append("C").append(esc).append("&a")
				.append(intY + 0.5).append("R").append(cvtBarCode); // ' Row 0, Column 0
			}
		}
		sb.append(esc).append("E");
		return sb.toString();
	}
}
