package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.nokona.data.NokonaDatabaseOperation;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.InvalidInsertException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.OperationFormatter;
import com.nokona.model.Operation;
import com.nokona.validator.OperationValidator;

public class NokonaDAOOperation extends NokonaDAO implements NokonaDatabaseOperation {

	private PreparedStatement psGetOperationByKey;
	private PreparedStatement psGetOperationByOpCode;
	private PreparedStatement psGetOperations;
	private PreparedStatement psAddOperation;
	private PreparedStatement psUpdateOperation;

	private PreparedStatement psDelOperationByKey;
	private PreparedStatement psDelOperationByOpId; 
	
	private PreparedStatement psMoveDeletedOperationByKey;
	private PreparedStatement psMoveDeletedOperationByOpCode;
	

//	private PreparedStatement psTransferOperation;

	// private Connection accessConn;
//	private static final Logger LOGGER = Logger.getLogger("OperationLogger");
	
	public NokonaDAOOperation() throws DatabaseException {
		super();
	}
	public NokonaDAOOperation(String userName, String password) throws DatabaseException {
		super(userName, password); 

	}

	

	@Override
	public Operation getOperationByKey(long key) throws DatabaseException {
		Operation operation = null;
		if (psGetOperationByKey == null) {
			try {
				psGetOperationByKey = conn.prepareStatement("Select * from Operation where Operation.key = ?");

			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psGetOperationByKey.setLong(1, key);
			ResultSet rs = psGetOperationByKey.executeQuery();
			if (rs.next()) {
				operation = convertOperationFromResultSet(rs);
			} else {
				throw new DataNotFoundException("Operation key " + key + " is not in DB");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
		return operation;
	}

	@Override
	public Operation getOperation(String opCode) throws DatabaseException {
		if (opCode == null) {
			throw new NullInputDataException("opCode cannot be null");
		}
		Operation operation = null;
		if (psGetOperationByOpCode == null) {
			try {
				psGetOperationByOpCode = conn.prepareStatement("Select * from Operation where Operation.OpCode = ?");

			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage(), e);
			}
		}
		try {
			psGetOperationByOpCode.setString(1, opCode);
			ResultSet rs = psGetOperationByOpCode.executeQuery();
			if (rs.next()) {
				operation = convertOperationFromResultSet(rs);
			} else {
				throw new DataNotFoundException("Operation OPCode " + opCode + " is not in DB");
			}
		} catch (SQLException e) {
			throw new DataNotFoundException(e.getMessage(), e);
		}
		return operation;
	}

	@Override
	public List<Operation> getOperations() throws DatabaseException {
		List<Operation> operations = new ArrayList<Operation>();
		if (psGetOperations == null) {
			try {
				psGetOperations = conn.prepareStatement("Select * from Operation order by opCode");

			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			ResultSet rs = psGetOperations.executeQuery();
			while (rs.next()) {
				operations.add(convertOperationFromResultSet(rs));
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		return operations;
	}

	

	@Override
	public Operation updateOperation(Operation operationIn) throws DatabaseException {
		if (psUpdateOperation == null) {
			try {
				psUpdateOperation = conn.prepareStatement(
						"Update Operation Set OpCode = ?, Description = ?, HourlyRateSAH = ?, LaborCode = ?, LastStudyYear = ? " +
							"WHERE Operation.KEY = ?");

			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage());
			}
		}
		Operation formattedOperation = OperationFormatter.format(operationIn);
		String validateMessage = OperationValidator.validateUpdate(formattedOperation, conn);
		if (! "".equals(validateMessage)) {
			throw new DatabaseException(validateMessage);
		}
		try {
			psUpdateOperation.setString(1, formattedOperation.getOpCode());
			psUpdateOperation.setString(2, formattedOperation.getDescription());
			psUpdateOperation.setDouble(3, formattedOperation.getHourlyRateSAH());
			psUpdateOperation.setInt(4, formattedOperation.getLaborCode());
			psUpdateOperation.setInt(5, formattedOperation.getLastStudyYear());
			psUpdateOperation.setLong(6, formattedOperation.getKey());
			int rowCount = psUpdateOperation.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
			}
//			loggit("UPDATE", operationIn);
			return getOperationByKey(formattedOperation.getKey());
			
		} catch (SQLException e) {
			throw new DuplicateDataException(e.getMessage(), e);
		}
	}

	@Override
	public Operation addOperation(Operation operationIn) throws DatabaseException {
		if (operationIn == null) {
			throw new NullInputDataException("Operation cannot be null");
		}
		if (psAddOperation == null) {
			try {
				psAddOperation = conn.prepareStatement(
						"Insert into Operation (OpCode, Description, HourlyRateSAH, LaborCode, LastStudyYear) values (?,?,?,?,?)",
						PreparedStatement.RETURN_GENERATED_KEYS);

			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage());
			}
		}
		Operation formattedOperation = OperationFormatter.format(operationIn);
		String validateMessage = OperationValidator.validateAdd(operationIn, conn);
		if (!"".equals(validateMessage)) {
			throw new DuplicateDataException(validateMessage);
		}
		try {
			psAddOperation.setString(1, formattedOperation.getOpCode());
			psAddOperation.setString(2, formattedOperation.getDescription());
			psAddOperation.setDouble(3, formattedOperation.getHourlyRateSAH());
			psAddOperation.setInt(4, formattedOperation.getLaborCode());
			psAddOperation.setInt(5, formattedOperation.getLastStudyYear());
			int rowCount = psAddOperation.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
			}
			Operation newOp = new Operation();
			try (ResultSet generatedKeys = psAddOperation.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					newOp.setKey(generatedKeys.getLong(1));
//					loggit("ADD", newOp);
					return getOperationByKey(generatedKeys.getLong(1));
				} else {
					throw new SQLException("Creating operation failed, no ID obtained.");
				}
			}
		} catch (SQLException e) {
			throw new DuplicateDataException(e.getMessage(), e);
		}
	}

