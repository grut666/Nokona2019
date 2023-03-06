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

public class BarCodeUtilitiesOld {
	public static final int DEFAULT_PAGE_QUANTITY = 1;
	public static final char ESC = 0x1b;
	public static final int POINT_SIZE = 30;
	public static final String PAGE_EJECT = ESC + "&r1F";
	public static char[] strCodeTable = new char[100];
	public static boolean isBuilt = false;
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

	public static String formatBarCode(String strIn) {
		String strOut = strIn;;
		if (!isBuilt) {
			loadStrCodeTable();
			isBuilt = true;
		}
		int strLen = strIn.length();
		if (strLen < 8) {
			strOut = new String(new char[8 - strLen]).replace("\0", "0") + strIn;
		} else if (strLen % 2 == 1) {
			strOut = "0" + strIn;
		}
		return strOut;
	}

	public static String convertBarCode2of5(String strIn) {
		// System.out.println("strIn is " + "X" + strIn + "X");
		StringBuilder strBarCode = new StringBuilder("");
		for (int i = 0; i < strIn.length(); i += 2) {
			String subStr = strIn.substring(i, i + 2);
			// System.out.println("substr is " + subStr);
			int index = Integer.parseInt(subStr);
			strBarCode.append(strCodeTable[index]);
		}

		strBarCode.insert(0, (char) 171);
		strBarCode.append((char) 172);
		// System.out.println("Final is " + strBarCode.toString());
		return strBarCode.toString();
	}

	public static void loadStrCodeTable() {
		int index = 0; // Not concerned about element 0

		// for (int i = 34; i <= 122; i++) {
		// strCodeTable[index++] = (char) i;
		// }
		// for (int i = 161; i <= 170; i++) {
		// strCodeTable[index++] = (char) i;
		// }
		for (int i = 33; i <= 122; i++) {
			strCodeTable[index++] = (char) i;
		}
		for (int i = 161; i <= 170; i++) {
			strCodeTable[index++] = (char) i;
		}
		// for (char code : strCodeTable) {
		// System.out.print(code + "-");
		// }
	}

	public static PrintService getBarCodePrinter() {
		// System.out.println("******** Entering getBarCodePrinter
		// ***********************");
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		PrintService barCodePrinter = null;
		if (services != null) {
			for (PrintService service : services) {
				System.out.println("Service is : " + service.getName());
				if (service.getName().contains("P3010") || service.getName().contains("P3015")) {
					System.out.println("**************Found it: " +  service.getName() + "********************************");
					barCodePrinter = service;
					break;
				}
			}
		}
		// System.out.println("******** Bar Code Printer is " + barCodePrinter + "
		// ***********************");
		return barCodePrinter;
	}

