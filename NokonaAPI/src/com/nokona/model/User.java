package com.nokona.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class User {
	private long key;
	private String lastName;
	private String firstName;
	private String userId;

	
	public User() {
		this(-1, "", "", "");
	}
	
}
