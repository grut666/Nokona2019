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
	
}
