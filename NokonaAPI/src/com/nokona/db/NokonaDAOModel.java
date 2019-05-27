package com.nokona.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nokona.data.NokonaDatabaseModel;
import com.nokona.enums.ModelType;
import com.nokona.exceptions.DataNotFoundException;
import com.nokona.exceptions.DatabaseException;
import com.nokona.model.Model;
import com.nokona.utilities.DateUtilities;

public class NokonaDAOModel extends NokonaDAO implements NokonaDatabaseModel {
	public NokonaDAOModel() throws DatabaseException {
		super();

	}

	public NokonaDAOModel(String userName, String password) throws DatabaseException {
		super(userName, password);
	}

	PreparedStatement psGetModelByKey;
	PreparedStatement psGetModelByOpModelId;
	PreparedStatement psGetModels;
	PreparedStatement psAddModel;
	PreparedStatement psUpdateModel;

	PreparedStatement psDelModelByKey;
	PreparedStatement psDelModelByModelId;

	@Override
	public Model getModelByKey(long key) throws DataNotFoundException {
		// Model model = null;
		// if (psGetModelByKey == null) {
		// try {
		// psGetModelByKey = conn.prepareStatement("Select * from Model where Model.key
		// = ?");
		//
		// } catch (SQLException e) {
		// System.err.println(e.getMessage());
		// throw new DataNotFoundException(e.getMessage());
		// }
		// }
		// try {
		// psGetModelByKey.setLong(1, key);
		// ResultSet rs = psGetModelByKey.executeQuery();
		// if (rs.next()) {
		// model = convertModelFromResultSet(rs);
		// } else {
		// throw new DataNotFoundException("Model key " + key + " is not in DB");
		// }
		// } catch (SQLException e) {
		// System.err.println(e.getMessage());
		// throw new DataNotFoundException(e.getMessage(), e);
		// }
		// return model;
		return null;
	}

	@Override
	public Model getModel(String modelId) throws DataNotFoundException {
		// Operation operation = null;
		// if (psGetOperationByOpCode == null) {
		// try {
		// psGetOperationByOpCode = conn.prepareStatement("Select * from Operation where
		// Operation.OpCode = ?");
		//
		// } catch (SQLException e) {
		// System.err.println(e.getMessage());
		// throw new DataNotFoundException(e.getMessage());
		// }
		// }
		// try {
		// psGetOperationByOpCode.setString(1, opCode);
		// ResultSet rs = psGetOperationByOpCode.executeQuery();
		// if (rs.next()) {
		// operation = convertOperationFromResultSet(rs);
		// } else {
		// throw new DataNotFoundException("Operation OPCode " + opCode + " is not in
		// DB");
		// }
		// } catch (SQLException e) {
		// System.err.println(e.getMessage());
		// throw new DataNotFoundException(e.getMessage(), e);
		// }
		// return operation;
		return null;
	}

