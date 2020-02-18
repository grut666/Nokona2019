package com.nokona.model;

import com.nokona.enums.JobType;

public class JobHeader {
	private String jobId;
	private String description;
	private int standardQuantity;
	private JobType jobType;
	private long key;

	public JobHeader() {

	}

	public JobHeader(String jobId, String description, int standardQuantity, JobType jobType, long key) {
		this.setJobId(jobId);
		this.setDescription(description);
		this.setStandardQuantity(standardQuantity);
		this.setJobType(jobType);
		this.setKey(key);

	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStandardQuantity() {
		return standardQuantity;
	}

	public void setStandardQuantity(int standardQuantity) {
		this.standardQuantity = standardQuantity;
	}

	public JobType getJobType() {
		return jobType;
	}

	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return "Job [jobId=" + jobId + ", description=" + description + ", standardQuantity=" + standardQuantity
				+ ", jobType=" + jobType + ", key=" + key + "]";
	}

}
