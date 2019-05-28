package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nokona.data.NokonaDatabaseTicket;
import com.nokona.dto.TicketDTOIn;
import com.nokona.enums.TicketStatus;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.TicketFormatter;
import com.nokona.formatter.TicketHeaderFormatter;
import com.nokona.model.Ticket;
import com.nokona.model.TicketDetail;
import com.nokona.model.TicketHeader;
import com.nokona.utilities.DateUtilities;

public class NokonaDAOTicket extends NokonaDAO implements NokonaDatabaseTicket {
	private PreparedStatement psGetTicketByKey;
	private PreparedStatement psGetTicketHeaderByKey;
	private PreparedStatement psGetTicketHeaders;
	private PreparedStatement psGetTickets;
	private PreparedStatement psGetTicketsByModel;

	private PreparedStatement psAddTicketHeader;
	private PreparedStatement psAddTicketDetail;
	private PreparedStatement psUpdateTicket;

	private PreparedStatement psGetOperationByKey;
	private PreparedStatement psGetOperationByOpCode;
	private PreparedStatement psGetOperations;

	private PreparedStatement psDelTicketByKey;

	private PreparedStatement psGetModelSegment;
	private PreparedStatement psGetSegmentOp;

	public NokonaDAOTicket() throws DatabaseException {
		super();

	}

