package com.nokona.model;

public class Labels {
	private String labels;

	public Labels() {
		super();
	}
	public Labels(Employee emp) {
		generateLabels(emp);
	}
	public String getLabels() {
		return labels;
	}
	public void setLabels(String labels) {
		this.labels = labels;
	}
	protected void generateLabels(Employee emp) {
		labels = "Label Stuff Will Be Here";
	}
}
