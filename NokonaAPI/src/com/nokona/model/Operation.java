package com.nokona.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class Operation {
	private long key;
	private String opCode;
	private String description;
	private String levelCode;
	private int laborCode;
	private double hourlyRateSAH;
	private int lastStudyYear;
	
	public Operation() {
		this(-1, "", "", "", 0, 0, 0);
	}
	
}
