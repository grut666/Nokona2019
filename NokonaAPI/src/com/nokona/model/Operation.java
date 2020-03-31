package com.nokona.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class Operation {
	private String opCode;
	private String description;
	private double hourlyRateSAH;
	private int laborCode;
	private long key;
	private int lastStudyYear;
	
	public Operation() {
		this("", "", 0, 0, -1, 0);
	}
//	public Operation(String opCode, String description, double hourlyRateSAH, int laborCode, long key, int lastStudyYear) {
//		this.setOpCode(opCode);
//		this.setDescription(description);
//		this.setHourlyRateSAH(hourlyRateSAH);
//		this.setLaborCode(laborCode);
//		this.setKey(key);
//		this.setLastStudyYear(lastStudyYear);
//
//		
//	}
//
//	public String getOpCode() {
//		return opCode;
//	}
//	public void setOpCode(String opCode) {
//		this.opCode = opCode;
//	}
//	public String getDescription() {
//		return description;
//	}
//	public void setDescription(String description) {
//		this.description = description;
//	}
//	public double getHourlyRateSAH() {
//		return hourlyRateSAH;
//	}
//	public void setHourlyRateSAH(double hourlyRateSAH) {
//		this.hourlyRateSAH = hourlyRateSAH;
//	}
//	public int getLaborCode() {
//		return laborCode;
//	}
//	public void setLaborCode(int laborCode) {
//		this.laborCode = laborCode;
//	}
//	public long getKey() {
//		return key;
//	}
//	public void setKey(long key) {
//		this.key = key;
//	}
//	public int getLastStudyYear() {
//		return lastStudyYear;
//	}
//	public void setLastStudyYear(int lastStudyYear) {
//		this.lastStudyYear = lastStudyYear;
//	}
//	@Override
//	public String toString() {
//		return "Operation [key=" + key + ", opCode=" + opCode + ", description=" + description + ", hourlyRateSAH="
//				+ hourlyRateSAH + ", laborCode=" + laborCode + ", lastStudyYear=" + lastStudyYear + "]";
//	}
//	
	
}
