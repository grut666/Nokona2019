package com.nokona.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class LaborCode {
	private long key;
	private int laborCode;
	private String description;
	private double rate;
	
	public LaborCode() {
		this(-1, 0,"",0);
	}

}