	@Override
	public List<Model> getModels() {
		List<Model> models = new ArrayList<Model>();
		if (psGetModels == null) {
			try {
				psGetModels = conn.prepareStatement("Select * from Model order by modelId");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		try {
			ResultSet rs = psGetModels.executeQuery();
			while (rs.next()) {
				models.add(convertModelFromResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return models;
	}

	@Override
	public Model updateModel(Model modelIn) throws DatabaseException {
		// if (psUpdateModel == null) {
		// try {
		// psUpdateModel = conn.prepareStatement(
		// "Update Operation Set OpCode = ?, Description = ?, HourlyRateSAH = ?,
		// LaborCode = ?, LastStudyYear = ? " +
		// "WHERE Operation.KEY = ?");
		//
		// } catch (SQLException e) {
		// System.err.println(e.getMessage());
		// throw new DatabaseException(e.getMessage());
		// }
		// }
		// Operation formattedOperation = OperationFormatter.format(operationIn);
		// String validateMessage =
		// OperationValidator.validateUpdate(formattedOperation, conn);
		// if (! "".equals(validateMessage)) {
		// throw new DatabaseException(validateMessage);
		// }
		// try {
		// psUpdateOperation.setString(1, formattedOperation.getOpCode());
		// psUpdateOperation.setString(2, formattedOperation.getDescription());
		// psUpdateOperation.setDouble(3, formattedOperation.getHourlyRateSAH());
		// psUpdateOperation.setInt(4, formattedOperation.getLaborCode());
		// psUpdateOperation.setInt(5, formattedOperation.getLastStudyYear());
		// psUpdateOperation.setLong(6, formattedOperation.getKey());
		// int rowCount = psUpdateOperation.executeUpdate();
		//
		// if (rowCount != 1) {
		// throw new DatabaseException("Error. Inserted " + rowCount + " rows");
		// }
		// return getOperationByKey(formattedOperation.getKey());
		//
		// } catch (SQLException e) {
		// System.err.println(e.getMessage());
		// throw new DuplicateDataException(e.getMessage(), e);
		// }
		return null;
	}

	@Override
	public Model addModel(Model modelIn) throws DatabaseException {
		// if (psAddOperation == null) {
		// try {
		// psAddOperation = conn.prepareStatement(
		// "Insert into Operation (OpCode, Description, HourlyRateSAH, LaborCode, Key,
		// LastStudyYear) values (?,?,?,?,?,?)",
		// PreparedStatement.RETURN_GENERATED_KEYS);
		//
		// } catch (SQLException e) {
		// System.err.println(e.getMessage());
		// throw new DatabaseException(e.getMessage());
		// }
		// }
		// Operation formattedOperation = OperationFormatter.format(operationIn);
		// String validateMessage = OperationValidator.validateAdd(operationIn, conn);
		// if (!"".equals(validateMessage)) {
		// throw new DatabaseException(validateMessage);
		// }
		// try {
		// psAddOperation.setString(1, formattedOperation.getOpCode());
		// psAddOperation.setString(2, formattedOperation.getDescription());
		// psAddOperation.setDouble(3, formattedOperation.getHourlyRateSAH());
		// psAddOperation.setInt(4, formattedOperation.getLaborCode());
		// psAddOperation.setLong(5, formattedOperation.getKey());
		// psAddOperation.setInt(6, formattedOperation.getLastStudyYear());
		// int rowCount = psAddOperation.executeUpdate();
		//
		// if (rowCount != 1) {
		// throw new DatabaseException("Error. Inserted " + rowCount + " rows");
		// }
		// Operation newOp = new Operation();
		// try (ResultSet generatedKeys = psAddOperation.getGeneratedKeys()) {
		// if (generatedKeys.next()) {
		// newOp.setKey(generatedKeys.getLong(1));
		// return getOperationByKey(generatedKeys.getLong(1));
		// } else {
		// throw new SQLException("Creating operation failed, no ID obtained.");
		// }
		// }
		// } catch (SQLException e) {
		// System.err.println(e.getMessage());
		// throw new DuplicateDataException(e.getMessage(), e);
		// }
		return null;
	}

	@Override
	public void deleteModelByKey(long key) throws DatabaseException {
		// if (psDelOperationByKey == null) {
		// try {
		// psDelOperationByKey = conn.prepareStatement("Delete From Operation where Key
		// = ?");
		//
		// } catch (SQLException e) {
		// System.err.println(e.getMessage());
		// throw new DatabaseException(e.getMessage());
		// }
		// }
		// try {
		// psDelOperationByKey.setLong(1, key);
		// int rowCount = psDelOperationByKey.executeUpdate();
		//
		// if (rowCount == 0) {
		// throw new DataNotFoundException("Error. Delete Operation Key " + key + "
		// failed");
		// }
		//
		// } catch (SQLException e) {
		// System.err.println(e.getMessage());
		// throw new DatabaseException(e.getMessage(), e);
		// }
	}

	@Override
	public void deleteModel(String modelId) throws DatabaseException {
		// if (opID == null) {
		// throw new NullInputDataException("opID cannot be null");
		// }
		// if (psDelOperationByOpId == null) {
		// try {
		// psDelOperationByOpId = conn.prepareStatement("Delete From Operation where
		// OpCode = ?");
		//
		// } catch (SQLException e) {
		// System.err.println(e.getMessage());
		// throw new DatabaseException(e.getMessage());
		// }
		// }
		// try {
		// psDelOperationByOpId.setString(1, opID);
		// int rowCount = psDelOperationByOpId.executeUpdate();
		//
		// if (rowCount == 0) {
		// throw new DataNotFoundException("Error. Delete Operation ID " + opID + "
		// failed");
		// }
		//
		// } catch (SQLException e) {
		// System.err.println(e.getMessage());
		// throw new DatabaseException(e.getMessage(), e);
		// }

	}

	private Model convertModelFromResultSet(ResultSet rs) throws SQLException {
		int key = rs.getInt("Key");
		String modelId = rs.getString("ModelID");
		String description = rs.getString("Description");
		int standardQuantity = rs.getInt("standardQuantity");
		java.sql.Date sqlDate = rs.getDate("deleteDate");
		Date deleteDate = null;
		if (sqlDate != null) {
			deleteDate = DateUtilities.convertSQLDateToUtilDate(rs.getDate("deleteDate"));
		}
		String modelTypeString = rs.getString("Type");
		ModelType modelType = ModelType.UNKNOWN;
		if ("B".equals(modelTypeString) || "S".equals(modelTypeString)) {
			modelType = ModelType.valueOf(modelTypeString);
		}
		boolean deleted = rs.getString("IsDeleted").equals("Y") ? true : false;

		return new Model(modelId, description, standardQuantity, modelType, key, deleted, deleteDate);
	}
}