	@Override
	public void deleteOperationByKey(long key) throws DatabaseException {
		if (psDelOperationByKey == null) {
			try {
				psDelOperationByKey = conn.prepareStatement("Delete From Operation where Operation.Key = ?");
				psMoveDeletedOperationByKey = conn.prepareStatement("INSERT INTO Deleted_Operation (Deleted_Operation.Key, OpCode, Description, HourlyRateSAH, LaborCode, LastStudyYear) " + 
						"  SELECT Operation.Key, OpCode, Description, HourlyRateSAH, LaborCode, LastStudyYear FROM Operation WHERE Operation.Key = ?");

			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psMoveDeletedOperationByKey.setLong(1, key);
			int rowCount = psMoveDeletedOperationByKey.executeUpdate();
			if (rowCount == 0) {
				throw new DataNotFoundException("Operation key "+ key + " could not be inserted into delete table");
			}
			psDelOperationByKey.setLong(1, key);
			rowCount = psDelOperationByKey.executeUpdate();

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Operation Key " + key + " failed");
			}
//			loggit("DELETE_BY_KEY", new Operation(key, null, null,  0, 0, 0));

		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}	
	}

	@Override
	public void deleteOperation(String opCode) throws DatabaseException {
		if (opCode == null) {
			throw new NullInputDataException("opID cannot be null");
		}
		if (psDelOperationByOpId == null) {
			try {
				psDelOperationByOpId = conn.prepareStatement("Delete From Operation where OpCode = ?");
				psMoveDeletedOperationByOpCode = conn.prepareStatement("INSERT INTO Deleted_Operation (Deleted_Operation.Key, OpCode, Description, HourlyRateSAH, LaborCode, LastStudyYear) " + 
						"  SELECT Operation.Key, OpCode, Description, HourlyRateSAH, LaborCode, LastStudyYear FROM Operation WHERE OpCode = ?");

			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psMoveDeletedOperationByOpCode.setString(1, opCode);
			int rowCount = psMoveDeletedOperationByOpCode.executeUpdate();
			if (rowCount == 0) {
				throw new InvalidInsertException("Operation opCode "+ opCode + " could not be inserted into delete table");
			}
			psDelOperationByOpId.setString(1, opCode);
			rowCount = psDelOperationByOpId.executeUpdate(); 

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Operation ID " + opCode + " failed");
			}
//			loggit("DELETE_BY_ID", new Operation(0, opCode,  null, 0, 0, 0));

		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
	
	}
	private Operation convertOperationFromResultSet(ResultSet rs) throws SQLException {
		int key = rs.getInt("Key");
		String opCode = rs.getString("OpCode");
		String description = rs.getString("Description");
		double hourlyRateSAH = rs.getDouble("HourlyRateSAH");
		int laborCode = rs.getInt("LaborCode");
		int lastStudyYear = rs.getInt("LastStudyYear");

		return new Operation(key, opCode, description, laborCode, hourlyRateSAH, lastStudyYear);
	}
	// Remove below when finished with Beta testing

//		protected void loggit(string typeofupdate, operation operationin) throws databaseexception {
//			flatlog(typeofupdate, operationin);
//			// this is for moving updates to the access db until the application has been
//			// completely ported over
//			if (pstransferoperation == null) {
//				try {
//					pstransferoperation = conn.preparestatement(
//							"insert into transfer_operation (opcode, description, hourlyratesah, laborcode, laststudyyear, udori, transfer_operation.key) values (?,?,?,?,?,?,?)");
//
//				} catch (sqlexception e) {
//					system.err.println(e.getmessage());
//					throw new databaseexception(e.getmessage());
//				}
//			}
//			try {
//				pstransferoperation.setstring(1, operationin.getopcode());
//				pstransferoperation.setstring(2, operationin.getdescription());
//				pstransferoperation.setdouble(3, operationin.gethourlyratesah());
//				pstransferoperation.setint(4, operationin.getlaborcode());
//				pstransferoperation.setint(5, operationin.getlaststudyyear());
//				pstransferoperation.setstring(6, typeofupdate);
//				pstransferoperation.setlong(7, operationin.getkey());
//				int rowcount = pstransferoperation.executeupdate();
//				if (rowcount != 1) {
//					throw new databaseexception("error.  inserted " + rowcount + " rows");
//				}
//
//			} catch (sqlexception e) {
//				system.err.println(e.getmessage());
//				throw new databaseexception(e.getmessage(), e);
//			}
//
//		}
//
//		protected void flatlog(string typeofupdate, operation operationin) {
//			handler consolehandler = null;
//			handler filehandler = null;
//			try {
//				// creating consolehandler and filehandler
//				consolehandler = new consolehandler();
//				filehandler = new filehandler("/logs/operation.log", 0, 1, true);
//
//				// assigning handlers to logger object
//				logger.addhandler(consolehandler);
//				logger.addhandler(filehandler);
//
//				// setting levels to handlers and logger
//				consolehandler.setlevel(level.all);
//				filehandler.setlevel(level.all);
//				logger.setlevel(level.all);
//
//				logger.log(level.info, typeofupdate, gson.tojson(operationin));
//				filehandler.close();
//			} catch (ioexception exception) {
//				logger.log(level.severe, "error occur in filehandler.", exception);
//			}
//		}
}
