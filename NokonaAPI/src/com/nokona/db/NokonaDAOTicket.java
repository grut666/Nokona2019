package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.nokona.data.NokonaDatabaseJob;
import com.nokona.data.NokonaDatabaseLaborCode;
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
import com.nokona.model.LaborCode;
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
	private NokonaDatabaseLaborCode laborCodeDAO;
	
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
						+ "join ticketdetail td on th.key = td.key "
						+ "join operation op on td.opcode = op.OpCode "
	//					+ "where ticketheader.jobid like 'A-1275%' " + // Limiting for testing, otherwise too large
						+ "order by CreatedDate desc, th.key, th.status, sequence "
						+ "limit ?, 200 ")) {
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
						+ "join ticketdetail td on th.key = td.key "
						+ "join operation op on td.opcode = op.OpCode "
						+ "where th.status = ?" + // Limiting for testing, otherwise too large
						"order by CreatedDate desc, th.key, sequence limit ?, 200")) {
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
				"Select * from ticketheader th left join ticketdetail td on th.key = td.key  "
						+ "join operation op on td.opcode = op.OpCode "
						+ "join jobheader jh on th.jobid = jh.jobid "
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
				"Insert into TicketHeader (JobID, Description, CreatedDate, Status, StatusDate, Quantity) values (?, ?,?,?,?,?)",
				PreparedStatement.RETURN_GENERATED_KEYS);
				PreparedStatement psAddTicketDetail = conn.prepareStatement(
						"Insert into TicketDetail (TicketDetail.Key, OpCode, Sequence, StatusDate, Status, "
								+ "StandardQuantity, HourlyRateSAH, BarCodeID, LaborRate, UpdatedSequence, "
								+ "ActualQuantity, OperationDescription, LaborDescription)  "
								+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
			psAddTicketHeader.setString(1, formattedTicketHeader.getJobId());
			psAddTicketHeader.setString(2, formattedTicketHeader.getDescription());
			psAddTicketHeader.setDate(3,
					DateUtilities.convertUtilDateToSQLDate(formattedTicketHeader.getDateCreated()));
			psAddTicketHeader.setString(4, formattedTicketHeader.getTicketStatus().getTicketStatus());
			System.out.println("**********Status is " + formattedTicketHeader.getTicketStatus().getTicketStatus());
			psAddTicketHeader.setDate(5, DateUtilities.convertUtilDateToSQLDate(formattedTicketHeader.getDateStatus()));
			psAddTicketHeader.setInt(6, formattedTicketHeader.getQuantity());

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
				LaborCode laborCode;
				long key = formattedTicketHeader.getKey();
				for (JobDetail jobDetail : jobDetails) {
					op = operationDAO.getOperation(jobDetail.getOpCode());
					String opCode = jobDetail.getOpCode();
					String opDesc = op.getDescription();
					double opPremium =jobDetail.getOpPremium();
					opPremium = opPremium < 1.000 ? 1.0000 : opPremium;
					OperationStatus status = OperationStatus.INCOMPLETE;
					int sequence = jobDetail.getSequence() + 1;    // The plus 1 is to keep consistent with the old system ... for now anyway.
					int quantity = formattedTicketHeader.getQuantity();
					double sah = op.getHourlyRateSAH() * opPremium;
					if (ticketHeader.getJobId().endsWith("-RH")) {
						sah *= 1.1;
					}
					String opDescription = op.getDescription();
					laborCode = laborCodeDAO.getLaborCode(op.getLaborCode());
					String laborDescription = laborCode.getDescription();
					
					TicketDetail td = new TicketDetail(key, opCode, opDesc, status, sequence, sequence, null, quantity,
							0, sah, laborCode.getLaborCode(), laborCode.getDescription(), laborCode.getRate(), 0);
					
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
					psAddTicketDetail.setLong(11, 0);
					psAddTicketDetail.setString(12, opDescription);
					psAddTicketDetail.setString(13, laborDescription);
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
						+ "WHERE ticketheader.Key = ?")) {
			psUpdateTicketHeader.setString(1, ticketHeader.getTicketStatus().getTicketStatus());
			psUpdateTicketHeader.setDate(2, DateUtilities.convertUtilDateToSQLDate(new Date()));
			psUpdateTicketHeader.setInt(3, ticketHeader.getQuantity());
			psUpdateTicketHeader.setLong(4, ticketHeader.getKey());
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
		
		try (PreparedStatement psUpdateTicketDetail = conn.prepareStatement(
				"Update TicketDetail Set Status = ?, StatusDate = ?, StandardQuantity = ?, "
				 + "HourlyRateSAH = ?, BarCodeID = ?, LaborRate = ?, "
				 + "UpdatedSequence = ?, ActualQuantity = ?, OperationDescription = ?, "
				 + "LaborDescription = ?, LaborCode = ? "
						+ "WHERE ticketDetail.Key = ? and sequence = ?")) {
			psUpdateTicketDetail.setString(1, ticketDetail.getOperationStatus().getOperationStatus());
			psUpdateTicketDetail.setDate(2, DateUtilities.convertUtilDateToSQLDate(ticketDetail.getStatusDate()));
			psUpdateTicketDetail.setInt(3, ticketDetail.getStandardQuantity());
			psUpdateTicketDetail.setDouble(4, ticketDetail.getHourlyRateSAH());
			psUpdateTicketDetail.setInt(5, ticketDetail.getEmployeeBarCodeID());
			psUpdateTicketDetail.setDouble(6, ticketDetail.getLaborRate());
			psUpdateTicketDetail.setInt(7, ticketDetail.getSequenceUpdated());
			psUpdateTicketDetail.setInt(8, ticketDetail.getActualQuantity());
			psUpdateTicketDetail.setString(9, ticketDetail.getOperationDescription());
			psUpdateTicketDetail.setString(10, ticketDetail.getLaborDescription());
			psUpdateTicketDetail.setInt(11, ticketDetail.getLaborCode());
			psUpdateTicketDetail.setLong(12, ticketDetail.getKey());
			psUpdateTicketDetail.setInt(13, ticketDetail.getSequenceOriginal());

			int rowCount = psUpdateTicketDetail.executeUpdate();
			if (rowCount != 1) {
				System.out.println("Throwing exception in UpdateDetail");
				throw new DatabaseException("TicketDetail Update Error.  Updated " + rowCount + " rows");
			}
			System.out.println("After updateTicketHeader executeUpdate().  Key is " + ticketDetail.getKey());
			return getTicketDetailByDetailKey(Long.parseLong(ticketDetail.getKey() + "" + ticketDetail.getSequenceOriginal()));
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}
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
						+ "order by CreatedDate desc, jobheader.jobID, Status limit ?, 200")) {
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
						+ " where ticketheader.status = ? order by CreatedDate desc, jobheader.jobID, Status limit ?, 200")) {
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
						+ "join laborcode lc on op.laborcode = lc.LaborCode "
						+ "where td.key = ? order by sequence")) {
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
//				.prepareStatement("Select * from ticketdetail td join operation op on op.OpCode = td.opcode "
//						+ "join laborcode lc on op.laborcode = lc.LaborCode "
//						+ "where td.key = ? and td.sequence = ?")) {
			.prepareStatement("Select * from ticketdetail td  "
										+ "where td.key = ? and td.sequence = ?")) {			
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
		
		ResultSetMetaData rsMetaData = rs.getMetaData();
	      System.out.println("List of column names in the current table: ");
	      //Retrieving the list of column names
	      int count = rsMetaData.getColumnCount();
	      for(int i = 1; i<=count; i++) {
	         System.out.println(rsMetaData.getColumnName(i));
	      }
		long key = rs.getInt("Key");
		String opCode = rs.getString("opCode");
		int sequence = rs.getInt("Sequence");
		int updatedSequence = rs.getInt("UpdatedSequence");
		Date statusDate = DateUtilities.convertSQLDateToUtilDate(rs.getDate("StatusDate"));
		String operationStatusString = rs.getString("Status");
		String operationDescription = rs.getString("operationDescription");
		int laborCode = rs.getInt("LaborCode");
		double laborRate = rs.getDouble("LaborRate");
		String laborDescription = rs.getString("LaborDescription");
		OperationStatus operationStatus = OperationStatus.INCOMPLETE;
		if ("C".equals(operationStatusString)) {
			operationStatus = OperationStatus.valueOf(operationStatusString);
		}
		int standardQuantity = rs.getInt("StandardQuantity");
		int actualQuantity = rs.getInt("ActualQuantity");


		int barCodeID = rs.getInt("BarCodeID");
		double hourlyRateSAH = rs.getDouble("HourlyRateSAH");
		TicketDetail td = new TicketDetail(key, opCode, operationDescription, operationStatus, sequence,
				updatedSequence, statusDate, standardQuantity, actualQuantity, hourlyRateSAH, laborCode, laborDescription, laborRate, barCodeID);
	
		return td;
	}

}
