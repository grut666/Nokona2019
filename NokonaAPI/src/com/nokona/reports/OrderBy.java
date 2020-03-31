package com.nokona.reports;

public class OrderBy {
	private String columnName;
	private boolean isAscending;
	public OrderBy() {
		super();
	}
	public OrderBy(String columnName, boolean isAscending) {
		this.columnName = columnName;
		this.isAscending = isAscending;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public boolean isAscending() {
		return isAscending;
	}
	public void setAscending(boolean isAscending) {
		this.isAscending = isAscending;
	}
	@Override
	public String toString() {
		return "OrderBy [columnName=" + columnName + ", isAscending=" + isAscending + "]";
	}
	
}
