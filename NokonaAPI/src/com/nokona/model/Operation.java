package com.nokona.model;

public class Operation {
	private long key;
	private String opCode;
	private String description;
	private double hourlyRateSAH;
	private int laborCode;
	private int lastStudyYear;
	private boolean active;
	
	public Operation() {
		this("", "", 0, 0, 0, 0, false);
	}
	public Operation(String opCode, String description, double hourlyRateSAH, int laborCode, long key, int lastStudyYear, boolean active) {
		this.setOpCode(opCode);
		this.setDescription(description);
		this.setHourlyRateSAH(hourlyRateSAH);
		this.setLaborCode(laborCode);
		this.setKey(key);
		this.setLastStudyYear(lastStudyYear);
		this.setActive(active);
		
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getOpCode() {
		return opCode;
	}
	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getHourlyRateSAH() {
		return hourlyRateSAH;
	}
	public void setHourlyRateSAH(double hourlyRateSAH) {
		this.hourlyRateSAH = hourlyRateSAH;
	}
	public int getLaborCode() {
		return laborCode;
	}
	public void setLaborCode(int laborCode) {
		this.laborCode = laborCode;
	}
	public long getKey() {
		return key;
	}
	public void setKey(long key) {
		this.key = key;
	}
	public int getLastStudyYear() {
		return lastStudyYear;
	}
	public void setLastStudyYear(int lastStudyYear) {
		this.lastStudyYear = lastStudyYear;
	}
	
}
