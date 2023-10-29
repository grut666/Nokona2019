package com.nokona.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class LevelCode {
	private long key;
	private String levelCode;
	private double rate;
	
	public LevelCode() {
		this(-1, "", 0);
	}

}
