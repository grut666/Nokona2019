package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.nokona.data.NokonaDatabaseJob;
import com.nokona.data.NokonaDatabaseOperation;
import com.nokona.data.NokonaDatabaseTicket;
import com.nokona.enums.OperationStatus;
import com.nokona.enums.TicketStatus;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.InvalidInsertException;
import com.nokona.exceptions.InvalidQuantityException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.TicketFormatter;
import com.nokona.formatter.TicketHeaderFormatter;
import com.nokona.model.JobDetail;
import com.nokona.model.Operation;
import com.nokona.model.Ticket;
import com.nokona.model.TicketDetail;
import com.nokona.model.TicketHeader;
import com.nokona.utilities.DateUtilities;

public class NokonaDAOTicket extends NokonaDAO implements NokonaDatabaseTicket {
	@Inject
	NokonaDatabaseJob jobDAO;
	@Inject
	NokonaDatabaseOperation operationDAO;
	private PreparedStatement psGetTicketByKey;
	private PreparedStatement psGetTicketHeaderByKey;
	private PreparedStatement psGetTicketHeaders;

	private PreparedStatement psGetTickets;
	private PreparedStatement psGetTicketsByJob;

	private PreparedStatement psGetTicketDetails;

	private PreparedStatement psAddTicketHeader;
	private PreparedStatement psAddTicketDetail;
	private PreparedStatement psUpdateTicket;

	PreparedStatement psDelTicketByKey;
	PreparedStatement psDelTicketDetailByKey;
	PreparedStatement psMoveDeletedTicketByKey;
	PreparedStatement psMoveDeletedTicketDetailByKey;

	public NokonaDAOTicket() throws DatabaseException {
		super();

	}

	public NokonaDAOTicket(String userName, String password) throws DatabaseException {
		super(userName, password);

	}

