package com.nokona.model;

import com.nokona.enums.JobType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@AllArgsConstructor
@NoArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class JobHeader {
	private long key;
	private String jobId;
	private String description;
	private int standardQuantity;
	private JobType jobType;

}