	@Override
	public List<Ticket> getTickets() throws DatabaseException {
		List<Ticket> tickets = new ArrayList<Ticket>();
		if (psGetTickets == null) {
			try {
				psGetTickets = getConn().prepareStatement(
						"Select * from ticketheader join ticketdetail on ticketheader.key = ticketdetail.key "
								+ "order by ticketheader.key, sequence");

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
						"Select * from ticketheader join ticketdetail on ticketheader.key = ticketdetail.key where "
								+ "ticketheader.key = ? order by sequence");

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

	// @Override
	// public Employee getEmployee(String empID) throws DatabaseException {
	// if (empID == null) {
	// throw new NullInputDataException("empID cannot be null");
	// }
	// Employee emp = null;
	// if (psGetEmployeeByEmpId == null) {
	// try {
	// psGetEmployeeByEmpId = conn.prepareStatement("Select * from Employee where
	// Employee.EmpID = ?");
	//
	// } catch (SQLException e) {
	// throw new DatabaseException(e.getMessage(), e);
	// }
	// }
	// try {
	// psGetEmployeeByEmpId.setString(1, empID);
	// ResultSet rs = psGetEmployeeByEmpId.executeQuery();
	// if (rs.next()) {
	// emp = convertEmployeeFromResultSet(rs);
	// } else {
	// throw new DataNotFoundException("Employee EmpID " + empID + " is not in DB");
	// }
	// } catch (SQLException e) {
	//
	// throw new DatabaseException(e.getMessage(), e);
	// }
	// return emp;
	// }
	//

	//
	// @Override
	// public Employee updateEmployee(Employee employeeIn) throws DatabaseException
	// {
	//
	// if (psUpdateEmployee == null) {
	// try {
	// psUpdateEmployee = conn.prepareStatement(
	// "Update Employee Set LastName = ?, FirstName = ?, BarCodeID = ?, LaborCode =
	// ?, EmpID = ?, Active = ? " +
	// "WHERE Employee.KEY = ?");
	//
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DatabaseException(e.getMessage());
	// }
	// }
	// Employee formattedEmployee = EmployeeFormatter.format(employeeIn);
	// String validateMessage = EmployeeValidator.validateUpdate(formattedEmployee,
	// conn);
	// if (! "".equals(validateMessage)) {
	// throw new DatabaseException(validateMessage);
	// }
	// try {
	// psUpdateEmployee.setString(1, formattedEmployee.getLastName());
	// psUpdateEmployee.setString(2, formattedEmployee.getFirstName());
	// psUpdateEmployee.setInt(3, formattedEmployee.getBarCodeID());
	// psUpdateEmployee.setInt(4, formattedEmployee.getLaborCode());
	// psUpdateEmployee.setString(5, formattedEmployee.getEmpId());
	// psUpdateEmployee.setInt(6, formattedEmployee.isActive() ? 1 : 0);
	// psUpdateEmployee.setLong(7, formattedEmployee.getKey());
	// int rowCount = psUpdateEmployee.executeUpdate();
	//
	// if (rowCount != 1) {
	// throw new DatabaseException("Error. Inserted " + rowCount + " rows");
	// }
	// return getEmployee(formattedEmployee.getKey());
	//
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DuplicateDataException(e.getMessage(), e);
	// }
	// }
	// @Override
	// public Employee addEmployee(Employee employeeIn) throws DatabaseException {
	//
	// if (psAddEmployee == null) {
	// try {
	// psAddEmployee = conn.prepareStatement(
	// "Insert into Employee (LastName, FirstName, BarCodeID, LaborCode, EmpID,
	// Active) values (?,?,?,?,?,?)",
	// PreparedStatement.RETURN_GENERATED_KEYS);
	//
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DatabaseException(e.getMessage());
	// }
	// }
	// Employee formattedEmployee = EmployeeFormatter.format(employeeIn);
	// String validateMessage = EmployeeValidator.validateAdd(formattedEmployee,
	// conn);
	// if (!"".equals(validateMessage)) {
	// throw new DatabaseException(validateMessage);
	// }
	// try {
	// psAddEmployee.setString(1, formattedEmployee.getLastName());
	// psAddEmployee.setString(2, formattedEmployee.getFirstName());
	// psAddEmployee.setInt(3, formattedEmployee.getBarCodeID());
	// psAddEmployee.setInt(4, formattedEmployee.getLaborCode());
	// psAddEmployee.setString(5, formattedEmployee.getEmpId());
	// psAddEmployee.setInt(6, formattedEmployee.isActive() ? 1 : 0);
	// int rowCount = psAddEmployee.executeUpdate();
	//
	// if (rowCount != 1) {
	// throw new DatabaseException("Error. Inserted " + rowCount + " rows");
	// }
	// Employee newEmp = new Employee();
	// try (ResultSet generatedKeys = psAddEmployee.getGeneratedKeys()) {
	// if (generatedKeys.next()) {
	// newEmp.setKey(generatedKeys.getLong(1));
	// return getEmployee(generatedKeys.getLong(1));
	// } else {
	// throw new SQLException("Creating user failed, no ID obtained.");
	// }
	// }
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DuplicateDataException(e.getMessage(), e);
	// }
	// }
	// @Override
	// public void deleteEmployee(long key) throws DatabaseException {
	// if (psDelEmployeeByKey == null) {
	// try {
	// psDelEmployeeByKey = conn.prepareStatement("Delete From Employee where Key =
	// ?");
	//
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DatabaseException(e.getMessage());
	// }
	// }
	// try {
	// psDelEmployeeByKey.setLong(1, key);
	// int rowCount = psDelEmployeeByKey.executeUpdate();
	//
	// if (rowCount == 0) {
	// throw new DataNotFoundException("Error. Delete Employee key " + key + "
	// failed");
	// }
	//
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DatabaseException(e.getMessage(), e);
	// }
	//
	// }
	//
	// @Override
	// public void deleteEmployee(String empID) throws DatabaseException {
	// if (empID == null) {
	// throw new NullInputDataException("empID cannot be null");
	// }
	// if (psDelEmployeeByEmpId == null) {
	// try {
	// psDelEmployeeByEmpId = conn.prepareStatement("Delete From Employee where
	// EmpID = ?");
	//
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DatabaseException(e.getMessage());
	// }
	// }
	// try {
	// psDelEmployeeByEmpId.setString(1, empID);
	// int rowCount = psDelEmployeeByEmpId.executeUpdate();
	//
	// if (rowCount == 0) {
	// throw new DataNotFoundException("Error. Delete Emp ID " + empID + " failed");
	// }
	//
	// } catch (SQLException e) {
	// System.err.println(e.getMessage());
	// throw new DatabaseException(e.getMessage(), e);
	// }
	// }
	//
	//
	@Override
	public List<Ticket> getTicketsByModel(String model) throws DatabaseException {
		List<Ticket> tickets = new ArrayList<Ticket>();
		if (psGetTicketsByModel == null) {
			try {
				psGetTicketsByModel = getConn().prepareStatement(
						"Select * from ticketheader join ticketdetail on ticketheader.key = ticketdetail.key "
								+ "where model = ? order by ticketheader.key, sequence");

			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage(), e);
			}
		}

		try {
			psGetTicketsByModel.setString(1, model);
			ResultSet rs = psGetTicketsByModel.executeQuery();
			while (rs.next()) {
				tickets.add(convertTicketFromResultSet(rs));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
		return tickets;
	}

	@Override
	public Ticket addTicket(TicketDTOIn dtoIn) throws DatabaseException {
		if (dtoIn == null) {
			throw new NullInputDataException("Input Ticket DTO cannot be null");
		}
		TicketHeader formattedTicketHeader = new TicketHeader(dtoIn);
		formattedTicketHeader = TicketHeaderFormatter.format(formattedTicketHeader);
		try {
			if (psAddTicketHeader == null) {
				conn.prepareStatement(
						"Insert into TicketHeader (ModelID, DateCreated, Status, StatusDate, Quantity, IsDeleted, DeleteDate) values (?,?,?,?,?,?,?)",
						PreparedStatement.RETURN_GENERATED_KEYS);
			}
			if (psGetSegmentOp == null) {
				conn.prepareStatement("Select * from SegmentOp where segment = ? order by Sequence");
			}
			if (psAddTicketDetail == null) {
				conn.prepareStatement(
						"Insert into TicketDetail (LastName, FirstName, BarCodeID, LaborCode, EmpID, Active) values (?,?,?,?,?,?)");
			}
		} catch(SQLException ex) {
			throw new DatabaseException("Prepare Statements failed");
		}

		// Should not need any validation so not validating
		TicketHeader newTicketHeader;
		try {
			psAddTicketHeader.setString(1, formattedTicketHeader.getModel());
			psAddTicketHeader.setDate(2,
					DateUtilities.convertUtilDateToSQLDate(formattedTicketHeader.getDateCreated()));
			psAddTicketHeader.setString(3, formattedTicketHeader.getTicketStatus().getTicketStatus());
			psAddTicketHeader.setDate(4, DateUtilities.convertUtilDateToSQLDate(formattedTicketHeader.getDateStatus()));
			psAddTicketHeader.setInt(5, formattedTicketHeader.getQuantity());
			psAddTicketHeader.setString(6, formattedTicketHeader.isDeleted() ? "T" : "F");
			psAddTicketHeader.setDate(6, DateUtilities.convertUtilDateToSQLDate(formattedTicketHeader.getDeleteDate()));
			int rowCount = psAddTicketHeader.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
			}
			newTicketHeader = new TicketHeader();
			try (ResultSet generatedKeys = psAddTicketHeader.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					newTicketHeader.setKey(generatedKeys.getLong(1));

				} else {
					throw new SQLException("Creating user failed, no ID obtained.");
				}
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
		List<TicketDetail> newTicketDetails = new ArrayList<TicketDetail>();

		// Get the detail records generated.
		if (psGetModelSegment == null) {
			try {
				psGetModelSegment = conn
						.prepareStatement("Select * from ModelSegment where modelId = ? order by Sequence");
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}

			try {
				psGetModelSegment.setString(1, formattedTicketHeader.getModel());
				ResultSet rs = psGetModelSegment.executeQuery();
				while (rs.next()) {
					String segmentName = rs.getString("SegmentName");
					ResultSet rsSegment = psGetSegmentOp.executeQuery();
					int sequenceCounter = 0;
					while (rsSegment.next()) {

					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return new Ticket(newTicketHeader, newTicketDetails);
	}

	@Override
	public Ticket updateTicket(Ticket ticket) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ticket deleteTicketByKey(Ticket ticket) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TicketHeader> getTicketHeaders() throws DatabaseException {
		List<TicketHeader> ticketHeaders = new ArrayList<TicketHeader>();
		if (psGetTicketHeaders == null) {
			try {
				psGetTicketHeaders = conn
						.prepareStatement("Select * from ticketheader " + "order by model, dateCreated, Status");

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
				psGetTicketHeaderByKey = conn
						.prepareStatement("Select * from ticketheader where " + "ticketheader.key = ?");

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
		// TODO Auto-generated method stub
		return null;
	}

	private Ticket convertTicketFromResultSet(ResultSet rs) throws SQLException {
		int key = rs.getInt("Key");
		// String lName = rs.getString("LastName");
		// String fName = rs.getString("FirstName");
		// int barCodeId = rs.getInt("BarCodeID");
		// int laborCode = rs.getInt("LaborCode");
		// String empId = rs.getString("EmpID");
		// boolean active = (rs.getInt("Active") > 0) ? true : false;
		TicketHeader th = new TicketHeader();
		TicketDetail td = new TicketDetail();
		return TicketFormatter.format(new Ticket()); // TODO Need to pass values in for Ticket
	}

	private TicketHeader convertTicketHeaderFromResultSet(ResultSet rs) throws SQLException {

		int key = rs.getInt("Key");
		String model = rs.getString("Model"); //
		// java.util.Date newDate = result.getTimestamp("VALUEDATE");
		Date dateCreated = DateUtilities.convertSQLDateToUtilDate(rs.getDate("DateCreated"));
		Date dateStatus = DateUtilities.convertSQLDateToUtilDate(rs.getDate("DateOfStatus"));
		String ticketStatusString = rs.getString("Status");
		TicketStatus ticketStatus = TicketStatus.NEW;
		if ("C".equals(ticketStatusString) || "P".equals(ticketStatusString)) {
			ticketStatus = TicketStatus.valueOf(ticketStatusString);
		}

		int quantity = rs.getInt("Quantity");
		boolean isDeleted = "T".equals(rs.getString("IsDeleted")) ? true : false;
		Date dateDeleted = DateUtilities.convertSQLDateToUtilDate(rs.getDate("DeleteDate"));
		return TicketHeaderFormatter.format(
				new TicketHeader(key, model, dateCreated, ticketStatus, dateStatus, quantity, isDeleted, dateDeleted));
	}

}
