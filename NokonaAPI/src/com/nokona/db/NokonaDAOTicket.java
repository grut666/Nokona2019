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

	public NokonaDAOTicket() throws DatabaseException {
		super();

	}

	public NokonaDAOTicket(String userName, String password) throws DatabaseException {
		super(userName, password);

	}

	@Override
	public List<Ticket> getTickets(int offset) throws DatabaseException {
		List<Ticket> tickets = new ArrayList<Ticket>();
		try (PreparedStatement psGetTickets = getConn()
				.prepareStatement("Select * from ticketheader join jobheader on ticketheader.jobid = jobheader.jobid "
						+ "join ticketdetail on ticketheader.key = ticketdetail.key "
						+ "join operation on ticketdetail.opcode = operation.opcode "
	//					+ "where ticketheader.jobid like 'A-1275%' " + // Limiting for testing, otherwise too large
						+ "order by CreatedDate desc, ticketheader.key, ticketheader.status, sequence"
						+ "limit ?, 1000 ")) {
			psGetTickets.setInt(1, offset);
			try (ResultSet rs = psGetTickets.executeQuery()) {
				while (rs.next()) {
					tickets.add(convertTicketFromResultSet(rs));
				}
				return tickets;
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}

	}
	@Override
	public List<Ticket> getTicketsByStatus(String status, int offset) throws DatabaseException {
		List<Ticket> tickets = new ArrayList<Ticket>();
		try (PreparedStatement psGetTickets = getConn()
				.prepareStatement("Select * from ticketheader join jobheader on ticketheader.jobid = jobheader.jobid "
						+ "join ticketdetail on ticketheader.key = ticketdetail.key "
						+ "join operation on ticketdetail.opcode = operation.opcode "
						+ "where TicketHeader.status = ?" + // Limiting for testing, otherwise too large
						"order by CreatedDate desc, ticketheader.key, sequence limit ?, 1000")) {
			psGetTickets.setString(1, status);
			psGetTickets.setInt(2,  offset);
			try (ResultSet rs = psGetTickets.executeQuery()) {
				while (rs.next()) {
					tickets.add(convertTicketFromResultSet(rs));
				}
				return tickets;
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}

	}

	@Override
	public Ticket getTicketByKey(long key) throws DatabaseException {
		Ticket ticket = null;
		try (PreparedStatement psGetTicketByKey = conn.prepareStatement(
				"Select * from ticketheader join ticketdetail on ticketheader.key = ticketdetail.key  "
						+ "join operation on ticketdetail.opcode = operation.opcode "
						+ "join jobheader on ticketheader.jobid = jobheader.jobid "
						+ "where ticketheader.key = ? order by sequence");) {
			psGetTicketByKey.setLong(1, key);
			try (ResultSet rs = psGetTicketByKey.executeQuery();) {
				if (rs.next()) {
					ticket = convertTicketFromResultSet(rs);
				} else {
					throw new DataNotFoundException("Ticket key " + key + " is not in DB");
				}
				return TicketFormatter.format(ticket);
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public List<Ticket> getTicketsByJob(String job) throws DatabaseException {
		List<Ticket> tickets = new ArrayList<Ticket>();
		try (PreparedStatement psGetTicketsByJob = getConn()
				.prepareStatement("Select * from ticketheader join ticketdetail on ticketheader.key = ticketdetail.key "
						+ "where jobID = ? order by ticketheader.key, sequence")) {
			psGetTicketsByJob.setString(1, job);
			try (ResultSet rs = psGetTicketsByJob.executeQuery();) {

				while (rs.next()) {
					tickets.add(convertTicketFromResultSet(rs));
				}
				return tickets;
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
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
		String jobId = ticketHeader.getJobId();
		formattedTicketHeader.setDescription(jobDAO.getJobHeader(jobId).getDescription());
		formattedTicketHeader.setJobId(jobId);
		formattedTicketHeader.setDateStatus(new Date());
		formattedTicketHeader.setDateCreated(new Date());
		formattedTicketHeader.setQuantity(ticketHeader.getQuantity());
		formattedTicketHeader.setTicketStatus(ticketHeader.getTicketStatus());
		formattedTicketHeader = TicketHeaderFormatter.format(formattedTicketHeader);
		try (PreparedStatement psAddTicketHeader = conn.prepareStatement(
				"Insert into TicketHeader (JobID, CreatedDate, Status, StatusDate, Quantity) values (?,?,?,?,?)",
				PreparedStatement.RETURN_GENERATED_KEYS);
				PreparedStatement psAddTicketDetail = conn.prepareStatement(
						"Insert into TicketDetail (TicketDetail.Key, OpCode, Sequence, StatusDate, Status, "
								+ "Quantity, HourlyRateSAH, BarCodeID, LaborRate, UpdatedSequence)  "
								+ "values (?,?,?,?,?,?,?,?,?,?)");) {
			psAddTicketHeader.setString(1, formattedTicketHeader.getJobId());
			psAddTicketHeader.setDate(2,
					DateUtilities.convertUtilDateToSQLDate(formattedTicketHeader.getDateCreated()));
			psAddTicketHeader.setString(3, formattedTicketHeader.getTicketStatus().getTicketStatus());
			System.out.println("**********Status is " + formattedTicketHeader.getTicketStatus().getTicketStatus());
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
				List<JobDetail> jobDetails = jobDAO.getJobDetails(formattedTicketHeader.getJobId());
				List<TicketDetail> newTicketDetails = new ArrayList<TicketDetail>();
				Operation op;
				long key = formattedTicketHeader.getKey();
				for (JobDetail jobDetail : jobDetails) {
					op = operationDAO.getOperation(jobDetail.getOpCode());
					String opCode = jobDetail.getOpCode();
					String desc = op.getDescription();
					OperationStatus status = OperationStatus.INCOMPLETE;
					int sequence = jobDetail.getSequence() + 1;    // The plus 1 is to keep consistent with the old system ... for now anyway.
					int quantity = formattedTicketHeader.getQuantity();
					double sah = op.getHourlyRateSAH();
					TicketDetail td = new TicketDetail(key, opCode, desc, status, sequence, sequence, null, quantity,
							sah, 0);
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
				return new Ticket(formattedTicketHeader, newTicketDetails);
			}
		} catch (SQLException ex) {
			throw new DatabaseException(ex.getMessage(), ex);
		}
	}

	// Should not need any validation so not validating

	@Override
	public Ticket updateTicket(Ticket ticket) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public TicketHeader updateTicketHeader(TicketHeader ticketHeader) throws DatabaseException {
		try (PreparedStatement psUpdateTicketHeader = conn.prepareStatement(
				"Update TicketHeader Set Status = ?, StatusDate = ?, Quantity = ? "
						+ "WHERE TicketHeader.KEY = ?")) {
			psUpdateTicketHeader.setString(1, ticketHeader.getTicketStatus().getTicketStatus());
			psUpdateTicketHeader.setDate(2, DateUtilities.convertUtilDateToSQLDate(new Date()));
			psUpdateTicketHeader.setInt(3, ticketHeader.getQuantity());
			psUpdateTicketHeader.setLong(4, ticketHeader.getKey());
			System.out.println("In updateTicketHeader.  Key is " + ticketHeader.getKey());
			int rowCount = psUpdateTicketHeader.executeUpdate();
			if (rowCount != 1) {
				throw new DatabaseException("TicketHeader Update Error.  Updated " + rowCount + " rows");
			}
			return getTicketHeaderByKey(ticketHeader.getKey());
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}
	}
	@Override
	public TicketDetail updateTicketDetail(TicketDetail ticketDetail) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteTicketByKey(long key) throws DatabaseException {
			try (PreparedStatement psDelTicketByKey = conn.prepareStatement("Delete From TicketHeader where Job.Key = ?")){
				psDelTicketByKey.setLong(1, key);
				int rowCount = psDelTicketByKey.executeUpdate();

				if (rowCount == 0) {
					throw new DataNotFoundException("Error.  Delete JobHeader key " + key + " failed");
				}
				deleteTicketDetailsByKey(key);
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}

	}

	private void deleteTicketDetailsByKey(long key) throws DatabaseException {
		try (PreparedStatement psDelTicketDetailByKey = conn
				.prepareStatement("Delete From TicketDetail where key = ?")) {
			psDelTicketDetailByKey.setLong(1, key);
			int rowCount = psDelTicketDetailByKey.executeUpdate();
			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete TicketDetail Key " + key + " failed");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}
	}

	@Override
	public List<TicketHeader> getTicketHeaders(int offset) throws DatabaseException {
		List<TicketHeader> ticketHeaders = new ArrayList<TicketHeader>();
		try (PreparedStatement psGetTicketHeaders = conn
				.prepareStatement("Select * from ticketheader join jobheader on ticketheader.jobid = jobheader.jobid "
						+ "order by CreatedDate desc, jobheader.jobID, Status limit ?, 1000")) {
			psGetTicketHeaders.setInt(1, offset);
			try (ResultSet rs = psGetTicketHeaders.executeQuery();) {

				while (rs.next()) {
					ticketHeaders.add(convertTicketHeaderFromResultSet(rs));
				}
				return ticketHeaders;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
	}
	@Override
	public List<TicketHeader> getTicketHeadersByStatus(String status, int offset) throws DatabaseException {
		List<TicketHeader> ticketHeaders = new ArrayList<TicketHeader>();
		try (PreparedStatement psGetTicketHeaders = conn
				.prepareStatement("Select * from ticketheader join jobheader on ticketheader.jobid = jobheader.jobid "
						+ " where ticketheader.status = ? order by CreatedDate desc, jobheader.jobID, Status limit ?, 1000")) {
			psGetTicketHeaders.setString(1, status);
			psGetTicketHeaders.setInt(2, offset);
			try (ResultSet rs = psGetTicketHeaders.executeQuery();) {

				while (rs.next()) {
					ticketHeaders.add(convertTicketHeaderFromResultSet(rs));
				}
				return ticketHeaders;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public TicketHeader getTicketHeaderByKey(long headerKey) throws DatabaseException {
		TicketHeader ticketHeader = null;
		try (PreparedStatement psGetTicketHeaderByKey = conn
				.prepareStatement("Select * from ticketheader join jobHeader on "
						+ "ticketheader.jobid = jobHeader.jobid where ticketheader.key = ?")) {
			psGetTicketHeaderByKey.setLong(1, headerKey);
			try (ResultSet rs = psGetTicketHeaderByKey.executeQuery();) {

				if (rs.next()) {
					ticketHeader = convertTicketHeaderFromResultSet(rs);
				} else {
					throw new DataNotFoundException("Ticket key " + headerKey + " is not in DB");
				}
				return TicketHeaderFormatter.format(ticketHeader);
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public List<TicketDetail> getTicketDetailsByKey(long headerKey) throws DatabaseException {
		List<TicketDetail> ticketDetails = new ArrayList<TicketDetail>();
		try (PreparedStatement psGetTicketDetails = conn
				.prepareStatement("Select * from ticketdetail join operation on operation.opcode = ticketdetail.opcode "
						+ "where ticketdetail.key = ? order by sequence")) {
			psGetTicketDetails.setLong(1, headerKey);
			try (ResultSet rs = psGetTicketDetails.executeQuery();) {
				while (rs.next()) {
					ticketDetails.add(convertTicketDetailFromResultSet(rs));

				}
				return ticketDetails;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}

	}

	private Ticket convertTicketFromResultSet(ResultSet rs) throws SQLException {

		TicketHeader th = convertTicketHeaderFromResultSet(rs);
		List<TicketDetail> details = new ArrayList<TicketDetail>();
		rs.first(); // reset rs back to first record
		while (rs.next()) {
			details.add(convertTicketDetailFromResultSet(rs));
		}
		return TicketFormatter.format(new Ticket(th, details)); // TODO Need to pass values in for Ticket - maybe
																// already done
	}

	private TicketHeader convertTicketHeaderFromResultSet(ResultSet rs) throws SQLException {

		int key = rs.getInt("Key");
		String jobId = rs.getString("JobID"); //
		String description = rs.getString("Description");
		Date createdDate = DateUtilities.convertSQLDateToUtilDate(rs.getDate("CreatedDate"));
		Date dateStatus = DateUtilities.convertSQLDateToUtilDate(rs.getDate("StatusDate"));
		String ticketStatusString = rs.getString("Status");
		TicketStatus ticketStatus = TicketStatus.NEW;
		if ("C".equals(ticketStatusString) || "P".equals(ticketStatusString)) {
			ticketStatus = TicketStatus.valueOf(ticketStatusString);
		}
		int quantity = rs.getInt("Quantity");
		return TicketHeaderFormatter
				.format(new TicketHeader(key, jobId, description, createdDate, ticketStatus, dateStatus, quantity));
	}

	private TicketDetail convertTicketDetailFromResultSet(ResultSet rs) throws SQLException {
		long key = rs.getInt("Key");
		String opCode = rs.getString("opCode");
		int sequence = rs.getInt("Sequence");
		int updatedSequence = rs.getInt("UpdatedSequence");
		Date statusDate = DateUtilities.convertSQLDateToUtilDate(rs.getDate("StatusDate"));
		String operationStatusString = rs.getString("Status");
		String operationDescription = rs.getString("operation.Description");
		OperationStatus operationStatus = OperationStatus.INCOMPLETE;
		if ("C".equals(operationStatusString)) {
			operationStatus = OperationStatus.valueOf(operationStatusString);
		}
		int quantity = rs.getInt("Quantity");

		int barCodeID = rs.getInt("BarCodeID");
		double hourlyRateSAH = rs.getDouble("HourlyRateSAH");
		TicketDetail td = new TicketDetail(key, opCode, operationDescription, operationStatus, sequence,
				updatedSequence, statusDate, quantity, hourlyRateSAH, barCodeID);

		return td;
	}

}
