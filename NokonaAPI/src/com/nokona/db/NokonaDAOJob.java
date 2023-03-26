package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;

import com.nokona.data.NokonaDatabaseJob;
import com.nokona.enums.JobType;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;

import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.JobFormatter;

import com.nokona.model.Job;
import com.nokona.model.JobDetail;
import com.nokona.model.JobHeader;

@ApplicationScoped
public class NokonaDAOJob extends NokonaDAO implements NokonaDatabaseJob {
	private boolean isAdding = false; // always delete and re-add detail

	public NokonaDAOJob() throws DatabaseException {
		super();
	}

	public NokonaDAOJob(String userName, String password) throws DatabaseException {
		super(userName, password);
	}

	@Override
	public JobHeader getJobHeaderByKey(long key) throws DataNotFoundException {
		JobHeader jobHeader = null;
		try (PreparedStatement psGetJobHeaderByKey = conn
				.prepareStatement("Select * from JobHeader where JobHeader.Key = ?")) {
			psGetJobHeaderByKey.setLong(1, key);
			try (ResultSet rs = psGetJobHeaderByKey.executeQuery()) {
				if (rs.next()) {
					jobHeader = convertJobHeaderFromResultSet(rs);
				} else {
					throw new DataNotFoundException("1. Job key " + key + " is not in DB");
				}
			}
		} catch (SQLException e) {
			throw new DataNotFoundException(e.getMessage(), e);
		}
		return jobHeader;
	}

	@Override
	public JobHeader getJobHeader(String jobId) throws DataNotFoundException {
		JobHeader jobHeader = null;
		try (PreparedStatement psGetJobHeaderByJobId = conn
				.prepareStatement("Select * from JobHeader where JobID = ?")) {
			psGetJobHeaderByJobId.setString(1, jobId);
			try (ResultSet rs = psGetJobHeaderByJobId.executeQuery();) {

				if (rs.next()) {
					jobHeader = convertJobHeaderFromResultSet(rs);
				} else {
					// Do nothing. Return null. This may be because of being called from Delete
					// throw new DataNotFoundException("3. Job: JobID " + jobId + " is not in DB");

				}
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage(), e);
		}
		return jobHeader;
	}