	@Override
	public List<Ticket> getTickets() throws DatabaseException {
		List<Ticket> tickets = new ArrayList<Ticket>();
		if (psGetTickets == null) {
			try {
				psGetTickets = getConn().prepareStatement(
						"Select * from ticketheader join ticketdetail on ticketheader.key = ticketdetail.key "
								+ "order by ticketheader.key, sequenceOriginal");

			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage(), e);
			}
		}
		try {
			ResultSet rs = psGetTickets.executeQuery();
			while (rs.next()) {
				tickets.add(convertTicketFromResultSet(rs));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
		return tickets;
	}

	@Override
	public Ticket getTicketByKey(long key) throws DatabaseException {
		Ticket ticket = null;
		if (psGetTicketByKey == null) {
			try {
				psGetTicketByKey = conn.prepareStatement(
						"Select * from ticketheader join ticketdetail on ticketheader.key = ticketdetail.key  "
								+ "join operation on operation = opcode "
								+ "join job on ticketheader.jobid = job.jobid "
								+ "where ticketheader.key = ? order by sequenceOriginal");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage(), e);
			}
		}
		try {
			psGetTicketByKey.setLong(1, key);
			ResultSet rs = psGetTicketByKey.executeQuery();
			if (rs.next()) {
				ticket = convertTicketFromResultSet(rs);
			} else {
				throw new DataNotFoundException("Ticket key " + key + " is not in DB");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
		return TicketFormatter.format(ticket);

	}

	@Override
	public List<Ticket> getTicketsByJob(String job) throws DatabaseException {
		List<Ticket> tickets = new ArrayList<Ticket>();
		if (psGetTicketsByJob == null) {
			try {
				psGetTicketsByJob = getConn().prepareStatement(
						"Select * from ticketheader join ticketdetail on ticketheader.key = ticketdetail.key "
								+ "where jobID = ? order by ticketheader.key, sequence");

			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage(), e);
			}
		}

		try {
			psGetTicketsByJob.setString(1, job);
			ResultSet rs = psGetTicketsByJob.executeQuery();
			while (rs.next()) {
				tickets.add(convertTicketFromResultSet(rs));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
		return tickets;
	}

	@Override
	public Ticket addTicket(TicketHeader ticketHeader) throws DatabaseException {
		if (ticketHeader == null) {
			throw new NullInputDataException("TicketHeader cannot be null");
		}
		if (ticketHeader.getQuantity() < 1) {
			throw new InvalidQuantityException("Quantity of value " + ticketHeader.getQuantity() + " is invalid");
		}
		TicketHeader formattedTicketHeader = new TicketHeader();
		formattedTicketHeader.setJobId(ticketHeader.getJobId());
		formattedTicketHeader.setDateStatus(ticketHeader.getDateCreated());
		formattedTicketHeader.setDateCreated(ticketHeader.getDateCreated());
		formattedTicketHeader.setQuantity(ticketHeader.getQuantity());
		formattedTicketHeader.setTicketStatus(TicketStatus.NEW);
		formattedTicketHeader = TicketHeaderFormatter.format(formattedTicketHeader);
		try {
			if (psAddTicketHeader == null) {
				conn.prepareStatement(
						"Insert into TicketHeader (JobID, DateCreated, Status, StatusDate, Quantity) values (?,?,?,?,?)",
						PreparedStatement.RETURN_GENERATED_KEYS);
			}

			if (psAddTicketDetail == null) {
				conn.prepareStatement(
						"Insert into TicketDetail (TicketDetail.Key, Operation, Sequence, StatusDate, Status, "
								+ "Quantity, HourlyRateSAH, BarCodeID, LaborRate, UpdatedSequence)  "
								+ "values (?,?,?,?,?,?,?,?,?,?)");
			}

		} catch (SQLException ex) {
			throw new DatabaseException("Prepare Statements failed");
		}

		// Should not need any validation so not validating
		try {
			psAddTicketHeader.setString(1, formattedTicketHeader.getJobId());
			psAddTicketHeader.setDate(2,
					DateUtilities.convertUtilDateToSQLDate(formattedTicketHeader.getDateCreated()));
			psAddTicketHeader.setString(3, formattedTicketHeader.getTicketStatus().getTicketStatus());
			psAddTicketHeader.setDate(4, DateUtilities.convertUtilDateToSQLDate(formattedTicketHeader.getDateStatus()));
			psAddTicketHeader.setInt(5, formattedTicketHeader.getQuantity());

			int rowCount = psAddTicketHeader.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
			}
			try (ResultSet generatedKeys = psAddTicketHeader.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					formattedTicketHeader.setKey(generatedKeys.getLong(1));

				} else {
					throw new SQLException("Creating user failed, no ID obtained.");
				}
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
		List<JobDetail> jobDetails = jobDAO.getJobDetails(formattedTicketHeader.getJobId());
		List<TicketDetail> newTicketDetails = new ArrayList<TicketDetail>();
		Operation op;
		long key = formattedTicketHeader.getKey();
		try {
			for (JobDetail jobDetail : jobDetails) {
				op = operationDAO.getOperation(jobDetail.getOpCode());
				String opCode = jobDetail.getOpCode();
				String desc = op.getDescription();
				OperationStatus status = OperationStatus.INCOMPLETE;
				int sequence = jobDetail.getSequence();
				int quantity = formattedTicketHeader.getQuantity();
				double sah = op.getHourlyRateSAH();
				TicketDetail td = new TicketDetail(key, opCode, desc, status, sequence, sequence, null, quantity, sah,
						0);
				newTicketDetails.add(td);
				psAddTicketDetail.setLong(1, key);
				psAddTicketDetail.setString(2, opCode);
				psAddTicketDetail.setLong(3, sequence);
				psAddTicketDetail.setDate(4, null);
				psAddTicketDetail.setString(5, status.getOperationStatus());
				psAddTicketDetail.setLong(6, quantity);
				psAddTicketDetail.setDouble(7, sah);
				psAddTicketDetail.setLong(8, 0);
				psAddTicketDetail.setLong(9, 0);
				psAddTicketDetail.setLong(10, sequence);
				psAddTicketDetail.addBatch();
			}
			psAddTicketDetail.executeBatch();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}

		return new Ticket(formattedTicketHeader, newTicketDetails);

	}

	@Override
	public Ticket updateTicket(Ticket ticket) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteTicketByKey(long key) throws DatabaseException {
		if (psDelTicketByKey == null) {
			try {
				psDelTicketByKey = conn.prepareStatement("Delete From TicketHeader where Job.Key = ?");
				psMoveDeletedTicketByKey = conn.prepareStatement("INSERT INTO Deleted_TicketHeader (Deleted_TicketHeader.key, JobId, CreatedDate, status, statusDate, quantity) " + 
						"  SELECT TicketHeader.key, JobId, CreatedDate, status, statusDate, quantity FROM TicketHeader WHERE TicketHeader.Key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psMoveDeletedTicketByKey.setLong(1, key);
			int rowCount = psMoveDeletedTicketByKey.executeUpdate();
			if (rowCount == 0) {
				throw new InvalidInsertException("TicketHeader key "+ key + " could not be inserted into delete table");
			}
			psDelTicketByKey.setLong(1, key);
			rowCount = psDelTicketByKey.executeUpdate();

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete JobHeader key " + key + " failed");
			}
			deleteTicketDetailsByKey(key);

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}

	}

	private void deleteTicketDetailsByKey(long key) throws DatabaseException {
		if (psDelTicketDetailByKey == null) {
			try {
				psDelTicketDetailByKey = conn.prepareStatement("Delete From TicketDetail where key = ?");
				psMoveDeletedTicketDetailByKey = conn.prepareStatement("INSERT INTO Deleted_TicketDetail (Deleted_TicketDetail.key, operation, sequence, statusDate, status, quantity, hourlyrateSAH, BarCodeID, LaborRate, UpdatedSequence) " + 
						"  SELECT TicketDetail.key, operation, sequence, statusDate, status, quantity, hourlyrateSAH, BarCodeID, LaborRate, UpdatedSequence FROM TicketDetail WHERE TicketDetail.Key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psMoveDeletedTicketDetailByKey.setLong(1, key);
			int rowCount = psMoveDeletedTicketDetailByKey.executeUpdate();
			if (rowCount == 0) {
				throw new InvalidInsertException("JobDetail Key "+ key + " could not be inserted into delete table");
			}
			psDelTicketDetailByKey.setLong(1, key);
			rowCount = psDelTicketDetailByKey.executeUpdate();

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete TicketDetail Key " + key + " failed");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
		
	}

	@Override
	public List<TicketHeader> getTicketHeaders() throws DatabaseException {
		List<TicketHeader> ticketHeaders = new ArrayList<TicketHeader>();
		if (psGetTicketHeaders == null) {
			try {
				psGetTicketHeaders = conn
						.prepareStatement("Select * from ticketheader " + "order by dateCreated desc, jobID, Status");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage(), e);
			}
		}
		try {
			ResultSet rs = psGetTicketHeaders.executeQuery();
			while (rs.next()) {
				ticketHeaders.add(convertTicketHeaderFromResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
		return ticketHeaders;
	}

	@Override
	public TicketHeader getTicketHeaderByKey(long headerKey) throws DatabaseException {
		TicketHeader ticketHeader = null;
		if (psGetTicketHeaderByKey == null) {
			try {
				psGetTicketHeaderByKey = conn.prepareStatement("Select * from ticketheader join job on "
						+ "ticketheader.jobid = job.jobid where ticketheader.key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage(), e);
			}
		}
		try {
			psGetTicketHeaderByKey.setLong(1, headerKey);
			ResultSet rs = psGetTicketHeaderByKey.executeQuery();
			if (rs.next()) {
				ticketHeader = convertTicketHeaderFromResultSet(rs);
			} else {
				throw new DataNotFoundException("Ticket key " + headerKey + " is not in DB");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
		return TicketHeaderFormatter.format(ticketHeader);
	}

	@Override
	public List<TicketDetail> getTicketDetailsByKey(long headerKey) throws DatabaseException {
		List<TicketDetail> ticketDetails = new ArrayList<TicketDetail>();
		if (psGetTicketDetails == null) {
			try {
				psGetTicketDetails = conn
						.prepareStatement("Select * from ticketdetail join operation on operation = opcode "
								+ "where ticketdetail.key = ? order by sequenceOriginal");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage(), e);
			}
		}
		try {
			psGetTicketDetails.setLong(1, headerKey);
			ResultSet rs = psGetTicketDetails.executeQuery();
			while (rs.next()) {
				ticketDetails.add(convertTicketDetailFromResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
		return ticketDetails;
	}

	private Ticket convertTicketFromResultSet(ResultSet rs) throws SQLException {

		TicketHeader th = convertTicketHeaderFromResultSet(rs);
		List<TicketDetail> details = new ArrayList<TicketDetail>();
		rs.first(); // reset rs back to first record
		while (rs.next()) {
			details.add(convertTicketDetailFromResultSet(rs));
		}
		return TicketFormatter.format(new Ticket(th, details)); // TODO Need to pass values in for Ticket - maybe already done
	}
	private TicketHeader convertTicketHeaderFromResultSet(ResultSet rs) throws SQLException {

		int key = rs.getInt("Key");
		String jobId = rs.getString("JobID"); //
		String description = rs.getString("Job.Description");
		Date dateCreated = DateUtilities.convertSQLDateToUtilDate(rs.getDate("DateCreated"));
		Date dateStatus = DateUtilities.convertSQLDateToUtilDate(rs.getDate("StatusDate"));
		String ticketStatusString = rs.getString("Status");
		TicketStatus ticketStatus = TicketStatus.NEW;
		if ("C".equals(ticketStatusString) || "P".equals(ticketStatusString)) {
			ticketStatus = TicketStatus.valueOf(ticketStatusString);
		}
		int quantity = rs.getInt("Quantity");
		return TicketHeaderFormatter
				.format(new TicketHeader(key, jobId, description, dateCreated, ticketStatus, dateStatus, quantity));
	}

	private TicketDetail convertTicketDetailFromResultSet(ResultSet rs) throws SQLException {
		long key = rs.getInt("Key");
		String operation = rs.getString("operation");
		int sequenceOriginal = rs.getInt("SequenceOriginal");
		int sequenceUpdated = rs.getInt("UpdatedSequence");
		Date statusDate = DateUtilities.convertSQLDateToUtilDate(rs.getDate("StatusDate"));
		String operationStatusString = rs.getString("Status");
		String operationDescription = rs.getString("Operation.Description");
		OperationStatus operationStatus = OperationStatus.INCOMPLETE;
		if ("C".equals(operationStatusString)) {
			operationStatus = OperationStatus.valueOf(operationStatusString);
		}
		int quantity = rs.getInt("Quantity");

		int barCodeID = rs.getInt("BarCodeID");
		double hourlyRateSAH = rs.getDouble("HourlyRateSAH");
		TicketDetail td = new TicketDetail(key, operation, operationDescription, operationStatus, sequenceOriginal,
				sequenceUpdated, statusDate, quantity, hourlyRateSAH, barCodeID);

		return td;
	}

}
