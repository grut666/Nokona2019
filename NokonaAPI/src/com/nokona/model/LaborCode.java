package com.nokona.model;

public class LaborCode {
	private long key;
	private int code;
	private String description;
	private double rate;
	
	public LaborCode() {
		this(0, 0,"",0);
	}
	public LaborCode(long key, int code, String description, double rate) {
		this.setKey(key);
		this.setCode(code);
		this.setDescription(description);
		this.setRate(rate);
	}
	public long getKey() {
		return key;
	}
	public void setKey(long key) {
		this.key = key;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
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
		return "LaborCode [key=" + key + ", code=" + code + ", description=" + description + ", rate=" + rate + "]";
	}
	
	
}
