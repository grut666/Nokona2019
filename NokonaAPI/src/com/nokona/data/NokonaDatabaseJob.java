package com.nokona.data;

import java.util.List;

import com.nokona.exceptions.DatabaseException;
import com.nokona.model.Job;
import com.nokona.model.JobDetail;
import com.nokona.model.JobHeader;

public interface NokonaDatabaseJob extends NokonaDatabase {

	List<JobHeader> getJobHeaders() throws DatabaseException;
	JobHeader getJobHeaderByKey(long key) throws DatabaseException;
	JobHeader getJobHeader(String jobId) throws DatabaseException;
	JobHeader updateJobHeader(JobHeader jobHeader) throws DatabaseException;
	JobHeader addJobHeader(JobHeader jobHeader) throws DatabaseException;
	
	List<JobDetail> getJobDetails(String jobId) throws DatabaseException;
	void updateJobDetail(JobDetail jobDetail)throws DatabaseException;
	void addJobDetail(JobDetail jobDetail) throws DatabaseException;
	
	Job getJob(String jobId) throws DatabaseException;
	Job updateJob(Job job) throws DatabaseException;
	Job addJob(Job job) throws DatabaseException;
//	void deleteJobByKey(long key) throws DatabaseException;
	void deleteJob(String jobId) throws DatabaseException;
	
	
	
	
}
