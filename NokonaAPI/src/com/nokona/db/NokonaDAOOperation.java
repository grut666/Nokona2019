package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nokona.data.NokonaDatabaseOperation;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.OperationFormatter;
import com.nokona.model.Operation;
import com.nokona.validator.OperationValidator;

public class NokonaDAOOperation extends NokonaDAO implements NokonaDatabaseOperation {
	public NokonaDAOOperation() throws DatabaseException {
		super();
		
	}

	PreparedStatement psGetOperationByKey;
	PreparedStatement psGetOperationByOpCode;
	PreparedStatement psGetOperations;
	PreparedStatement psAddOperation;
	PreparedStatement psUpdateOperation;

	PreparedStatement psDelOperationByKey;
	PreparedStatement psDelOperationByOpId;

	
	@Override
	public Operation getOperationByKey(long key) throws DataNotFoundException {
		Operation operation = null;
		if (psGetOperationByKey == null) {
			try {
				psGetOperationByKey = conn.prepareStatement("Select * from Operation where Operation.key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DataNotFoundException(e.getMessage());
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
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage(), e);
		}
		return operation;
	}

	@Override
	public Operation getOperation(String opCode) throws DataNotFoundException {
		Operation operation = null;
		if (psGetOperationByOpCode == null) {
			try {
				psGetOperationByOpCode = conn.prepareStatement("Select * from Operation where Operation.OpCode = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DataNotFoundException(e.getMessage());
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
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage(), e);
		}
		return operation;
	}

	@Override
	public List<Operation> getOperations() {
		List<Operation> operations = new ArrayList<Operation>();
		if (psGetOperations == null) {
			try {
				psGetOperations = conn.prepareStatement("Select * from Operation order by opCode");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		try {
			ResultSet rs = psGetOperations.executeQuery();
			while (rs.next()) {
				operations.add(convertOperationFromResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
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
				System.err.println(e.getMessage());
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
			return getOperationByKey(formattedOperation.getKey());
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
	}

	@Override
	public Operation addOperation(Operation operationIn) throws DatabaseException {
		if (psAddOperation == null) {
			try {
				psAddOperation = conn.prepareStatement(
						"Insert into Operation (OpCode, Description, HourlyRateSAH, LaborCode, LastStudyYear) values (?,?,?,?,?)",
						PreparedStatement.RETURN_GENERATED_KEYS);

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		Operation formattedOperation = OperationFormatter.format(operationIn);
		String validateMessage = OperationValidator.validateAdd(operationIn, conn);
		if (!"".equals(validateMessage)) {
			throw new DatabaseException(validateMessage);
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
					return getOperationByKey(generatedKeys.getLong(1));
				} else {
					throw new SQLException("Creating operation failed, no ID obtained.");
				}
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
	}

	@Override
	public void deleteOperationByKey(long key) throws DatabaseException {
		if (psDelOperationByKey == null) {
			try {
				psDelOperationByKey = conn.prepareStatement("Delete From Operation where Operation.Key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psDelOperationByKey.setLong(1, key);
			int rowCount = psDelOperationByKey.executeUpdate();

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Operation Key " + key + " failed");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}	
	}

	@Override
	public void deleteOperation(String opID) throws DatabaseException {
		if (opID == null) {
			throw new NullInputDataException("opID cannot be null");
		}
		if (psDelOperationByOpId == null) {
			try {
				psDelOperationByOpId = conn.prepareStatement("Delete From Operation where OpCode = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psDelOperationByOpId.setString(1, opID);
			int rowCount = psDelOperationByOpId.executeUpdate();

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Operation ID " + opID + " failed");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
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
		boolean active = rs.getString("Active").equals("Y") ? true : false;

		return new Operation(opCode, description, hourlyRateSAH, laborCode, key, lastStudyYear, active);
	}
}
