package com.nokona.model;

public class Labels {
	private static final int DEFAULT_PAGE_QUANTITY = 1;
	private String labels;

	public Labels() {
		super();
	}
	public Labels(Employee emp) {
		generateLabels(emp, DEFAULT_PAGE_QUANTITY);
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
		StringBuilder sb = new StringBuilder("");
		labels = "Label Stuff Will Be Here.  Quantity: " + page_quantity;
	}
}
