package com.nokona.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class JobDetail {
	private String jobId;
	private String opCode;
	private int sequence;	
}
