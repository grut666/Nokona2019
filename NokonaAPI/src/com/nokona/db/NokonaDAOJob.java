package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
// import com.nokona.validator.JobValidator;

public class NokonaDAOJob extends NokonaDAO implements NokonaDatabaseJob {



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
			System.err.println("-----------------JOB ID IS:" + jobId + ":-----------------");
			psGetJobHeaderByJobId.setString(1, jobId);
			try (ResultSet rs = psGetJobHeaderByJobId.executeQuery();) {

				if (rs.next()) {
					jobHeader = convertJobHeaderFromResultSet(rs);
				} else {
					// Do nothing.  Return null.  This may be because of being called from Delete
//					System.err.println("-----------------JOB ID IS NOT FOUND:" + jobId + ":-----------------");
//					throw new DataNotFoundException("3. Job: JobID " + jobId + " is not in DB");

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
//			String validateMessage = JobValidator.validateUpdate(formattedJobHeader, conn);
//			if (!"".equals(validateMessage)) {
//				throw new DatabaseException(validateMessage);
//			}
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
//		try (PreparedStatement psAddJobHeaderDupeCheck = conn
//				.prepareStatement("Select * from JobHeader where JobID = ?")) {
//			psAddJobHeaderDupeCheck.setString(1, formattedJobHeader.getJobId());
//			try (ResultSet rs = psAddJobHeaderDupeCheck.executeQuery();) {
//
//				if (rs.next()) {
//					throw new DuplicateDataException("Job ID is already in use");
//				}
//			}
//
//		} catch (SQLException e) {
//			System.err.println(e.getMessage());
//			throw new DuplicateDataException(e.getMessage(), e);
//		}
		try (PreparedStatement psAddJobHeader = conn.prepareStatement(
				"Insert into JobHeader (JobId, Description, StandardQuantity, JobType) values (?,?,?,?)",
				PreparedStatement.RETURN_GENERATED_KEYS);) {
//			String validateMessage = JobValidator.validateAdd(formattedJobHeader, conn);
//			if (!"".equals(validateMessage)) {
//				throw new DatabaseException(validateMessage);
//			}
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
//					return null;
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
		try (PreparedStatement psDelJobByJobId = conn.prepareStatement("Delete From JobHeader where JobID = ?")) {
			psDelJobByJobId.setString(1, jobId);
			int rowCount = psDelJobByJobId.executeUpdate();

			if (rowCount == 0) {
//				throw new DataNotFoundException("Error.  Delete JobHeader JobID " + jobId + " failed");
//				Do Nothing because this may have been called from an add or update where it didn't exist
			}
			deleteJobDetailsByJobId(jobId, jobHeaderKey); // Keep Job details for now

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
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

	// Not going to delete Job Details for now as they may be attached to more than one job (-LH, -RH, etc)
	// Going to now 4-25-22
	private void deleteJobDetailsByJobId(String jobId, long jobHeaderKey) throws DatabaseException {

		try (PreparedStatement psDelJobDetailByJobId = conn
				.prepareStatement("Delete From JobDetail where JobDetail.JobId = ?")) {
			jobId = StringUtils.removeEnd(jobId, "-LH");
			jobId = StringUtils.removeEnd(jobId, "-RH");
			psDelJobDetailByJobId.setString(1, jobId);
//			int rowCount =
					psDelJobDetailByJobId.executeUpdate();

//			if (rowCount == 0) {
//				throw new DataNotFoundException("Error.  JobDetail  " + jobId + " failed");
//			}
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
			System.err.println("-----------------JOB ID IS:" + jobId + ":-----------------");
			jobId = StringUtils.removeEnd(jobId, "-LH");
			jobId = StringUtils.removeEnd(jobId, "-RH");
			psGetJobDetailByJobId.setString(1, jobId);
			ResultSet rs = psGetJobDetailByJobId.executeQuery();
			details = convertJobDetailsFromResultSet(rs);
			if (details.isEmpty()) {
				throw new DataNotFoundException("2. Job: JobID " + jobId + " is not in DB");
			}
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
			try(PreparedStatement psAddJobDetail = conn
					.prepareStatement("INSERT INTO JobDetail " + "(JobId, OpCode, Sequence) " + " values (?,?,?)");
 ) {
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
		return addJob(job);
	}

	@Override
	public Job addJob(Job job) throws DatabaseException {
		JobHeader formattedJobHeader = JobFormatter.format(job.getHeader());
//		String validateMessage = JobValidator.validateAdd(formattedJobHeader, conn);
//		if (!"".equals(validateMessage)) {
//			throw new DatabaseException(validateMessage);
//		}
		String jobId = formattedJobHeader.getJobId();
		System.err.println("Deleting job");
		try {
			deleteJob(jobId);
		}
		catch (DatabaseException ex) {
			// No concern as this is used for both adds and updates to get rid of the existing job, if it exists
		}
		System.err.println("Finished Deleting job");
		System.err.println("Adding Header");
		addJobHeader(formattedJobHeader);
		System.err.println("Finished Adding Header");

		System.err.println("Formatted JobHeader is " + formattedJobHeader);

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

		return new JobHeader(key, jobId, description, standardQuantity, jobType);
	}

	private List<JobDetail> convertJobDetailsFromResultSet(ResultSet rs) throws SQLException {

		List<JobDetail> details = new ArrayList<JobDetail>();
		while (rs.next()) {
			System.err.println("Getting rs details");
			String jobId = rs.getString("JobID");
			String opCode = rs.getString("OpCode");
			int sequence = rs.getInt("Sequence");
			double opPremium = rs.getInt("OperationPremium");
			details.add(new JobDetail(jobId, opCode, sequence, opPremium));
		}
		return details;
	}

	// Remove below when finished with Beta testing

	// protected void loggitHeader(String TypeOfUpdate, JobHeader jobHeaderIn)
	// throws DatabaseException {
	// flatLogHeader(TypeOfUpdate, jobHeaderIn);
	// // This is for moving updates to the Access DB until the application has been
	// // completely ported over
	// if (psTransferJobHeader == null) {
	// try {
	// psTransferJobHeader = conn.prepareStatement(
	// "Insert into Transfer_JobHeader (JobId, Description, StandardQuantity,
	// JobType, UDorI, Transfer_JobHeader.Key) values (?,?,?,?,?,?)");
	//
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DatabaseException(e.getMessage());
	// }
	// }
	//
	// try {
	// psTransferJobHeader.setString(1, jobHeaderIn.getJobId());
	// psTransferJobHeader.setString(2, jobHeaderIn.getDescription());
	// psTransferJobHeader.setInt(3, jobHeaderIn.getStandardQuantity());
	// psTransferJobHeader.setString(4, jobHeaderIn.getJobType().getJobType());
	// psTransferJobHeader.setString(5, TypeOfUpdate);
	// psTransferJobHeader.setLong(6, jobHeaderIn.getKey());
	// int rowCount = psTransferJobHeader.executeUpdate();
	// if (rowCount != 1) {
	// throw new DatabaseException("Error. Inserted " + rowCount + " rows");
	// }
	//
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DatabaseException(e.getMessage(), e);
	// }
	//
	// }
	// protected void loggitDetail(String TypeOfUpdate, JobDetail jobDetailIn)
	// throws DatabaseException {
	// flatLogDetail(TypeOfUpdate, jobDetailIn);
	// // This is for moving updates to the Access DB until the application has been
	// // completely ported over
	// if (psTransferJobDetail == null) {
	// try {
	// psTransferJobDetail = conn.prepareStatement(
	// "Insert into Transfer_JobDetail (JobID, OpCode, Sequence, UDorI) values
	// (?,?,?,?)");
	//
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DatabaseException(e.getMessage());
	// }
	// }
	// try {
	// psTransferJobDetail.setString(1, jobDetailIn.getJobId());
	// psTransferJobDetail.setString(2, jobDetailIn.getOpCode());
	// psTransferJobDetail.setInt(3, jobDetailIn.getSequence());
	// psTransferJobDetail.setString(7, TypeOfUpdate);
	//
	// int rowCount = psTransferJobDetail.executeUpdate();
	// if (rowCount != 1) {
	// throw new DatabaseException("Error. Inserted " + rowCount + " rows");
	// }
	//
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DatabaseException(e.getMessage(), e);
	// }
	//
	// }
	//
	// protected void flatLogHeader(String TypeOfUpdate, JobHeader jobHeaderIn) {
	// Handler consoleHandler = null;
	// Handler fileHandler = null;
	// try {
	// // Creating consoleHandler and fileHandler
	// consoleHandler = new ConsoleHandler();
	// fileHandler = new FileHandler("/logs/jobheader.log", 0, 1, true);
	//
	// // Assigning handlers to LOGGER object
	// LOGGER.addHandler(consoleHandler);
	// LOGGER.addHandler(fileHandler);
	//
	// // Setting levels to handlers and LOGGER
	// consoleHandler.setLevel(Level.ALL);
	// fileHandler.setLevel(Level.ALL);
	// LOGGER.setLevel(Level.ALL);
	//
	// LOGGER.log(Level.INFO, TypeOfUpdate, gson.toJson(jobHeaderIn));
	// fileHandler.close();
	// } catch (IOException exception) {
	// LOGGER.log(Level.SEVERE, "Error occur in FileHandler.", exception);
	// }
	// }
	// protected void flatLogDetail(String TypeOfUpdate, JobDetail jobDetailIn) {
	// Handler consoleHandler = null;
	// Handler fileHandler = null;
	// try {
	// // Creating consoleHandler and fileHandler
	// consoleHandler = new ConsoleHandler();
	// fileHandler = new FileHandler("/logs/jobdetail.log", 0, 1, true);
	//
	// // Assigning handlers to LOGGER object
	// LOGGER.addHandler(consoleHandler);
	// LOGGER.addHandler(fileHandler);
	//
	// // Setting levels to handlers and LOGGER
	// consoleHandler.setLevel(Level.ALL);
	// fileHandler.setLevel(Level.ALL);
	// LOGGER.setLevel(Level.ALL);
	//
	// LOGGER.log(Level.INFO, TypeOfUpdate, gson.toJson(jobDetailIn));
	// fileHandler.close();
	// } catch (IOException exception) {
	// LOGGER.log(Level.SEVERE, "Error occur in FileHandler.", exception);
	// }
	// }
}
