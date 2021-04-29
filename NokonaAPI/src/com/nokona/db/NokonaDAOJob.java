package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nokona.data.NokonaDatabaseJob;
import com.nokona.enums.JobType;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.InvalidInsertException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.JobFormatter;
import com.nokona.model.Job;
import com.nokona.model.JobDetail;
import com.nokona.model.JobHeader;
import com.nokona.validator.JobValidator;

public class NokonaDAOJob extends NokonaDAO implements NokonaDatabaseJob {
	private PreparedStatement psGetJobHeaderByKey;
	private PreparedStatement psGetJobHeaderByJobId;
	private PreparedStatement psGetJobHeaders;
	private PreparedStatement psAddJobHeader;
	private PreparedStatement psAddJobHeaderDupeCheck;
	private PreparedStatement psUpdateJobHeader;

	private PreparedStatement psGetJobDetailByJobId;
	private PreparedStatement psAddJobDetail;
	private PreparedStatement psDelJobByJobId;
	private PreparedStatement psDelJobDetailByKey;
	private PreparedStatement psDelJobDetailByJobId;
	private PreparedStatement psMoveDeletedJobByJobId;
	private PreparedStatement psMoveDeletedJobDetailByJobId;

	public NokonaDAOJob() throws DatabaseException {
		super();
	}

	public NokonaDAOJob(String userName, String password) throws DatabaseException {
		super(userName, password);
	}

	@Override
	public JobHeader getJobHeaderByKey(long key) throws DataNotFoundException {
		JobHeader jobHeader = null;
		if (psGetJobHeaderByKey == null) {
			try {

				psGetJobHeaderByKey = conn.prepareStatement("Select * from JobHeader where JobHeader.Key = ?");

			} catch (SQLException e) {
				throw new DataNotFoundException(e.getMessage());
			}
		}
		try {
			psGetJobHeaderByKey.setLong(1, key);
			ResultSet rs = psGetJobHeaderByKey.executeQuery();
			if (rs.next()) {
				jobHeader = convertJobHeaderFromResultSet(rs);
			} else {
				throw new DataNotFoundException("Job key " + key + " is not in DB");
			}
		} catch (SQLException e) {
			throw new DataNotFoundException(e.getMessage(), e);
		}
		return jobHeader;
	}

