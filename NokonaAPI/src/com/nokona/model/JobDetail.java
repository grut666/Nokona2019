package com.nokona.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString(includeFieldNames=true)
@Data
public class JobDetail {
	private String jobId;
	private String opCode;
	private int sequence;
}
