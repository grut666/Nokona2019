package com.nokona.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class Employee {
	private long key;
	private String lastName;
	private String firstName;
	private int barCodeID;
	private int laborCode;
	private String empId;
	boolean active;
	
	public Employee() {
		this(-1, "", "", 0, 0, "", false);
	}
}