	@Override
	public JobHeader getJobHeader(String jobId) throws DataNotFoundException {
		JobHeader jobHeader = null;
		if (psGetJobHeaderByJobId == null) {
			try {
				psGetJobHeaderByJobId = conn.prepareStatement("Select * from JobHeader where JobID = ?");

			} catch (SQLException e) {

				throw new DataNotFoundException(e.getMessage());
			}
		}
		try {
			System.err.println("-----------------JOB ID IS:" + jobId + ":-----------------");
			psGetJobHeaderByJobId.setString(1, jobId);
			ResultSet rs = psGetJobHeaderByJobId.executeQuery();
			if (rs.next()) {
				jobHeader = convertJobHeaderFromResultSet(rs);
			} else {
				System.err.println("-----------------JOB ID IS NOT FOUND:" + jobId + ":-----------------");

				throw new DataNotFoundException("Job: JobID " + jobId + " is not in DB");

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
		if (psGetJobHeaders == null) {
			try {
				psGetJobHeaders = conn.prepareStatement("Select * from JobHeader order by jobId");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		try {
			ResultSet rs = psGetJobHeaders.executeQuery();
			while (rs.next()) {
				jobHeaders.add(convertJobHeaderFromResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return jobHeaders;
	}

	@Override
	public JobHeader updateJobHeader(JobHeader jobHeaderIn) throws DatabaseException {

		if (psUpdateJobHeader == null) {
			try {
				psUpdateJobHeader = conn.prepareStatement(
						"Update JobHeader Set jobId = ?, Description = ?, StandardQuantity = ?, JobType = ? "
								+ "WHERE JobHeader.KEY = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		JobHeader formattedJobHeader = JobFormatter.format(jobHeaderIn);
		String validateMessage = JobValidator.validateUpdate(formattedJobHeader, conn);
		if (!"".equals(validateMessage)) {
			throw new DatabaseException(validateMessage);
		}
		try {
			psUpdateJobHeader.setString(1, formattedJobHeader.getJobId());
			psUpdateJobHeader.setString(2, formattedJobHeader.getDescription());
			psUpdateJobHeader.setInt(3, formattedJobHeader.getStandardQuantity());
			psUpdateJobHeader.setString(4, formattedJobHeader.getJobType().getJobType());
			psUpdateJobHeader.setLong(5, formattedJobHeader.getKey());
			int rowCount = psUpdateJobHeader.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Updated " + rowCount + " rows");
			}
			return getJobHeaderByKey(formattedJobHeader.getKey());

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
	}

	@Override
	public JobHeader addJobHeader(JobHeader jobHeaderIn) throws DatabaseException {
		// Dupe Data Check

		if (jobHeaderIn == null) {
			throw new NullInputDataException("Job Header cannot be null");
		}
		JobHeader formattedJobHeader = JobFormatter.format(jobHeaderIn);
		if (psAddJobHeaderDupeCheck == null) {
			try {
				psAddJobHeaderDupeCheck = conn.prepareStatement("Select * from JobHeader where JobID = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psAddJobHeaderDupeCheck.setString(1, formattedJobHeader.getJobId());
			ResultSet rs = psAddJobHeaderDupeCheck.executeQuery();
			if (rs.next()) {
				throw new DuplicateDataException("Job ID is already in use");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
		if (psAddJobHeader == null) {
			try {
				psAddJobHeader = conn.prepareStatement(
						"Insert into JobHeader (JobId, Description, StandardQuantity, JobType) values (?,?,?,?)",
						PreparedStatement.RETURN_GENERATED_KEYS);

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		String validateMessage = JobValidator.validateAdd(formattedJobHeader, conn);
		if (!"".equals(validateMessage)) {
			throw new DatabaseException(validateMessage);
		}
		try {
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
					return getJobHeaderByKey(generatedKeys.getLong(1));
				} else {
					throw new SQLException("Creating JobHeader failed, no ID obtained.");
				}
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
	}

	// @Override
	// public void deleteJobByKey(long key) throws DatabaseException {
	//
	// if (psDelJobByKey == null) {
	// try {
	// psDelJobByKey = conn.prepareStatement("Delete From JobHeader where Job.Key =
	// ?");
	// psMoveDeletedJobByKey = conn.prepareStatement("INSERT INTO Deleted_JobHeader
	// (Deleted_JobHeader.key, JobId, Description, StandardQuantity, JobType) " +
	// " SELECT JobHeader.key, JobId, Description, StandardQuantity, JobType FROM
	// JobHeader WHERE Deleted_JobHeader.Key = ?");
	//
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DatabaseException(e.getMessage());
	// }
	// }
	// try {
	// psMoveDeletedJobByKey.setLong(1, key);
	// int rowCount = psMoveDeletedJobByKey.executeUpdate();
	// if (rowCount == 0) {
	// throw new InvalidInsertException("JobHeader key "+ key + " could not be
	// inserted into delete table");
	// }
	// psDelJobByKey.setLong(1, key);
	// rowCount = psDelJobByKey.executeUpdate();
	//
	// if (rowCount == 0) {
	// throw new DataNotFoundException("Error. Delete JobHeader key " + key + "
	// failed");
	// }
	// deleteJobDetailsByKey(key);
	//
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DatabaseException(e.getMessage(), e);
	// }
	//
	// }

	@Override
	public void deleteJob(String jobId) throws DatabaseException {
		JobHeader jobHeader = getJobHeader(jobId);
		long jobHeaderKey = jobHeader.getKey();
		if (jobId == null) {
			throw new NullInputDataException("jobID cannot be null");
		}
		if (psDelJobByJobId == null) {
			try {
				psDelJobByJobId = conn.prepareStatement("Delete From JobHeader where JobID = ?");
				psMoveDeletedJobByJobId = conn.prepareStatement(
						"INSERT INTO Deleted_JobHeader (Deleted_JobHeader.key, JobId, Description, StandardQuantity, JobType) "
								+ "  SELECT JobHeader.key, JobId, Description, StandardQuantity, JobType FROM JobHeader WHERE JobId = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psMoveDeletedJobByJobId.setString(1, jobId);
			// int rowCount = psMoveDeletedJobByJobId.executeUpdate(); // put back in for
			// move to deleted table
			// if (rowCount == 0) {
			// throw new InvalidInsertException("JobHeader JobId "+ jobId + " could not be
			// inserted into delete table");
			// }
			psDelJobByJobId.setString(1, jobId);
			int rowCount = psDelJobByJobId.executeUpdate();

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete JobHeader JobID " + jobId + " failed");
			}
			deleteJobDetailsByJobId(jobId, jobHeaderKey);

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	// private void deleteJobDetailsByKey(long key) throws DatabaseException {
	//
	// if (psDelJobDetailByKey == null) {
	// try {
	// psDelJobDetailByKey = conn.prepareStatement("Delete From JobDetail where key
	// = ?");
	// psMoveDeletedJobDetailByKey = conn.prepareStatement("INSERT INTO
	// Deleted_JobDetail (JobId, OpCode, Sequence) " +
	// " SELECT JobId, OpCode, Sequence FROM JobDetail WHERE JobDetail.Key = ?");
	//
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DatabaseException(e.getMessage());
	// }
	// }
	// try {
	// psMoveDeletedJobDetailByKey.setLong(1, key);
	// int rowCount = psMoveDeletedJobDetailByKey.executeUpdate();
	// if (rowCount == 0) {
	// throw new InvalidInsertException("JobDetail Key "+ key + " could not be
	// inserted into delete table");
	// }
	// psDelJobDetailByKey.setLong(1, key);
	// rowCount = psDelJobDetailByKey.executeUpdate();
	//
	// if (rowCount == 0) {
	// throw new DataNotFoundException("Error. Delete JobDetail Key " + key + "
	// failed");
	// }
	//
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DatabaseException(e.getMessage(), e);
	// }
	// }

	private void deleteJobDetailsByJobId(String jobId, long jobHeaderKey) throws DatabaseException {

		if (psDelJobDetailByJobId == null) {
			try {
				psDelJobDetailByJobId = conn.prepareStatement("Delete From JobDetail where JobDetail.JobId = ?");
				psMoveDeletedJobDetailByJobId = conn.prepareStatement("INSERT INTO Deleted_JobDetail "
						+ "(Deleted_JobDetail.Key, JobId, OpCode, Sequence, JobHeaderKey) "
						+ "  SELECT JobDetail.Key, JobId, OpCode, Sequence, ? FROM JobDetail WHERE JobId = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psMoveDeletedJobDetailByJobId.setLong(1, jobHeaderKey);
			psMoveDeletedJobDetailByJobId.setString(2, jobId);
			// int rowCount = psMoveDeletedJobDetailByJobId.executeUpdate(); // put back in
			// for move to deleted table
			// if (rowCount == 0) {
			// throw new InvalidInsertException("JobDetail JobId "+ jobId + " could not be
			// inserted into delete table");
			// }
			psDelJobDetailByJobId.setString(1, jobId);
			int rowCount = psDelJobDetailByJobId.executeUpdate();

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  JobDetail  " + jobId + " failed");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public List<JobDetail> getJobDetails(String jobId) throws DatabaseException {
		List<JobDetail> details;
		if (psGetJobDetailByJobId == null) {
			try {
				psGetJobDetailByJobId = conn
						.prepareStatement("Select * from JobDetail where JobID = ? order by sequence");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DataNotFoundException(e.getMessage());
			}
		}
		try {
			System.err.println("-----------------JOB ID IS:" + jobId + ":-----------------");
			psGetJobDetailByJobId.setString(1, jobId);
			ResultSet rs = psGetJobDetailByJobId.executeQuery();
			details = convertJobDetailsFromResultSet(rs);
			if (details.isEmpty()) {

				throw new DataNotFoundException("Job: JobID " + jobId + " is not in DB");

			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage(), e);
		}
		return details;
	}

	@Override
	public void updateJobDetail(JobDetail jobDetail) throws DatabaseException {
		// possibly not needed
	}

	@Override
	public void addJobDetail(JobDetail jobDetail) throws DatabaseException {
		if (psAddJobDetail == null) {
			try {
				psAddJobDetail = conn
						.prepareStatement("INSERT INTO JobDetail " + "(JobId, OpCode, Sequence) " + " values (?,?,?)");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psAddJobDetail.setString(1, jobDetail.getJobId());
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
		return addJob(job);
	}

	@Override
	public Job addJob(Job job) throws DatabaseException {
		JobHeader formattedJobHeader = JobFormatter.format(job.getHeader());
		String validateMessage = JobValidator.validateAdd(formattedJobHeader, conn);
		if (!"".equals(validateMessage)) {
			throw new DatabaseException(validateMessage);
		}
		String jobId = formattedJobHeader.getJobId();
		deleteJob(jobId);
		addJobHeader(formattedJobHeader);
		System.err.println(formattedJobHeader);

		for (JobDetail jobDetail : job.getDetails()) {
			System.err.println(jobDetail);
			addJobDetail(jobDetail);
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
		if ("B".equals(jobTypeString) || "S".equals(jobTypeString)) {
			jobType = JobType.valueOf(jobTypeString);
		}

		return new JobHeader(jobId, description, standardQuantity, jobType, key);
	}

	private List<JobDetail> convertJobDetailsFromResultSet(ResultSet rs) throws SQLException {

		List<JobDetail> details = new ArrayList<JobDetail>();
		while (rs.next()) {
			System.err.println("Getting rs details");
			String jobId = rs.getString("JobID");
			String opCode = rs.getString("OpCode");
			int sequence = rs.getInt("Sequence");
			details.add(new JobDetail(jobId, opCode, sequence));
		}
		return details;

	}
}
