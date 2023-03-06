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

	public NokonaDAOOperation(String userName, String password) throws DatabaseException {
		super(userName, password);

	}

	@Override
	public Operation getOperationByKey(long key) throws DatabaseException {
		Operation operation = null;

		try (PreparedStatement psGetOperationByKey = conn
				.prepareStatement("Select * from Operation where Operation.Key = ?")) {
			psGetOperationByKey.setLong(1, key);
			try (ResultSet rs = psGetOperationByKey.executeQuery()) {

				if (rs.next()) {
					operation = convertOperationFromResultSet(rs);
				} else {
					throw new DataNotFoundException("Operation key " + key + " is not in DB");
				}
				return operation;
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}

	}

	@Override
	public List<Operation> getOperationsByJobId(String jobId) throws DatabaseException {
		List<Operation> operations = new ArrayList<Operation>();
		try (PreparedStatement psGetOperations = conn.prepareStatement("Select * from Operation op "
				+ "join jobDetail jd on op.OpCode = jd.OpCode where jd.jobId = ? order by jd.sequence");) {
			psGetOperations.setString(1, jobId);
			try (ResultSet rs = psGetOperations.executeQuery();) {
				while (rs.next()) {
					operations.add(convertOperationFromResultSet(rs));
				}
			}
			return operations;
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	@Override
	public Operation getOperation(String opCode) throws DatabaseException {
		if (opCode == null) {
			throw new NullInputDataException("opCode cannot be null");
		}
		Operation operation = null;
		try (PreparedStatement psGetOperationByOpCode = conn
				.prepareStatement("Select * from Operation where Operation.OpCode = ?")) {
			psGetOperationByOpCode.setString(1, opCode);
			try (ResultSet rs = psGetOperationByOpCode.executeQuery()) {

				if (rs.next()) {
					operation = convertOperationFromResultSet(rs);
				} else {
					throw new DataNotFoundException("Operation OPCode " + opCode + " is not in DB");
				}
				return operation;
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public List<Operation> getOperations() throws DatabaseException {
		List<Operation> operations = new ArrayList<Operation>();
		try (PreparedStatement psGetOperations = conn.prepareStatement("Select * from Operation order by opCode");) {
			try (ResultSet rs = psGetOperations.executeQuery();) {
				while (rs.next()) {
					operations.add(convertOperationFromResultSet(rs));
				}
			}
			return operations;
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}

	}

	@Override
	public Operation updateOperation(Operation operationIn) throws DatabaseException {
		try (PreparedStatement psUpdateOperation = conn.prepareStatement(
				"Update Operation Set OpCode = ?, Description = ?, HourlyRateSAH = ?, LevelCode = ?, LaborCode = ?, LastStudyYear = ? "
						+ " WHERE Operation.Key = ?")) {

			Operation formattedOperation = OperationFormatter.format(operationIn);
			String validateMessage = OperationValidator.validateUpdate(formattedOperation, conn);
			if (!"".equals(validateMessage)) {
				throw new DatabaseException(validateMessage);
			}
			psUpdateOperation.setString(1, formattedOperation.getOpCode());
			psUpdateOperation.setString(2, formattedOperation.getDescription());
			psUpdateOperation.setDouble(3, formattedOperation.getHourlyRateSAH());
			psUpdateOperation.setInt(4, formattedOperation.getLevelCode());
			psUpdateOperation.setInt(5, formattedOperation.getLaborCode());
			psUpdateOperation.setInt(6, formattedOperation.getLastStudyYear());
			psUpdateOperation.setLong(7, formattedOperation.getKey());
			int rowCount = psUpdateOperation.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
			}
			// loggit("UPDATE", operationIn);
			return getOperationByKey(formattedOperation.getKey());
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	@Override
	public Operation addOperation(Operation operationIn) throws DatabaseException {
		if (operationIn == null) {
			throw new NullInputDataException("Operation cannot be null");
		}

		try (PreparedStatement psAddOperation = conn.prepareStatement(
				"Insert into Operation (OpCode, Description, HourlyRateSAH, LevelCode, LaborCode, LastStudyYear) values (?,?,?,?,?,?)",
				PreparedStatement.RETURN_GENERATED_KEYS)) {
			Operation formattedOperation = OperationFormatter.format(operationIn);
			String validateMessage = OperationValidator.validateAdd(operationIn, conn);
			if (!"".equals(validateMessage)) {
				throw new DuplicateDataException(validateMessage);
			}
			psAddOperation.setString(1, formattedOperation.getOpCode());
			psAddOperation.setString(2, formattedOperation.getDescription());
			psAddOperation.setDouble(3, formattedOperation.getHourlyRateSAH());
			psAddOperation.setInt(4, formattedOperation.getLevelCode());
			psAddOperation.setInt(5, formattedOperation.getLaborCode());
			psAddOperation.setInt(6, formattedOperation.getLastStudyYear());
			int rowCount = psAddOperation.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
			}
			Operation newOp = new Operation();
			try (ResultSet generatedKeys = psAddOperation.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					newOp.setKey(generatedKeys.getLong(1));
					// loggit("ADD", newOp);
					return getOperationByKey(generatedKeys.getLong(1));
				} else {
					throw new SQLException("Creating operation failed, no ID obtained.");
				}
			}

		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}

	}

	@Override
	public void deleteOperationByKey(long key) throws DatabaseException {

		try (PreparedStatement psDelOperationByKey = conn
				.prepareStatement("Delete From Operation where Operation.Key = ?")) {
			psDelOperationByKey.setLong(1, key);
			int rowCount = psDelOperationByKey.executeUpdate();
			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Operation Key " + key + " failed");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	@Override
	public void deleteOperation(String opCode) throws DatabaseException {
		if (opCode == null) {
			throw new NullInputDataException("opID cannot be null");
		}
		try (PreparedStatement psDelOperationByOpId = conn.prepareStatement("Delete From Operation where OpCode = ?")) {

			psDelOperationByOpId.setString(1, opCode);
			int rowCount = psDelOperationByOpId.executeUpdate();
			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Operation ID " + opCode + " failed");
			}

		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	private Operation convertOperationFromResultSet(ResultSet rs) throws SQLException {
		int key = rs.getInt("Key");
		String opCode = rs.getString("OpCode");
		String description = rs.getString("Description");
		double hourlyRateSAH = rs.getDouble("HourlyRateSAH");
		int levelCode = rs.getInt("LevelCode");
		int laborCode = rs.getInt("LaborCode");
		int lastStudyYear = rs.getInt("LastStudyYear");

		return new Operation(key, opCode, description, levelCode, laborCode, hourlyRateSAH, lastStudyYear);
	}

}
