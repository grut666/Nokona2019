package com.nokona.model;

import com.nokona.utilities.BarCodeUtilities;

public class Labels {
	private String labels;

	public Labels() {
		super();
	}

	public Labels(Employee emp) {
		generateLabels(emp, BarCodeUtilities.DEFAULT_PAGE_QUANTITY);
	}

	public Labels(Employee emp, int page_quantity) {
		generateLabels(emp, page_quantity);
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	protected void generateLabels(Employee emp, int page_quantity) {
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
		labels = sb.toString();
	}

}



