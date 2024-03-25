package com.nokona.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class CategoryCode {
	private long key;
	private String categoryCode;
	private String description;
	public CategoryCode() {
		this(-1, "","");
	}

}
