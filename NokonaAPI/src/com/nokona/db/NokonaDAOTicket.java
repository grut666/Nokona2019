package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.nokona.data.NokonaDatabaseJob;
import com.nokona.data.NokonaDatabaseLevelCode;
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
import com.nokona.model.LevelCode;
import com.nokona.model.Operation;
import com.nokona.model.Ticket;
import com.nokona.model.TicketDetail;
import com.nokona.model.TicketHeader;
import com.nokona.utilities.DateUtilities;

public class NokonaDAOTicket extends NokonaDAO implements NokonaDatabaseTicket {
	@Inject
	private NokonaDatabaseJob jobDAO;
	@Inject
	private NokonaDatabaseOperation operationDAO;
	@Inject
	private NokonaDatabaseLevelCode levelCodeDAO;

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
				.prepareStatement("Select * from ticketheader th join jobheader jh on th.jobid = jh.jobid "
						+ "join ticketdetail td on th.key = td.key " + "join operation op on td.opcode = op.OpCode "
						// + "where ticketheader.jobid like 'A-1275%' " + // Limiting for testing,
						// otherwise too large
						+ "order by th.key desc, CreatedDate desc, th.status, sequence " + "limit ?, 20000 ")) {
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
				.prepareStatement("Select * from ticketheader th join jobheader jh on th.jobid = jh.jobid "
						+ "join ticketdetail td on th.key = td.key " + "join operation op on td.opcode = op.OpCode "
						+ "where th.status = ?" + // Limiting for testing, otherwise too large
						"order by th.key desc, CreatedDate desc, sequence limit ?, 20000")) {
			psGetTickets.setString(1, status);
			psGetTickets.setInt(2, offset);
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
		try (PreparedStatement psGetTicketByKey = conn
				.prepareStatement("Select * from ticketheader th left join ticketdetail td on th.key = td.key  "
						+ "join operation op on td.opcode = op.OpCode " + "join jobheader jh on th.jobid = jh.jobid "
						+ "where th.key = ? order by sequence");) {
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
	public List<Ticket> getTicketsByJob(String job, String status) throws DatabaseException {
		List<Ticket> tickets = new ArrayList<Ticket>();
		try (PreparedStatement psGetTicketsByJob = getConn()
				.prepareStatement("Select th.key from ticketheader th " + "where jobID = ? and th.status = ?")) {
			psGetTicketsByJob.setString(1, job);
			psGetTicketsByJob.setString(2, status);
			try (ResultSet rs = psGetTicketsByJob.executeQuery();) {
				tickets = convertResultSetToTicketsArray(rs);
				// while (rs.next()) {
				// Ticket ticket = convertTicketFromResultSet(rs);
				// tickets.add(ticket);
				// System.out.println("Ticket is " + ticket);
				// }
				return tickets;
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	List<Ticket> convertResultSetToTicketsArray(ResultSet rs) throws DatabaseException {
		// private long key;
		// private String jobId;
		// private String description;
		// private Date dateCreated;
		// private TicketStatus ticketStatus;
		// private Date dateStatus;
		// private int quantity;
		// private int premiumPercent;
		List<Ticket> tickets = new ArrayList<Ticket>();
		// int lastKey = -1;
		// Ticket ticket = new Ticket(new TicketHeader(), new
		// ArrayList<TicketDetail>());
		try {
			while (rs.next()) {
				int thisKey = rs.getInt("Key");
				Ticket ticket = getTicketByKey(thisKey);
				for (int i = ticket.getTicketDetails().size() - 1; i >= 0; i--) {
					TicketDetail detail = ticket.getTicketDetails().get(i);
					if (detail.getHourlyRateSAH() == 0) {
						ticket.getTicketDetails().remove(i);
					}
				}
				tickets.add(ticket);
			}
		} catch (SQLException ex) {
			throw new DatabaseException(ex.getMessage(), ex);
		}
		// if (thisKey != lastKey) {
		// if (lastKey != -1) {
		// tickets.add(ticket);
		// }
		// ticket = new Ticket(new TicketHeader(), new ArrayList<TicketDetail>());
		// lastKey = thisKey;
		// ticket.getTicketHeader().setJobId(rs.getString("JobID"));
		// ticket.getTicketHeader().setDateCreated(DateUtilities.convertSQLDateToUtilDate(rs.getDate("CreatedDate")));
		// ticket.getTicketHeader().setTicketStatus(TicketStatus.valueOf(rs.getString("jobId")));
		// ticket.getTicketHeader().setDateStatus(DateUtilities.convertSQLDateToUtilDate(rs.getDate("StatusDate")));
		// ticket.getTicketHeader().setQuantity(rs.getInt("Quantity"));
		// ticket.getTicketHeader().setKey(thisKey);
		//
		// }
		//
		// TicketDetail detail = new TicketDetail();
		// detail.setOperationStatus(rs.getString("));
		// detail.setOperationDescription();
		// detail.setSequenceOriginal(sequenceOriginal);
		//
		//
		// ticket.getTicketDetails().add(detail);
		// // Just do the ticketdetail and add it to ticket
		//
		// }
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
		String jobId = ticketHeader.getJobId();
		formattedTicketHeader.setDescription(jobDAO.getJobHeader(jobId).getDescription());
		formattedTicketHeader.setJobId(jobId);
		formattedTicketHeader.setDateStatus(new Date());
		formattedTicketHeader.setDateCreated(new Date());
		formattedTicketHeader.setQuantity(ticketHeader.getQuantity());
		formattedTicketHeader.setTicketStatus(ticketHeader.getTicketStatus());
		formattedTicketHeader.setPremiumPercent(ticketHeader.getPremiumPercent());
		formattedTicketHeader = TicketHeaderFormatter.format(formattedTicketHeader);
		try (PreparedStatement psAddTicketHeader = conn.prepareStatement(
				"Insert into TicketHeader (JobID, Description, CreatedDate, Status, StatusDate, Quantity, PremiumPercent) values (?,?,?,?,?,?,?)",
				PreparedStatement.RETURN_GENERATED_KEYS);
				PreparedStatement psAddTicketDetail = conn.prepareStatement(
						"Insert into TicketDetail (TicketDetail.Key, OpCode, Sequence, StatusDate, Status, "
								+ "StandardQuantity, HourlyRateSAH, LevelRate, UpdatedSequence, "
								+ " OperationDescription, BarCodeID1, ActualQuantity1, BarCodeID2, ActualQuantity2 )  "
								+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
			psAddTicketHeader.setString(1, formattedTicketHeader.getJobId());
			psAddTicketHeader.setString(2, formattedTicketHeader.getDescription());
			psAddTicketHeader.setDate(3,
					DateUtilities.convertUtilDateToSQLDate(formattedTicketHeader.getDateCreated()));
			psAddTicketHeader.setString(4, formattedTicketHeader.getTicketStatus().getTicketStatus());
			System.out.println("Formatted Ticket Header is " + formattedTicketHeader);
			System.out.println("**********Status is " + formattedTicketHeader.getTicketStatus().getTicketStatus());
			psAddTicketHeader.setDate(5, DateUtilities.convertUtilDateToSQLDate(formattedTicketHeader.getDateStatus()));
			psAddTicketHeader.setInt(6, formattedTicketHeader.getQuantity());
			psAddTicketHeader.setInt(7, formattedTicketHeader.getPremiumPercent());
			System.out.println("Before Update");
			int rowCount = psAddTicketHeader.executeUpdate();
			System.out.println("After Update");
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
				jobDetails.add(new JobDetail(formattedTicketHeader.getJobId(), "ZZZ", 98)); // Will result in seq 99
				List<TicketDetail> newTicketDetails = new ArrayList<TicketDetail>();
				Operation op;
				LevelCode levelCode;
				long key = formattedTicketHeader.getKey();
				for (JobDetail jobDetail : jobDetails) {
					op = operationDAO.getOperation(jobDetail.getOpCode());
					String opCode = jobDetail.getOpCode();
					String opDesc = op.getDescription();
					OperationStatus status = OperationStatus.I;
					int sequence = jobDetail.getSequence() + 1; // The plus 1 is to keep consistent with the old system
																// ... for now anyway.
					int quantity = formattedTicketHeader.getQuantity();
					double sah = op.getHourlyRateSAH();
					if (ticketHeader.getJobId().contains("-RH")) {
						sah *= 1.1;
					}
					if (ticketHeader.getPremiumPercent() > 0) {
						sah *= 1 + (ticketHeader.getPremiumPercent() / 100.0);
					} // Will not do this on update so we don't get a compounding increase in premium
					String opDescription = op.getDescription();
					levelCode = levelCodeDAO.getLevelCode(op.getLevelCode());
					// String levelDescription = levelCode.getDescription();

					TicketDetail td = new TicketDetail(key, opCode, opDesc, status, sequence, sequence, null, quantity,
							sah, levelCode.getLevelCode(), levelCode.getRate(), 0, 0, 0, 0);

					newTicketDetails.add(td);
					psAddTicketDetail.setLong(1, key);
					psAddTicketDetail.setString(2, opCode);
					psAddTicketDetail.setLong(3, sequence);
					psAddTicketDetail.setDate(4, null);
					psAddTicketDetail.setString(5, status.getOperationStatus());
					psAddTicketDetail.setLong(6, quantity);
					psAddTicketDetail.setDouble(7, sah);
					psAddTicketDetail.setLong(8, 0);
					psAddTicketDetail.setLong(9, sequence);
					psAddTicketDetail.setString(10, opDescription);
					psAddTicketDetail.setLong(11, 0);
					psAddTicketDetail.setLong(12, 0);
					psAddTicketDetail.setLong(13, 0);
					psAddTicketDetail.setLong(14, 0);
					psAddTicketDetail.addBatch();
					System.out.println("Ticket Detail is " + td);
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
		System.err.println("TicketHeader is " + ticketHeader);
		try (PreparedStatement psUpdateTicketHeader = conn.prepareStatement(
				"Update TicketHeader Set Status = ?, StatusDate = ?, Quantity = ?, PremiumPercent = ? "
						+ "WHERE ticketheader.Key = ?")) {
			psUpdateTicketHeader.setString(1, ticketHeader.getTicketStatus().getTicketStatus());
			psUpdateTicketHeader.setDate(2, DateUtilities.convertUtilDateToSQLDate(new Date()));
			psUpdateTicketHeader.setInt(3, ticketHeader.getQuantity());
			psUpdateTicketHeader.setInt(4, ticketHeader.getPremiumPercent());
			psUpdateTicketHeader.setLong(5, ticketHeader.getKey());
			System.out.println("In updateTicketHeader.  Key is " + ticketHeader.getKey());
			int rowCount = psUpdateTicketHeader.executeUpdate();
			if (rowCount != 1) {
				System.out.println("Throwing exception in UpdateHeader");
				throw new DatabaseException("TicketHeader Update Error.  Updated " + rowCount + " rows");
			}
			System.out.println("After updateTicketHeader executeUpdate().  Key is " + ticketHeader.getKey());

			return getTicketHeaderByKey(ticketHeader.getKey());
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}
	}

	@Override
	public TicketDetail updateTicketDetail(TicketDetail ticketDetail) throws DatabaseException {

		try (PreparedStatement psUpdateTicketDetail = conn
				.prepareStatement("Update TicketDetail Set Status = ?, StatusDate = ?, StandardQuantity = ?, "
						+ "HourlyRateSAH = ?, LevelRate = ?, "
						+ "UpdatedSequence = ?, BarCodeID1 = ?, ActualQuantity1 = ?, BarCodeID2 = ?, ActualQuantity2 = ?, OperationDescription = ?, "
						+ "LevelCode = ? " + "WHERE ticketDetail.Key = ? and sequence = ?")) {
			psUpdateTicketDetail.setString(1, ticketDetail.getOperationStatus().getOperationStatus());
			psUpdateTicketDetail.setDate(2, DateUtilities.convertUtilDateToSQLDate(ticketDetail.getStatusDate()));
			psUpdateTicketDetail.setInt(3, ticketDetail.getStandardQuantity());
			psUpdateTicketDetail.setDouble(4, ticketDetail.getHourlyRateSAH());

			psUpdateTicketDetail.setDouble(5, ticketDetail.getLevelRate());
			psUpdateTicketDetail.setInt(6, ticketDetail.getSequenceUpdated());
			psUpdateTicketDetail.setInt(7, ticketDetail.getEmployeeBarCodeID1());
			psUpdateTicketDetail.setInt(8, ticketDetail.getActualQuantity1());
			psUpdateTicketDetail.setInt(9, ticketDetail.getEmployeeBarCodeID2());
			psUpdateTicketDetail.setInt(10, ticketDetail.getActualQuantity2());
			psUpdateTicketDetail.setString(11, ticketDetail.getOperationDescription());

			psUpdateTicketDetail.setInt(12, ticketDetail.getLevelCode());
			psUpdateTicketDetail.setLong(13, ticketDetail.getKey());
			psUpdateTicketDetail.setInt(14, ticketDetail.getSequenceOriginal());

			int rowCount = psUpdateTicketDetail.executeUpdate();
			if (rowCount != 1) {
				System.out.println("Throwing exception in UpdateDetail");
				throw new DatabaseException("TicketDetail Update Error.  Updated " + rowCount + " rows");
			}
			System.out.println("After updateTicketHeader executeUpdate().  Key is " + ticketDetail.getKey());

			return getTicketDetailByDetailKey(Long
					.parseLong(ticketDetail.getKey() + "" + String.format("%02d", ticketDetail.getSequenceOriginal())));
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}
	}

	@Override
	public void deleteTicketByKey(long key) throws DatabaseException {
		try (PreparedStatement psDelTicketByKey = conn
				.prepareStatement("Delete From TicketHeader where TicketHeader.Key = ?")) {
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
				.prepareStatement("Delete From TicketDetail where TicketDetail.key = ?")) {
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
				.prepareStatement("Select * from ticketheader th join jobheader jh on th.jobid = jh.jobid "
						+ "order by th.key desc, CreatedDate desc, jh.jobID, Status limit ?, 20000")) {
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
				.prepareStatement("Select * from ticketheader th join jobheader jh on th.jobid = jh.jobid "
						+ " where th.status = ? order by th.key, CreatedDate desc, jh.jobID, Status limit ?, 20000")) {
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
		System.out.println("Entering getTicketHeaderByKey");

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
				System.out.println("Ticket Header is " + ticketHeader);
				return TicketHeaderFormatter.format(ticketHeader);
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public List<TicketDetail> getTicketDetailsByHeaderKey(long headerKey) throws DatabaseException {
		List<TicketDetail> ticketDetails = new ArrayList<TicketDetail>();
		try (PreparedStatement psGetTicketDetails = conn
				.prepareStatement("Select * from ticketdetail td join operation op on op.OpCode = td.opcode "
						+ "join levelcode lc on op.levelcode = lc.LevelCode " + "where td.key = ? order by sequence")) {
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

	@Override
	public TicketDetail getTicketDetailByDetailKey(long detailKey) throws DatabaseException {
		try (PreparedStatement psGetTicketDetails = conn
				.prepareStatement("Select * from ticketdetail td  " + "where td.key = ? and td.sequence = ?")) {
			String stringKey = "" + detailKey;
			if (stringKey.length() < 3) {
				throw new DatabaseException("Invalid detail key.  Size is less than 3");
			}
			int header = Integer.parseInt(stringKey.substring(0, stringKey.length() - 2));
			int detail = Integer.parseInt(stringKey.substring(stringKey.length() - 2));
			System.out.println("Header is " + header + "  Detail is " + detail);
			psGetTicketDetails.setLong(1, header);
			psGetTicketDetails.setInt(2, detail);
			try (ResultSet rs = psGetTicketDetails.executeQuery();) {
				if (rs.next()) {
					return convertTicketDetailFromResultSet(rs);
				}
				throw new DataNotFoundException("No record found for header " + header + " and sequence " + detail);
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		} catch (NumberFormatException nfe) {
			throw new DatabaseException(nfe.getMessage(), nfe);
		}

	}

	private Ticket convertTicketFromResultSet(ResultSet rs) throws SQLException {

		TicketHeader th = convertTicketHeaderFromResultSet(rs);
		List<TicketDetail> details = new ArrayList<TicketDetail>();
		rs.first(); // reset rs back to first record
		do {
			details.add(convertTicketDetailFromResultSet(rs));
		} while (rs.next());
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
		int premiumPercent = rs.getInt("PremiumPercent");
		return TicketHeaderFormatter.format(new TicketHeader(key, jobId, description, createdDate, ticketStatus,
				dateStatus, quantity, premiumPercent));
	}

	private TicketDetail convertTicketDetailFromResultSet(ResultSet rs) throws SQLException {
		long key = rs.getInt("Key");
		String opCode = rs.getString("opCode");
		int sequence = rs.getInt("Sequence");
		int updatedSequence = rs.getInt("UpdatedSequence");
		Date statusDate = DateUtilities.convertSQLDateToUtilDate(rs.getDate("StatusDate"));
		String operationStatusString = rs.getString("td.Status");
		// System.out.println("Op Status is " +operationStatusString);
		String operationDescription = rs.getString("operationDescription");
		int levelCode = rs.getInt("LevelCode");
		double levelRate = rs.getDouble("LevelRate");
		OperationStatus operationStatus = OperationStatus.I;
		if ("C".equals(operationStatusString)) {
			operationStatus = OperationStatus.valueOf(operationStatusString);
		}
		int standardQuantity = rs.getInt("StandardQuantity");
		int barCodeID1 = rs.getInt("BarCodeID1");
		int actualQuantity1 = rs.getInt("ActualQuantity1");
		int barCodeID2 = rs.getInt("BarCodeID2");
		int actualQuantity2 = rs.getInt("ActualQuantity2");
		double hourlyRateSAH = rs.getDouble("HourlyRateSAH");
		TicketDetail td = new TicketDetail(key, opCode, operationDescription, operationStatus, sequence,
				updatedSequence, statusDate, standardQuantity, hourlyRateSAH, levelCode, levelRate, barCodeID1,
				actualQuantity1, barCodeID2, actualQuantity2);

		return td;
	}

}