	@Override
	public List<JobHeader> getJobHeaders() {
		List<JobHeader> jobHeaders = new ArrayList<JobHeader>();
		try (PreparedStatement psGetJobHeaders = conn.prepareStatement("Select * from JobHeader order by jobId")) {
			try (ResultSet rs = psGetJobHeaders.executeQuery();) {

				while (rs.next()) {
					jobHeaders.add(convertJobHeaderFromResultSet(rs));
				}
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return jobHeaders;
	}

	@Override
	public JobHeader updateJobHeader(JobHeader jobHeaderIn) throws DatabaseException {
		try (PreparedStatement psUpdateJobHeader = conn
				.prepareStatement("Update JobHeader Set jobId = ?, Description = ?, StandardQuantity = ?, JobType = ? "
						+ "WHERE JobHeader.KEY = ?")) {

			JobHeader formattedJobHeader = JobFormatter.format(jobHeaderIn);
			// String validateMessage = JobValidator.validateUpdate(formattedJobHeader,
			// conn);
			// if (!"".equals(validateMessage)) {
			// throw new DatabaseException(validateMessage);
			// }
			psUpdateJobHeader.setString(1, formattedJobHeader.getJobId());
			psUpdateJobHeader.setString(2, formattedJobHeader.getDescription());
			psUpdateJobHeader.setInt(3, formattedJobHeader.getStandardQuantity());
			psUpdateJobHeader.setString(4, formattedJobHeader.getJobType().getJobType());
			psUpdateJobHeader.setLong(5, formattedJobHeader.getKey());
			int rowCount = psUpdateJobHeader.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Updated " + rowCount + " rows");
			}
			// loggitHeader("UPDATE", jobHeaderIn);
			return getJobHeaderByKey(formattedJobHeader.getKey());
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}

	}

	@Override
	public JobHeader addJobHeader(JobHeader jobHeaderIn) throws DatabaseException {
		// Dupe Data Check

		if (jobHeaderIn == null) {
			throw new NullInputDataException("Job Header cannot be null");
		}
		JobHeader formattedJobHeader = JobFormatter.format(jobHeaderIn);
		// try (PreparedStatement psAddJobHeaderDupeCheck = conn
		// .prepareStatement("Select * from JobHeader where JobID = ?")) {
		// psAddJobHeaderDupeCheck.setString(1, formattedJobHeader.getJobId());
		// try (ResultSet rs = psAddJobHeaderDupeCheck.executeQuery();) {
		//
		// if (rs.next()) {
		// throw new DuplicateDataException("Job ID is already in use");
		// }
		// }
		//
		// } catch (SQLException e) {
		// System.err.println(e.getMessage());
		// throw new DuplicateDataException(e.getMessage(), e);
		// }
		try (PreparedStatement psAddJobHeader = conn.prepareStatement(
				"Insert into JobHeader (JobId, Description, StandardQuantity, JobType) values (?,?,?,?)",
				PreparedStatement.RETURN_GENERATED_KEYS);) {
			// String validateMessage = JobValidator.validateAdd(formattedJobHeader, conn);
			// if (!"".equals(validateMessage)) {
			// throw new DatabaseException(validateMessage);
			// }
			psAddJobHeader.setString(1, formattedJobHeader.getJobId());
			psAddJobHeader.setString(2, formattedJobHeader.getDescription());
			psAddJobHeader.setInt(3, formattedJobHeader.getStandardQuantity());
			psAddJobHeader.setString(4, formattedJobHeader.getJobType().getJobType());

			int rowCount = psAddJobHeader.executeUpdate();
			System.err.println("Row count = " + rowCount);
			if (rowCount != 1) {
				throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
			}
			JobHeader newJobHeader = new JobHeader();
			try (ResultSet generatedKeys = psAddJobHeader.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					newJobHeader.setKey(generatedKeys.getInt(1));
					// loggitHeader("ADD", newJobHeader);

					System.err.println("Generated key 1 is " + generatedKeys.getLong(1));
					// return null;
					return getJobHeaderByKey(generatedKeys.getLong(1));
				} else {
					throw new SQLException("Creating JobHeader failed, no ID obtained.");
				}
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			if (e.getMessage().contains("Duplicate")) {
				throw new DuplicateDataException(e.getMessage());
			}
			throw new DatabaseException(e.getMessage());
		}

	}

	@Override
	public void deleteJob(String jobId) throws DatabaseException {
		// JobHeader jobHeader = getJobHeader(jobId);
		// long jobHeaderKey = jobHeader.getKey();
		System.err.println("Job ID is " + jobId);
		if (jobId == null) {
			throw new NullInputDataException("jobID cannot be null");
		}
		try (PreparedStatement psDelJobByJobId = conn.prepareStatement("Delete From JobHeader where JobID = ?")) {
			psDelJobByJobId.setString(1, jobId);
			int rowCount = psDelJobByJobId.executeUpdate();

			if (rowCount == 0) {
				// throw new DataNotFoundException("Error. Delete JobHeader JobID " + jobId + "
				// failed");
				// Do Nothing because this may have been called from an add or update where it
				// didn't exist
			}
			// System.err.println("Row Count is " + rowCount);
			if (isAdding) {
				deleteJobDetailsByJobId(jobId);
			}
			// multiple jobheaders using it.

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}

	}

	private void deleteJobDetailsByJobId(String jobId) throws DatabaseException {
		System.err.println("Detail JobId 1 is " + jobId);
		try (PreparedStatement psDelJobDetailByJobId = conn
				.prepareStatement("Delete From JobDetail where JobDetail.JobId = ?")) {
			jobId = StringUtils.removeEnd(jobId, "-LH");
			jobId = StringUtils.removeEnd(jobId, "-RH");
			if (!isAdding) {
				int jobHeadersCount = getJobHeadersLike(jobId); // Delete if last JOBID-?H
				if (jobHeadersCount > 0) {
					return;
				}
			}
			psDelJobDetailByJobId.setString(1, jobId);
			System.err.println("Detail JobId 2 is " + jobId);
			int rowCount = psDelJobDetailByJobId.executeUpdate();
			System.err.println("Detail Row Count is " + rowCount);
			// if (rowCount == 0) {
			// throw new DataNotFoundException("Error. JobDetail " + jobId + " failed");
			// }
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}
	}

	int getJobHeadersLike(String jobId) throws DatabaseException {
		try (PreparedStatement psJobHeaders = conn
				.prepareStatement("Select count(*) as JOBCOUNT From JobHeader where JobHeader.JobId IN (?,?,?")) {

			psJobHeaders.setString(1, jobId);
			psJobHeaders.setString(2, jobId + "-RH");
			psJobHeaders.setString(2, jobId + "-LH");
			ResultSet rs = psJobHeaders.executeQuery();
			if (rs.next()) {
				return rs.getInt("JOBCOUNT");
			} else {
				return 0;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}

	}

	@Override
	public List<JobDetail> getJobDetails(String jobId) throws DatabaseException {
		List<JobDetail> details;
		try (PreparedStatement psGetJobDetailByJobId = conn
				.prepareStatement("Select * from JobDetail where JobID = ? order by sequence");) {
			jobId = StringUtils.removeEnd(jobId, "-LH");
			jobId = StringUtils.removeEnd(jobId, "-RH");
			psGetJobDetailByJobId.setString(1, jobId);
			ResultSet rs = psGetJobDetailByJobId.executeQuery();
			details = convertJobDetailsFromResultSet(rs);
			// if (details.isEmpty()) {
			// throw new DataNotFoundException("2. Job: JobID " + jobId + " is not in DB");
			// } // This may be legit. A Job with no details.
			return details;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage());
		}
	}

	@Override
	public void updateJobDetail(JobDetail jobDetail) throws DatabaseException {
		// possibly not needed
	}

	@Override
	public void addJobDetail(JobDetail jobDetail) throws DatabaseException {
		try (PreparedStatement psAddJobDetail = conn
				.prepareStatement("INSERT INTO JobDetail " + "(JobId, OpCode, Sequence) " + " values (?,?,?)");) {
			String jobId = jobDetail.getJobId();
			jobId = StringUtils.removeEnd(jobId, "-LH");
			jobId = StringUtils.removeEnd(jobId, "-RH");
			psAddJobDetail.setString(1, jobId);
			psAddJobDetail.setString(2, jobDetail.getOpCode());
			psAddJobDetail.setInt(3, jobDetail.getSequence());
			int rowsAffected = psAddJobDetail.executeUpdate();
			if (rowsAffected != 1) {
				throw new DatabaseException("Detail row not added");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}

	}

	@Override
	public Job getJob(String jobId) throws DatabaseException {
		Job job = new Job(getJobHeader(jobId), getJobDetails(jobId));

		return job;

	}

	@Override
	public Job updateJob(Job job) throws DatabaseException {
		Job jobAdded = addJob(job);
		return jobAdded;
	}

	@Override
	public Job addJob(Job job) throws DatabaseException {
		JobHeader formattedJobHeader = JobFormatter.format(job.getHeader());
		String jobId = formattedJobHeader.getJobId();
		try {
			isAdding = true;
			deleteJob(jobId);
		} catch (DatabaseException ex) {
			// No concern as this is used for both adds and updates to get rid of the
			// existing job, if it exists
		}
		isAdding = false;
		addJobHeader(formattedJobHeader);
		int sequence = 0;
		for (JobDetail jobDetail : job.getDetails()) {
			jobDetail.setSequence(sequence);
			addJobDetail(jobDetail);
			sequence++;
		}
		return getJob(jobId);

	}

	private JobHeader convertJobHeaderFromResultSet(ResultSet rs) throws SQLException {
		int key = rs.getInt("Key");
		String jobId = rs.getString("JobID");
		String description = rs.getString("Description");

		int standardQuantity = rs.getInt("standardQuantity");
		String jobTypeString = rs.getString("JobType");
		JobType jobType = JobType.UNKNOWN;
		if (JobType.contains(jobTypeString)) {
			jobType = JobType.valueOf(jobTypeString);
		}

		return new JobHeader(key, jobId, description, standardQuantity, jobType);
	}

	private List<JobDetail> convertJobDetailsFromResultSet(ResultSet rs) throws SQLException {

		List<JobDetail> details = new ArrayList<JobDetail>();
		while (rs.next()) {

			String jobId = rs.getString("JobID");
			String opCode = rs.getString("OpCode");
			int sequence = rs.getInt("Sequence");
			details.add(new JobDetail(jobId, opCode, sequence));
		}
		return details;
	}

}
