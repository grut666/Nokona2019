package com.nokona.utilities;

import java.text.SimpleDateFormat;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

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
	public static char[] strCodeTable = new char[99];
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

	public static String generateEmployeeLabels(Employee emp, int page_quantity) throws NullInputDataException {
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

	public static String generateTicketLabels(Ticket ticketIn) throws NullInputDataException {
		int intRowCount = 0;
		if (ticketIn == null) {
			throw new NullInputDataException("Ticket cannot be null");
		}

		TicketHeader th = ticketIn.getTicketHeader();
		String star44 = "********************************************";
		String strJobId = th.getJobId();
		boolean isRH = strJobId.contains("-RH") ? true : false;
		String strJobDesc = th.getDescription();
		String strTkt1 = String.format("%06d", th.getKey());
		String strQtyFormatted = replaceLeadingWithUnderScores(String.format("%03d", th.getQuantity()));
		// String strJobDesc = th.gexxxxxxxxxxxxxxxxxxxxxxxxxxxxxx; // Change Ticket to
		// have all info about Ticket including operation and Job info
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
		sb.append(ESC).append("&a0.1C").append(star44).append(ESC).append("&a1.1C").append("TICKET:").append("strTkt1")
				.append(ESC).append("&a1.4C").append("QTY:").append(strQtyFormatted).append(ESC).append("&a1.6C")
				.append(dateCreated).append(ESC).append("&a2.1C").append(star44);
		sb.append(ESC).append("&a").append(intRowCount + 0.65).append("R"); // Set Vertical Coordinate
		sb.append(ESC).append("&a0.1C").append(star44).append(ESC).append("&a1.1C").append(star44).append(ESC)
				.append("&a2.1C").append(star44);
		sb.append(ESC).append("&a2.1C").append(star44).append(ESC).append("&a1.1C").append(star44).append(ESC)
				.append("&a2.1C").append(star44);
		sb.append(ESC).append("&a").append(intRowCount + .81).append("R"); // Set Vertical Coordinate

		int intKounter = 1;
		int theKount;
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
		theKount = (int) (Math.ceil(detailCount / 3.0)); // How many rows of labels
		for (int intLoop = 0; intLoop < theKount; intLoop++) {
			td0 = detailIndex < detailCount ? ticketIn.getTicketDetails().get(detailIndex) : null;
			td1 = (detailIndex + 1) < detailCount ? ticketIn.getTicketDetails().get(detailIndex) : null;
			td1 = (detailIndex + 2) < detailCount ? ticketIn.getTicketDetails().get(detailIndex) : null;

			if (td0 != null) {

			}
			if (td1 != null) {

			}
			if (td2 != null) {

			}

			// If adoOp.EOF Then
			// strRate(intKounter) = 0
			// If InStr(1, strMissing, Left(lstExisting.List(intLoop), 4)) = 0 Then
			// If theOp <> "BLANK" Then
			// strMissing = strMissing & Left(lstExisting.List(intLoop), 4) & " "
			// End If
			// End If
			// strDesc1(intKounter) = "***************"
			// strDesc2(intKounter) = "***************"
			//
			// Else
			// If isRH Then
			// strRate(intKounter) = Format(Str(adoOp("HourlyRateSAH") * 1.1), "@@@@@@@")
			// Else
			// strRate(intKounter) = Format(Str(adoOp("HourlyRateSAH")), "@@@@@@@")
			// End If
			// strRateFormatted(intKounter) = Replace(strRate(intKounter), " ", "_")
			// strDesc = adoOp("Description")
			// strDesc1(intKounter) = Left(strDesc, 17)
			// strDesc = Mid(strDesc, 18)
			// strDesc2(intKounter) = Left(LTrim(strDesc), 17)
			// End If
			// adoOp.Close
			// Set adoOp = Nothing
			// If intLoop < lstExisting.ListCount Then
			// strSeq(intKounter) = lstExisting.ItemData(intLoop)
			// Else
			// strSeq(intKounter) = 0
			// strRate(intKounter) = 0
			// strRateFormatted(intKounter) = 0
			// End If
			// If intKounter = 1 Then
			// intKounter = 2
			// ElseIf intKounter = 2 Then
			// intKounter = 3
			// Else
			// intKounter = 1
			// strExt(1) = Format(Val(strQty) * Val(strRate(1)), "@@@@@@@")
			// strExt(2) = Format(Val(strQty) * Val(strRate(2)), "@@@@@@@")
			// strExt(3) = Format(Val(strQty) * Val(strRate(3)), "@@@@@@@")
			// strExt(1) = Replace(strExt(1), " ", "_")
			// strExt(2) = Replace(strExt(2), " ", "_")
			// strExt(3) = Replace(strExt(3), " ", "_")
			// Line1 = ESC & _
			// "&a0.1C" & strJob & ESC & "&a0.5C" & "TKT:" & strTkt1 & "__" &
			// Format(strSeq(1), "00") & _
			// ESC & _
			// "&a1.1C" & strJob & ESC & "&a1.5C" & "TKT:" & strTkt1 & "__" &
			// Format(strSeq(2), "00") & _
			// ESC & _
			// "&a2.1C" & strJob & ESC & "&a2.5C" & "TKT:" & strTkt1 & "__" &
			// Format(strSeq(3), "00")
			//
			// Line2 = ESC & _
			// "&a0.1C" & "QTY:" & strQtyFormatted & ESC & "&a0.4C" & "RATE:_" &
			// strRateFormatted(1) & _
			// ESC & "&a0.7C" & "EXT:" & strExt(1) & ESC & _
			// "&a1.1C" & "QTY:" & strQtyFormatted & ESC & "&a1.4C" & "RATE:_" &
			// strRateFormatted(2) & _
			// ESC & "&a1.7C" & "EXT:" & strExt(2) & ESC & _
			// "&a2.1C" & "QTY:" & strQtyFormatted & ESC & "&a2.4C" & "RATE:_" &
			// strRateFormatted(3) & _
			// ESC & "&a2.7C" & "EXT:" & strExt(3)
			// strDesc1(1) = Replace(strDesc1(1), " ", "_")
			// strDesc1(2) = Replace(strDesc1(2), " ", "_")
			// strDesc1(3) = Replace(strDesc1(3), " ", "_")
			// strDesc2(1) = Replace(strDesc2(1), " ", "_")
			// strDesc2(2) = Replace(strDesc2(2), " ", "_")
			// strDesc2(3) = Replace(strDesc2(3), " ", "_")
			// strDesc1(1) = RightUnderScoreTrim(strDesc1(1))
			// strDesc1(2) = RightUnderScoreTrim(strDesc1(2))
			// strDesc1(3) = RightUnderScoreTrim(strDesc1(3))
			// strDesc2(1) = RightUnderScoreTrim(strDesc2(1))
			// strDesc2(2) = RightUnderScoreTrim(strDesc2(2))
			// strDesc2(3) = RightUnderScoreTrim(strDesc2(3))
			//
			// FBarCode1 = strTkt1 & Format(strSeq(1), "00")
			// FBarCode2 = strTkt1 & Format(strSeq(2), "00")
			// FBarCode3 = strTkt1 & Format(strSeq(3), "00")
			// strCvtBarCode1 = ConvertBarCode2of5(FBarCode1)
			// strCvtBarCode2 = ConvertBarCode2of5(FBarCode2)
			// strCvtBarCode3 = ConvertBarCode2of5(FBarCode3)
			//
			// Line3 = ESC & "&a0.1C" & strDesc1(1) & _
			// ESC & "&a1.1C" & strDesc1(2) & _
			// ESC & "&a2.1C" & strDesc1(3)
			// Line4 = ESC & "&a0.1C" & strDesc2(1) & _
			// ESC & "&a1.1C" & strDesc2(2) & _
			// ESC & "&a2.1C" & strDesc2(3)
			// Line5 = ESC & "&a0.6C" & FBarCode1 & _
			// ESC & "&a1.6C" & FBarCode2 & _
			// ESC & "&a2.6C" & FBarCode3 // Back to 8 point
			//
			//
			// Put #1, , ESC & "(0U" & ESC & "(s1p6v0s0b16602T" ' 6 pitch arial
			// Put #1, , ESC & "&k330H" & ESC & "&l48C" ' Column width and vertical height
			// for 1 inch per increment
			// Put #1, , ESC & "&a" & intRowCount & "R" ' Set Vertical Coordinate
			// Put #1, , Line1
			// Put #1, , ESC & "&a" & intRowCount + 0.14 & "R" ' Set Vertical Coordinate
			// Put #1, , Line2
			//
			// Put #1, , ESC & "&a" & intRowCount + 0.3 & "R" ' Set Vertical Coordinate
			// Put #1, , Line3
			// Put #1, , ESC & "&a" & intRowCount + 0.48 & "R" ' Set Vertical Coordinate
			// Put #1, , Line4
			// Put #1, , ESC & "&a" & intRowCount + 0.705 & "R" ' Set Vertical Coordinate
			// Put #1, , ESC & "(0U" & ESC & "(s1p10v0s0b16602T"
			// Put #1, , ESC & "&k330H" & ESC & "&l48C"
			// Put #1, , Line5
			// ' ******************************
			//
			// Put #1, , ESC & "(3Y" & ESC & "(s1p" & PointSize & "v0s0b28673T" ' Set Bar
			// Code Font
			// Put #1, , ESC & "&k330H" & ESC & "&l48C" ' Column width and vertical height
			// for 1 inch per increment
			//
			// Put #1, , ESC & "&a0.5C" & ESC & "&a" & intRowCount + 0.6 & "R" &
			// strCvtBarCode1 ' Row 0, Column 0
			// Put #1, , ESC & "&a1.5C" & ESC & "&a" & intRowCount + 0.6 & "R" &
			// strCvtBarCode2 ' Row 0, Column 0
			// Put #1, , ESC & "&a2.5C" & ESC & "&a" & intRowCount + 0.6 & "R" &
			// strCvtBarCode3 ' Row 0, Column 0
			//
			// ' ************************************************************
			// intRowCount = intRowCount + 1
			// If intRowCount >= 10 Then
			// Put #1, , PageEject
			// intRowCount = 0
			// End If
			// End If
			//
			// Next
			//
			// If Len(strMissing) > 0 Then
			// MsgBox "Missing the following OP Code(s):" & vbCrLf & vbCrLf & strMissing
			// End If

			// Put Ticket printing code here
			// sb.append("Ticket is : " + ticketIn);

		}
		// ' ************************* Print the Trailer
		//
		// '
		// *******************************************************************************************
		//

		// Put #1, , ESC & "&l0E" ' - Top of Page is 0 lines down
		// Put #1, , ESC & "(0U" & ESC & "(s1p8v0s0b16602T" ' 8 pitch arial
		// Put #1, , ESC & "&k330H" & ESC & "&l48C" ' Column width and vertical height
		// for 1 inch per increment
		//
		sb.append(ESC).append("&l0E"); // Top of Page is 0 lines down
		sb.append(ESC).append("(0U").append(ESC).append("(s1p8v0s0b16602T"); // ' 8 pitch arial
		sb.append(ESC).append("&k330H").append(ESC).append("&l48C"); // ' Column width and vertical height

		// Put #1, , ESC & "&a" & (intRowCount) & "R" ' Set Vertical Coordinate"
		// Put #1, , ESC & "&a0.1C" & String(44, "*") & ESC & "&a1.1C" & String(44, "*")
		// & ESC & "&a2.1C" & String(44, "*")
		//
		sb.append(ESC).append("&a").append(intRowCount).append("R"); // ' Set Vertical Coordinate")
		sb.append(ESC).append("&a0.1C").append(star44).append(ESC).append("&a1.1C").append(star44).append(ESC)
				.append("&a2.1C").append(star44);

		// Put #1, , ESC & "&a" & (intRowCount + 0.14) & "R" ' Set Vertical Coordinate
		// Put #1, , ESC & "&a0.3C" & "FACTORY_CONTROL" & ESC & "&a1.3C" & strJob & ESC
		// & "&a2.3C" & "FACTORY_CONTROL"
		//
		sb.append(ESC).append("&a").append(intRowCount + 0.14).append("R"); // ' Set Vertical Coordinate)
		sb.append(ESC).append("&a0.3C").append("FACTORY_CONTROL").append(ESC).append("&a1.3C").append(strJobId)
				.append(ESC).append("&a2.3C").append("FACTORY_CONTROL");

		// Put #1, , ESC & "&a" & (intRowCount + 0.3) & "R" ' Set Vertical Coordinate
		// Put #1, , ESC & "&a0.1C" & "TICKET:" & strTkt1 & ESC & "&a0.5C" &
		// Format(DateCreated, "mm/dd/yyyy") _
		// & ESC & "&a1.1C" & strJobDesc & ESC & "&a2.1C" _
		// & "TICKET:" & strTkt1 & ESC & "&a2.5C" & Format(DateCreated, "mm/dd/yyyy")

		sb.append(ESC).append("&a").append(intRowCount + 0.3).append("R"); // ' Set Vertical Coordinate)
		sb.append(ESC).append("&a0.1C").append("TICKET:").append(strTkt1).append(ESC).append("&a0.5C")
				.append(dateCreated);
		//
		// Put #1, , ESC & "&a" & (intRowCount + 0.48) & "R" ' Set Vertical Coordinate
		// Put #1, , ESC & "&a0.1C" & String(44, "*") & ESC & "&a1.1C" & "TICKET:" &
		// strTkt1 & ESC & "&a1.4C" & "QTY:" & strQtyFormatted & _
		// ESC & "&a1.6C" & Format(DateCreated, "mm/dd/yyyy") & ESC & "&a2.1C" &
		// String(44, "*")

		sb.append(ESC).append("&a").append(intRowCount + 0.48).append("R"); // ' Set Vertical Coordinate)
		sb.append(ESC).append("&a0.1C").append(star44).append(ESC).append("&a1.1C").append("TICKET:");
		sb.append(strTkt1).append(ESC).append("&a1.4C").append("QTY:").append(strQtyFormatted);
		sb.append(ESC).append("&a1.6C").append(dateCreated).append(ESC).append("&a2.1C").append(star44);
		//
		// Put #1, , ESC & "&a" & (intRowCount + 0.65) & "R" ' Set Vertical Coordinate
		// Put #1, , ESC & "&a0.1C" & String(44, "*") & ESC & "&a1.1C" & String(44, "*")
		// & ESC & "&a2.1C" & String(44, "*")

		sb.append(ESC).append("&a").append(intRowCount + 0.65).append("R"); // ' Set Vertical Coordinate)
		sb.append(ESC).append("&a0.1C").append(star44).append(ESC).append("&a1.1C").append(star44).append(ESC)
				.append("&a2.1C").append(star44);
		// intRowCount = intRowCount + 1
		// If intRowCount >= 10 Then
		// intRowCount = 0
		// End If
		// Put #1, , PageEject ' Unconditional Page Eject after each set, per Paul
		// intRowCount = 0 ' Reset back to top of next page

		intRowCount++;
		if (intRowCount >= 10) {
			intRowCount = 0;
		}
		sb.append(PAGE_EJECT);
		//
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
