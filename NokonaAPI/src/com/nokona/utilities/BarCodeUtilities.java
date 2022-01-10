package com.nokona.utilities;

import java.text.SimpleDateFormat;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.commons.lang.StringUtils;

import com.nokona.exceptions.NullInputDataException;
import com.nokona.model.Employee;
import com.nokona.model.Ticket;
import com.nokona.model.TicketDetail;
import com.nokona.model.TicketHeader;

public class BarCodeUtilities {
	public static final int DEFAULT_PAGE_QUANTITY = 1;
	public static final char ESC = 0x1b;
	public static final int POINT_SIZE = 30;
	public static final String PAGE_EJECT = ESC + "&r1F";
	public static char[] strCodeTable = new char[100];
	public static boolean isBuilt = false;
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

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
			strBarCode.append(strCodeTable[index]);
			}
		strBarCode.insert(0, (char) 171).append((char) 172);
		return strBarCode.toString();
	}

	public static void loadStrCodeTable() {
		int index = 0;  // Not concerned about element 0

//		for (int i = 34; i <= 122; i++) {
//			strCodeTable[index++] = (char) i;
//		}
//		for (int i = 161; i <= 170; i++) {
//			strCodeTable[index++] = (char) i;
//		}
		for (int i = 33; i <= 122; i++) {
			strCodeTable[index++] = (char) i;
		}
		for (int i = 161; i <= 170; i++) {
			strCodeTable[index++] = (char) i;
		}
		for (char code : strCodeTable) {
			System.out.print(code + "-");
		}
	}

	public static PrintService getBarCodePrinter() {
		System.out.println("******** Entering getBarCodePrinter ***********************");
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		PrintService barCodePrinter = null;
		if (services != null) {
			for (PrintService service : services) {
				if (service.getName().contains("P3010") || service.getName().contains("P3015")) {
					barCodePrinter = service;
					break;
				}
			}
		}
		System.out.println("******** Bar Code Printer is " + barCodePrinter + " ***********************");
		return barCodePrinter;
	}

	public static String generateEmployeeLabels(Employee emp, int page_quantity) throws NullInputDataException {
		System.out.println("******** Entering generateEmployeeLabels ***********************");
		if (emp == null) {
			throw new NullInputDataException("Employee cannot be null");
		}
		// char esc = 0x1b;
		// int pointSize = 30;
		int tailEnd = page_quantity * 10;
		String fBarCode = emp.getBarCodeID() + "";
		fBarCode = BarCodeUtilities.formatBarCode(fBarCode);
		String employee = (emp.getFirstName() + " " + emp.getLastName() + " - " + emp.getEmpId()).replaceAll(" ", "_");
		String cvtBarCode = BarCodeUtilities.convertBarCode2of5(fBarCode);
		System.out.println(cvtBarCode + " : " + cvtBarCode.length());
		StringBuilder sb = new StringBuilder("");
		sb.append(ESC).append("&l0E"); // Top of Page is 0 lines down
		sb.append(ESC).append("(0U").append(ESC).append("(s1p10v0s0b16602T");
		sb.append(ESC).append("&k330H").append(ESC).append("&l48C"); // Column width and vertical height for 1 inch per
																		// increment
		sb.append(ESC).append("&a1R"); // Set Vertical Coordinate
		sb.append(ESC).append("&a1C"); // Set Horizontal Coordinate
		sb.append(ESC).append("&k330H").append(ESC).append("&l48C"); // Column width and vertical height)
		String formattedEmpID = fBarCode.substring(fBarCode.length() - 4);
		for (int intY = 0; intY < tailEnd; intY++) {
			for (int intX = 0; intX < 3; intX++) {
				sb.append(ESC).append("&a").append(intX + 0.4).append("C").append(ESC).append("&a").append(intY + 0.6)
						.append("R").append(formattedEmpID); // Row 0, Column 0
			}
		}
		sb.append(ESC).append("&a0R"); // Set vertical coordinate
		sb.append(ESC).append("&a0C"); // set horizontal coordinate
		for (int intY = 0; intY < tailEnd; intY++) {
			for (int intX = 0; intX < 3; intX++) {
				sb.append(ESC).append("&a").append(intX + 0.0).append("C").append(ESC).append("&a").append(intY)
						.append("R").append(employee); // Row 0, Column 0
			}
		}
		sb.append(ESC).append("(3Y").append(ESC).append("(s1p").append(POINT_SIZE).append("v0s0b28673T"); // ' Set Bar
																											// Code Font
		sb.append(ESC).append("&k330H").append(ESC).append("&l48C"); // Column width and vertical height for 1 inch per
																		// increment
		sb.append(ESC).append("&a200V"); // Set X and Y at top left
		sb.append(ESC).append("&a100H");
		for (int intY = 0; intY < tailEnd; intY++) {
			for (int intX = 0; intX < 3; intX++) {
				sb.append(ESC).append("&a").append(intX + 0.3).append("C").append(ESC).append("&a").append(intY + 0.5)
						.append("R").append(cvtBarCode); // ' Row 0, Column 0
			}
		}
		sb.append(ESC).append("E");
		return sb.toString().replaceAll("\"", "\\\\\"");
	}
	//******************** TICKETS

	public static String generateTicketLabels(Ticket ticketIn) throws NullInputDataException {
		int intRowCount = 0;
		if (ticketIn == null) {
			throw new NullInputDataException("Ticket cannot be null");
		}

		TicketHeader th = ticketIn.getTicketHeader();
//		System.out.println("****************Header Key is " + th.getKey());
		
		String star44 = "********************************************";
		String star15 = "***************     ";
//		String strJobId = StringUtils.stripEnd("_", String.format("%20s", th.getJobId()).replace(" ", "_"));
		String strJobId = String.format("%-20s", th.getJobId());
		System.out.println("-" + strJobId + "-");
		boolean isRH = strJobId.contains("-RH") ? true : false;
		String strJobDesc = th.getDescription();
		StringUtils.stripEnd("_", strJobDesc.replace(" ", "_"));
		System.out.println("STR JOB DESC: " + strJobDesc);
//		String strTkt1 = StringUtils.stripEnd("_", String.format("%06d", th.getKey()));
		String strTkt1 = String.format("%06d", th.getKey());
		System.out.println("StrTkt1 is " + strTkt1);
		strTkt1.replace(" ", "_");
		System.out.println("StrTkt1 is " + strTkt1);
		String strQtyFormatted = replaceLeadingWithUnderScores(String.format("%03d", th.getQuantity()));
		StringBuilder sb = new StringBuilder("");
		String dateCreated = simpleDateFormat.format(th.getDateCreated());
		sb.append(ESC).append("&l0E"); // Top of Page is 0 lines down
		sb.append(ESC).append("(0U").append(ESC).append("(s1p8v0s0b16602T");
		sb.append(ESC).append("&k330H").append(ESC).append("&l48C");
		sb.append(ESC).append("&a").append(intRowCount).append("R"); // Set Vertical Coordinate
		sb.append(ESC).append("&a0.1C").append(star44).append(ESC).append("&a1.1C").append(star44).append(ESC)
				.append("&a2.1C").append(star44);
		sb.append(ESC).append("&a").append(intRowCount + .14).append("R"); // Set Vertical Coordinate
		sb.append(ESC).append("&a0.3C").append("OFFICE_CONTROL").append(ESC).append("&a1.3C").append(strJobId)
				.append(ESC).append("&a2.3C").append("OFFICE_CONTROL");
		sb.append(ESC).append("&a").append(intRowCount + 0.3).append("R"); // Set Vertical Coordinate
		sb.append(ESC).append("&a0.1C").append("TICKET:").append(strTkt1).append(ESC).append("&a0.5C")
				.append(dateCreated).append(ESC).append("&a1.1C").append(strJobDesc).append(ESC).append("&a2.1C")
				.append("TICKET:").append(strTkt1).append(ESC).append("&a2.5C").append(dateCreated);
		sb.append(ESC).append("&a").append(intRowCount + 0.48).append("R"); // Set Vertical Coordinate
		sb.append(ESC).append("&a0.1C").append(star44).append(ESC).append("&a1.1C").append("TICKET:").append(strTkt1)
				.append(ESC).append("&a1.4C").append("QTY:").append(strQtyFormatted).append(ESC).append("&a1.6C")
				.append(dateCreated).append(ESC).append("&a2.1C").append(star44);
		sb.append(ESC).append("&a").append(intRowCount + 0.65).append("R"); // Set Vertical Coordinate

		sb.append(ESC).append("&a0.1C").append(star44).append(ESC).append("&a1.1C").append(star44).append(ESC)
				.append("&a2.1C").append(star44);
		sb.append(ESC).append("&a").append(intRowCount + .81).append("R"); // Set Vertical Coordinate
		sb.append(ESC).append("(0U").append(ESC).append("(s1p6v0s0b16602T"); // Set 6 pitch arial
		sb.append(ESC).append("&k330H").append(ESC).append("&l48C");
		int intKounter = 1;
		int numberOfLabelRows;
		// ------------------
		int detailIndex = 0;
		int detailCount = ticketIn.getTicketDetails().size();

		// ' Set up to be evenly divisible by three
		intRowCount = intRowCount + 1;
		if (intRowCount >= 10) {
			sb.append(PAGE_EJECT);
			intRowCount = 0;
		}
		TicketDetail td0 = null;
		TicketDetail td1 = null;
		TicketDetail td2 = null;
		numberOfLabelRows = (int) (Math.ceil(detailCount / 3.0)); // How many rows of labels
		System.out.println("***************The rows = " + numberOfLabelRows);
		for (int intLoop = 0; intLoop < numberOfLabelRows; intLoop++) {
			String[] strDesc0 = { star15, star15, star15 };
			String[] strDesc1 = { star15, star15, star15 };
			String[] strDesc2 = { star15, star15, star15 };
			String[] strRate = { "0", "0", "0" };
			String[] strSequence = { "00", "00", "00" };
			String[] strRateFormatted = { "", "", "" };
			String[] strExt = { "", "", "" };
			td0 = detailIndex < detailCount ? ticketIn.getTicketDetails().get(detailIndex) : null;
			td1 = (detailIndex + 1) < detailCount ? ticketIn.getTicketDetails().get(detailIndex + 1) : null;
			td2 = (detailIndex + 2) < detailCount ? ticketIn.getTicketDetails().get(detailIndex + 2) : null;

			StringBuilder line1 = new StringBuilder();
			StringBuilder line2 = new StringBuilder();
			StringBuilder line3 = new StringBuilder();
			StringBuilder line4 = new StringBuilder();
			StringBuilder line5 = new StringBuilder();

			String strCvtBarCode0;
			String strCvtBarCode1;
			String strCvtBarCode2;
			if (td0 != null) {
				String strDescAll = td0.getOperationDescription();
				double rate = isRH ? td0.getHourlyRateSAH() * 1.1 : td0.getHourlyRateSAH();
				int quantity = td0.getQuantity();
				strSequence[0] = String.format("%02d", td0.getSequenceOriginal());
				strRate[0] = String.format("%7.4f", rate);
				strRateFormatted[0] = strRate[0].replace(" ", "_");
				strExt[0] = String.format("%7.4f", rate * quantity).replace(" ", "_");
				strDesc0[0] = StringUtils.left(strDescAll, 17);
				strDescAll = StringUtils.mid(strDescAll, 17, 17);
				strDesc0[1] = StringUtils.left(strDescAll, 17);
				strDescAll = StringUtils.mid(strDescAll, 17, 17);
				strDesc0[2] = StringUtils.left(strDescAll, 17);

			}
			if (td1 != null) {
				String strDescAll = td1.getOperationDescription();
				double rate = isRH ? td1.getHourlyRateSAH() * 1.1 : td1.getHourlyRateSAH();
				int quantity = td1.getQuantity();
				strSequence[1] = String.format("%02d", td1.getSequenceOriginal());
				strRate[1] = String.format("%7.4f", rate);
				strRateFormatted[1] = strRate[1].replace(" ", "_");
				strExt[1] = String.format("%7.4f", rate * quantity).replace(" ", "_");
				strDesc1[0] = StringUtils.left(strDescAll, 17);
				strDescAll = StringUtils.mid(strDescAll, 17, 17);
				strDesc1[1] = StringUtils.left(strDescAll, 17);
				strDescAll = StringUtils.mid(strDescAll, 17, 17);
				strDesc1[2] = StringUtils.left(strDescAll, 17);
			}
			if (td2 != null) {
				String strDescAll = td2.getOperationDescription();
				double rate = isRH ? td2.getHourlyRateSAH() * 1.1 : td2.getHourlyRateSAH();
				int quantity = td2.getQuantity();
				strSequence[2] = String.format("%02d", td2.getSequenceOriginal());
				strRate[2] = String.format("%7.4f", rate);
				strRateFormatted[2] = strRate[2].replace(" ", "_");
				strExt[2] = String.format("%7.4f", rate * quantity).replace(" ", "_");
				strDesc2[0] = StringUtils.left(strDescAll, 17);
				strDescAll = StringUtils.mid(strDescAll, 17, 17);
				strDesc2[1] = StringUtils.left(strDescAll, 17);
				strDescAll = StringUtils.mid(strDescAll, 17, 17);
				strDesc2[2] = StringUtils.left(strDescAll, 17);
			}
			
//			if (intKounter < 3) {
//				intKounter++;
//			} else {
//				intKounter = 1;
			{
				line1.append(ESC).append("&a0.1C").append(strJobId).append(ESC).append("&a0.5C").append("TKT:")
						.append(strTkt1).append("__").append(strSequence[0]).append(ESC).append("&a1.1C")
						.append(strJobId).append(ESC).append("&a1.5C").append("TKT:").append(strTkt1).append("__")
						.append(strSequence[1]).append(ESC).append("&a2.1C").append(strJobId).append(ESC)
						.append("&a2.5C").append("TKT:").append(strTkt1).append("__").append(strSequence[2]);

				line2.append(ESC).append("&a0.1C").append("QTY:").append(strQtyFormatted).append(ESC).append("&a0.4C")
						.append("RATE:_").append(strRateFormatted[0]).append(ESC).append("&a0.7C").append("EXT:")
						.append(ESC).append("&a1.1C").append("QTY:").append(strQtyFormatted).append(ESC)
						.append("&a1.4C").append("RATE:_").append(strRateFormatted[1]).append(ESC).append("&a01.7C")
						.append("EXT:").append(ESC).append("&a2.1C").append("QTY:").append(strQtyFormatted).append(ESC)
						.append("&a2.4C").append("RATE:_").append(strRateFormatted[2]).append(ESC).append("&a2.7C")
						.append("EXT:");
				strDesc0[0] = strDesc0[0] == null ? "" : strDesc0[0].replace(" ", "_");
				StringUtils.stripEnd("_", strDesc0[0]);
				strDesc0[1] = strDesc0[1] == null ? "" : strDesc0[1].replace(" ", "_");
				StringUtils.stripEnd("_", strDesc0[1]);
				strDesc0[2] = strDesc0[2] == null ? "" : strDesc0[2].replace(" ", "_");
				StringUtils.stripEnd("_", strDesc0[2]);

				strDesc1[0] = strDesc1[0] == null ? "" : strDesc1[0].replace(" ", "_");
				StringUtils.stripEnd("_", strDesc0[0]);
				strDesc1[1] = strDesc1[1] == null ? "" : strDesc1[1].replace(" ", "_");
				StringUtils.stripEnd("_", strDesc0[1]);
				strDesc1[2] = strDesc1[2] == null ? "" : strDesc1[2].replace(" ", "_");
				StringUtils.stripEnd("_", strDesc0[2]);
				// **************** good to here

				strDesc2[0] = strDesc2[0] == null ? "" : strDesc2[0].replace(" ", "_");
				StringUtils.stripEnd("_", strDesc0[0]);
				strDesc2[1] = strDesc2[1] == null ? "" : strDesc2[1].replace(" ", "_");
				StringUtils.stripEnd("_", strDesc0[1]);
				strDesc2[2] = strDesc2[2] == null ? "" : strDesc2[2].replace(" ", "_");
				StringUtils.stripEnd("_", strDesc0[2]);

				String fBarCode0 = strTkt1 + strSequence[0];
				String fBarCode1 = strTkt1 + strSequence[1];
				String fBarCode2 = strTkt1 + strSequence[2];
				System.out.println("**************************************");
				System.out.println("*****Length of fBarCode0 is " + fBarCode0.length() + ": Value is " + fBarCode0);
				System.out.println("*****Length of fBarCode1 is " + fBarCode1.length() + ": Value is " + fBarCode1);
				System.out.println("*****Length of fBarCode2 is " + fBarCode2.length() + ": Value is " + fBarCode2);
				
				strCvtBarCode0 = convertBarCode2of5(fBarCode0);
				strCvtBarCode1 = convertBarCode2of5(fBarCode1);
				strCvtBarCode2 = convertBarCode2of5(fBarCode2);
				System.out.println("*****Length of strCvtBarCode0 is " + strCvtBarCode0.length());
				System.out.println("*****Length of strCvtBarCode1 is " + strCvtBarCode1.length());
				System.out.println("*****Length of strCvtBarCode2 is " + strCvtBarCode2.length());
				line3.append(ESC).append("&a0.1C").append(strDesc0[0]).append(ESC).append("&a1.1C").append(strDesc0[1])
						.append(ESC).append("&a2.1C").append(strDesc0[2]);
				line4.append(ESC).append("&a0.1C").append(strDesc1[0]).append(ESC).append("&a1.1C").append(strDesc1[1])
						.append(ESC).append("&a2.1C").append(strDesc1[2]);
				line5.append(ESC).append("&a0.16C").append(fBarCode0).append(ESC).append("&a1.6C").append(fBarCode1)
						.append(ESC).append("&a2.6C").append(fBarCode2);

				
				sb.append(ESC).append("&a").append(intRowCount).append("R");
				sb.append(line1);
				sb.append(ESC).append("&a").append(intRowCount + 0.14).append("R");
				sb.append(line2);
				sb.append(ESC).append("&a").append(intRowCount + 0.3).append("R");
				sb.append(line3);
				sb.append(ESC).append("&a").append(intRowCount + 0.48).append("R");
				sb.append(line4);
				sb.append(ESC).append("&a").append(intRowCount + 0.705).append("R");
				sb.append(ESC).append("(0U").append(ESC).append("(s1p10v0s0b16602T");
				sb.append(ESC).append("&k330H").append(ESC).append("&l48C");
				sb.append(line5);

				sb.append(ESC).append("(3Y").append(ESC).append("(s1p").append(POINT_SIZE).append("v0s0b28673T");
				sb.append(ESC).append("&k330H").append(ESC).append("&l48C");
				// Set Bar Code Font
				System.out.println("Bar Codes are: ");
				for (int i = 0; i < 8; i++) { 
					System.out.print(strCvtBarCode0.charAt(i) + "-");					
				}
//				+ strCvtBarCode0 + strCvtBarCode1 + strCvtBarCode2 );
				sb.append(ESC).append("&a0.5C").append(ESC).append("&a").append(intRowCount + 0.6).append("R")
						.append(strCvtBarCode0);
				sb.append(ESC).append("&a1.5C").append(ESC).append("&a").append(intRowCount + 0.6).append("R")
						.append(strCvtBarCode1);
				sb.append(ESC).append("&a2.5C").append(ESC).append("&a").append(intRowCount + 0.6).append("R")
						.append(strCvtBarCode2);
				intRowCount++;
				if (intRowCount >= 10) {
					sb.append(PAGE_EJECT);
					intRowCount = 0;
				}

				sb.append(ESC).append("&l0E"); // Top of Page is 0 lines down
				sb.append(ESC).append("(0U").append(ESC).append("(s1p8v0s0b16602T"); // ' 8 pitch arial
				sb.append(ESC).append("&k330H").append(ESC).append("&l48C"); // ' Column width and vertical height

				sb.append(ESC).append("&a").append(intRowCount).append("R"); // ' Set Vertical Coordinate")
				sb.append(ESC).append("&a0.1C").append(star44).append(ESC).append("&a1.1C").append(star44).append(ESC)
						.append("&a2.1C").append(star44);

				sb.append(ESC).append("&a").append(intRowCount + 0.14).append("R"); // ' Set Vertical Coordinate)
				sb.append(ESC).append("&a0.3C").append("FACTORY_CONTROL").append(ESC).append("&a1.3C").append(strJobId)
						.append(ESC).append("&a2.3C").append("FACTORY_CONTROL");

				sb.append(ESC).append("&a").append(intRowCount + 0.3).append("R"); // ' Set Vertical Coordinate)
				sb.append(ESC).append("&a0.1C").append("TICKET:").append(strTkt1).append(ESC).append("&a0.5C")
						.append(dateCreated);

				sb.append(ESC).append("&a").append(intRowCount + 0.48).append("R"); // ' Set Vertical Coordinate)
				sb.append(ESC).append("&a0.1C").append(star44).append(ESC).append("&a1.1C").append("TICKET:");
				sb.append(strTkt1).append(ESC).append("&a1.4C").append("QTY:").append(strQtyFormatted);
				sb.append(ESC).append("&a1.6C").append(dateCreated).append(ESC).append("&a2.1C").append(star44);

				sb.append(ESC).append("&a").append(intRowCount + 0.65).append("R"); // ' Set Vertical Coordinate)
				sb.append(ESC).append("&a0.1C").append(star44).append(ESC).append("&a1.1C").append(star44).append(ESC)
						.append("&a2.1C").append(star44);

				intRowCount++;
				if (intRowCount >= 10) {
					intRowCount = 0;
					sb.append(PAGE_EJECT);
				}
				
				//

			}
			detailIndex += 3;
		}
		String output = sb.toString();
		return output.replaceAll("\"", "\\\\\"");
	}

	private static String replaceLeadingWithUnderScores(String input) {
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