	public static String generateEmployeeLabels(Employee emp, int page_quantity) throws NullInputDataException {
		// System.out.println("******** Entering generateEmployeeLabels
		// ***********************");
		if (emp == null) {
			throw new NullInputDataException("Employee cannot be null");
		}
		// char esc = 0x1b;
		// int pointSize = 30;
		int tailEnd = page_quantity * 10;
		String fBarCode = emp.getBarCodeID() + "";
		fBarCode = BarCodeUtilitiesOld.formatBarCode(fBarCode);
		String employee = (emp.getFirstName() + " " + emp.getLastName() + " - " + emp.getEmpId()).replaceAll(" ", "_");
		String cvtBarCode = BarCodeUtilitiesOld.convertBarCode2of5(fBarCode);
		// System.out.println(cvtBarCode + " : " + cvtBarCode.length());
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
	// ******************** TICKETS

	public static String generateTicketLabels(Ticket ticketIn) throws NullInputDataException {
		int intRowCount = 0;
		if (ticketIn == null) {
			throw new NullInputDataException("Ticket cannot be null");
		}
//		TicketDetail ticketComplete = new TicketDetail(ticketIn.getTicketHeader().getKey()); // 1-arg constructor
		TicketHeader th = ticketIn.getTicketHeader();
//		ticketIn.getTicketDetails().add(ticketComplete); // Add to produce a final ticket with opCode ZZZ.  When scanned, shows job completed

		String star44 = "********************************************";
		String star15 = "***************     ";

		String strJobId = String.format("%-20s", th.getJobId());
		// System.out.println("-" + strJobId + "-");
		boolean isRH = strJobId.contains("-RH") ? true : false;
		String strJobDesc = th.getDescription();
		// StringUtils.stripEnd("_", strJobDesc.replace(" ", "_"));
		strJobDesc = strJobDesc.replace(" ", "_");
		// System.out.println("STR JOB DESC: " + strJobDesc);

		String strTkt1 = String.format("%06d", th.getKey());
		// System.out.println("StrTkt1 is " + strTkt1);
		strTkt1.replace(" ", "_");
		// System.out.println("StrTkt1 is " + strTkt1);
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
		// System.out.println("***************The rows = " + numberOfLabelRows);
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
				strDescAll = strDescAll.replaceAll("\\s+", "_");
				double rate = isRH ? td0.getHourlyRateSAH() * 1.1 : td0.getHourlyRateSAH();
				int quantity = td0.getStandardQuantity();
				strSequence[0] = String.format("%02d", td0.getSequenceOriginal());
				strRate[0] = String.format("%7.4f", rate);
				strRateFormatted[0] = strRate[0].replaceAll(" ", "_");
				strExt[0] = String.format("%7.4f", rate * quantity);
				strExt[0] = strExt[0].replaceAll(" ", "_");
				strDesc0[0] = StringUtils.left(strDescAll, 17);
				strDescAll = StringUtils.mid(strDescAll, 17, 17);
				strDesc0[1] = StringUtils.left(strDescAll, 17);
				strDescAll = StringUtils.mid(strDescAll, 17, 17);
				strDesc0[2] = StringUtils.left(strDescAll, 17);

			}
			if (td1 != null) {
				String strDescAll = td1.getOperationDescription();
				strDescAll = strDescAll.replaceAll("\\s+", "_");
				double rate = isRH ? td1.getHourlyRateSAH() * 1.1 : td1.getHourlyRateSAH();
				int quantity = td1.getStandardQuantity();
				strSequence[1] = String.format("%02d", td1.getSequenceOriginal());
				strRate[1] = String.format("%7.4f", rate);
				strRateFormatted[1] = strRate[1].replace(" ", "_");
				strExt[1] = String.format("%7.4f", rate * quantity);
				strExt[1] = strExt[1].replaceAll(" ", "_");
				strDesc1[0] = StringUtils.left(strDescAll, 17);
				strDescAll = StringUtils.mid(strDescAll, 17, 17);
				strDesc1[1] = StringUtils.left(strDescAll, 17);
				strDescAll = StringUtils.mid(strDescAll, 17, 17);
				strDesc1[2] = StringUtils.left(strDescAll, 17);
			}
			if (td2 != null) {
				String strDescAll = td2.getOperationDescription();
				strDescAll = strDescAll.replaceAll("\\s+", "_");
				double rate = isRH ? td2.getHourlyRateSAH() * 1.1 : td2.getHourlyRateSAH();
				int quantity = td2.getStandardQuantity();
				strSequence[2] = String.format("%02d", td2.getSequenceOriginal());
				strRate[2] = String.format("%7.4f", rate);
				strRateFormatted[2] = strRate[2].replace(" ", "_");
				strExt[2] = String.format("%7.4f", rate * quantity);
				strExt[2] = strExt[2].replaceAll(" ", "_");
				strDesc2[0] = StringUtils.left(strDescAll, 17);
				strDescAll = StringUtils.mid(strDescAll, 17, 17);
				strDesc2[1] = StringUtils.left(strDescAll, 17);
				strDescAll = StringUtils.mid(strDescAll, 17, 17);
				strDesc2[2] = StringUtils.left(strDescAll, 17);
			}

			{
				sb.append(ESC).append("(0U").append(ESC).append("(s1p6v0s0b16602T"); // ' 6 pitch arial
				sb.append(ESC).append("&k330H").append(ESC).append("&l48C"); // ' Column width and vertical height
				sb.append(ESC).append("&a").append(intRowCount).append("R"); // Move to Row number
				// Line 1 - Job and TKT:
				line1.append(ESC).append("&a0.1C").append(strJobId).append(ESC).append("&a0.5C").append("TKT:")
						.append(strTkt1).append("__").append(strSequence[0]).append(ESC).append("&a1.1C")
						.append(strJobId).append(ESC).append("&a1.5C").append("TKT:").append(strTkt1).append("__")
						.append(strSequence[1]).append(ESC).append("&a2.1C").append(strJobId).append(ESC)
						.append("&a2.5C").append("TKT:").append(strTkt1).append("__").append(strSequence[2]);
				// Line 2 - QTY: and RATE and EXT
				line2.append(ESC).append("&a0.1C").append("QTY:").append(strQtyFormatted).append(ESC).append("&a0.4C")
						.append("RATE:_").append(strRateFormatted[0]).append(ESC).append("&a0.7C").append("EXT:")
						.append(strExt[0]).append(ESC).append("&a1.1C").append("QTY:").append(strQtyFormatted).append(ESC)
						.append("&a1.4C").append("RATE:_").append(strRateFormatted[1]).append(ESC).append("&a01.7C")
						.append("EXT:").append(strExt[1]).append(ESC).append("&a2.1C").append("QTY:").append(strQtyFormatted).append(ESC)
						.append("&a2.4C").append("RATE:_").append(strRateFormatted[2]).append(ESC).append("&a2.7C")
						.append("EXT:").append(strExt[2]);
				strDesc0[0] = strDesc0[0] == null ? "" : strDesc0[0].replace(" ", "_");
				strDesc0[0] = StringUtils.stripEnd(strDesc0[0], "_");
				strDesc0[1] = strDesc0[1] == null ? "" : strDesc0[1].replace(" ", "_");
				strDesc0[1] = StringUtils.stripEnd(strDesc0[1], "_");
				strDesc0[2] = strDesc0[2] == null ? "" : strDesc0[2].replace(" ", "_");
				strDesc0[2] = StringUtils.stripEnd(strDesc0[2], "_");

				strDesc1[0] = strDesc1[0] == null ? "" : strDesc1[0].replace(" ", "_");
				strDesc1[0] = StringUtils.stripEnd(strDesc1[0], "_");
				strDesc1[1] = strDesc1[1] == null ? "" : strDesc1[1].replace(" ", "_");
				strDesc1[1] = StringUtils.stripEnd(strDesc1[1], "_");
				strDesc1[2] = strDesc1[2] == null ? "" : strDesc1[2].replace(" ", "_");
				strDesc1[2] = StringUtils.stripEnd(strDesc1[2], "_");
				// **************** good to here

				strDesc2[0] = strDesc2[0] == null ? "" : strDesc2[0].replace(" ", "_");
				strDesc2[0] = StringUtils.stripEnd(strDesc2[0], "_");
				strDesc2[1] = strDesc2[1] == null ? "" : strDesc2[1].replace(" ", "_");
				strDesc2[1] = StringUtils.stripEnd(strDesc2[1], "_");
				strDesc2[2] = strDesc2[2] == null ? "" : strDesc2[2].replace(" ", "_");
				strDesc2[2] = StringUtils.stripEnd(strDesc2[2], "_");

				String fBarCode0 = strTkt1 + strSequence[0];
				String fBarCode1 = strTkt1 + strSequence[1];
				String fBarCode2 = strTkt1 + strSequence[2];
				// System.out.println("**************************************");
				// System.out.println("*****Length of fBarCode0 is " + fBarCode0.length() + ":
				// Value is " + fBarCode0);
				// System.out.println("*****Length of fBarCode1 is " + fBarCode1.length() + ":
				// Value is " + fBarCode1);
				// System.out.println("*****Length of fBarCode2 is " + fBarCode2.length() + ":
				// Value is " + fBarCode2);
				strCvtBarCode0 = convertBarCode2of5(BarCodeUtilitiesOld.formatBarCode(fBarCode0));
				strCvtBarCode1 = convertBarCode2of5(BarCodeUtilitiesOld.formatBarCode(fBarCode1));
				strCvtBarCode2 = convertBarCode2of5(BarCodeUtilitiesOld.formatBarCode(fBarCode2));
				// Line 3 - Description part 1
				line3.append(ESC).append("&a0.1C").append(strDesc0[0]).append(ESC).append("&a1.1C").append(strDesc1[0])
						.append(ESC).append("&a2.1C").append(strDesc2[0]);
				// Line 4 - Description part 2
				line4.append(ESC).append("&a0.1C").append(strDesc0[1]).append(ESC).append("&a1.1C").append(strDesc1[1])
						.append(ESC).append("&a2.1C").append(strDesc2[1]);
				// Line 5 - Bar Code
				line5.append(ESC).append("&a0.6C").append(fBarCode0).append(ESC).append("&a1.6C").append(fBarCode1)
						.append(ESC).append("&a2.6C").append(fBarCode2);
				System.err.println(fBarCode0);
				System.err.println(fBarCode1);
				System.err.println(fBarCode2);
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

				sb.append(ESC).append("&a0.5C").append(ESC).append("&a").append(intRowCount + 0.6).append("R")
						.append(strCvtBarCode0);
				sb.append(ESC).append("&a1.5C").append(ESC).append("&a").append(intRowCount + 0.6).append("R")
						.append(strCvtBarCode1);
				sb.append(ESC).append("&a2.5C").append(ESC).append("&a").append(intRowCount + 0.6).append("R")
						.append(strCvtBarCode2);
//				System.out.println("Count is " + intRowCount);
				intRowCount++;
				if (intRowCount >= 10) {
					sb.append(PAGE_EJECT);
					intRowCount = 0;
				}
			}
			detailIndex += 3;
		}

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
				.append(dateCreated).append(ESC).append("&a1.1C").append(strJobDesc).append(ESC).append("&a2.1C").append("TICKET:")
				.append(strTkt1).append(ESC).append("&a2.5C").append(dateCreated);

		sb.append(ESC).append("&a").append(intRowCount + 0.48).append("R"); // ' Set Vertical Coordinate)
		sb.append(ESC).append("&a0.1C").append(star44).append(ESC).append("&a1.1C").append("TICKET:");
		sb.append(strTkt1).append(ESC).append("&a1.4C").append("QTY:").append(strQtyFormatted);
		sb.append(ESC).append("&a1.6C").append(dateCreated).append(ESC).append("&a2.1C").append(star44);

		sb.append(ESC).append("&a").append(intRowCount + 0.65).append("R"); // ' Set Vertical Coordinate)
		sb.append(ESC).append("&a0.1C").append(star44).append(ESC).append("&a1.1C").append(star44).append(ESC)
				.append("&a2.1C").append(star44);

		String output = sb.toString();
		return output.replaceAll("\"", "\\\\\""); // What the hell is this?
//		return output; 

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
