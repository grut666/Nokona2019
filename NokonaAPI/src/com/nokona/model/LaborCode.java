package com.nokona.model;

public class LaborCode {
	private long key;
	private int laborCode;
	private String description;
	private double rate;
	
	public LaborCode() {
		this(0, 0,"",0);
	}
	public LaborCode(long key, int laborCode, String description, double rate) {
		this.setKey(key);
		this.setLaborCode(laborCode);
		this.setDescription(description);
		this.setRate(rate);
	}
	public long getKey() {
		return key;
	}
	public void setKey(long key) {
		this.key = key;
	}
	public int getLaborCode() {
		return laborCode;
	}
	public void setLaborCode(int laborCode) {
		this.laborCode = laborCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	@Override
	public String toString() {
		return "LaborCode [key=" + key + ", laborCode=" + laborCode + ", description=" + description + ", rate=" + rate + "]";
	}
	
	
}
