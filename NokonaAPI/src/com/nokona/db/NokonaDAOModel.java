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
import com.nokona.exceptions.DuplicateDataException;
import com.nokona.exceptions.NullInputDataException;
import com.nokona.formatter.ModelFormatter;
import com.nokona.model.Employee;
import com.nokona.model.Model;
import com.nokona.model.ModelDetail;
import com.nokona.model.ModelHeader;
import com.nokona.utilities.DateUtilities;
import com.nokona.validator.ModelValidator;

public class NokonaDAOModel extends NokonaDAO implements NokonaDatabaseModel {
	public NokonaDAOModel() throws DatabaseException {
		super();

	}

	public NokonaDAOModel(String userName, String password) throws DatabaseException {
		super(userName, password);
	}

	PreparedStatement psGetModelHeaderByKey;
	PreparedStatement psGetModelHeaderByModelId;
	PreparedStatement psGetModelHeaders;
	PreparedStatement psAddModelHeader;
	PreparedStatement psAddModelHeaderDupeCheck;
	PreparedStatement psUpdateModelHeader;

	PreparedStatement psGetModelDetailByModelId;

	PreparedStatement psDelModelByKey;
	PreparedStatement psDelModelByModelId;

	@Override
	public ModelHeader getModelHeaderByKey(long key) throws DataNotFoundException {
		ModelHeader model = null;
		if (psGetModelHeaderByKey == null) {
			try {
				psGetModelHeaderByKey = conn.prepareStatement("Select * from Model where Model.Key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DataNotFoundException(e.getMessage());
			}
		}
		try {
			psGetModelHeaderByKey.setLong(1, key);
			ResultSet rs = psGetModelHeaderByKey.executeQuery();
			if (rs.next()) {
				model = convertModelHeaderFromResultSet(rs);
			} else {
				throw new DataNotFoundException("Model key " + key + " is not in DB");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage(), e);
		}
		return model;
	}

	@Override
	public ModelHeader getModelHeader(String modelId) throws DataNotFoundException {
		ModelHeader model = null;
		if (psGetModelHeaderByModelId == null) {
			try {
				psGetModelHeaderByModelId = conn.prepareStatement("Select * from ModelHeader where ModelID = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DataNotFoundException(e.getMessage());
			}
		}
		try {
			psGetModelHeaderByModelId.setString(1, modelId);
			ResultSet rs = psGetModelHeaderByModelId.executeQuery();
			if (rs.next()) {
				model = convertModelHeaderFromResultSet(rs);
			} else {
				throw new DataNotFoundException("Model ModelID " + modelId + " is not in DB");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage(), e);
		}
		return model;
	}

	@Override
	public List<ModelHeader> getModelHeaders() {
		List<ModelHeader> models = new ArrayList<ModelHeader>();
		if (psGetModelHeaders == null) {
			try {
				psGetModelHeaders = conn.prepareStatement("Select * from ModelHeader order by modelId");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		try {
			ResultSet rs = psGetModelHeaders.executeQuery();
			while (rs.next()) {
				models.add(convertModelHeaderFromResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return models;
	}

	@Override
	public ModelHeader updateModelHeader(ModelHeader modelHeaderIn) throws DatabaseException {

		if (psUpdateModelHeader == null) {
			try {
				psUpdateModelHeader = conn.prepareStatement(
						"Update ModelHeader Set modelId = ?, Description = ?, StandardQuantity = ?, Type = ?, IsDeleted = ?, deleteDate = ? "
								+ "WHERE ModelHeader.KEY = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		ModelHeader formattedModel = ModelFormatter.format(modelHeaderIn);
		String validateMessage = ModelValidator.validateUpdate(formattedModel, conn);
		if (!"".equals(validateMessage)) {
			throw new DatabaseException(validateMessage);
		}
		try {
			psUpdateModelHeader.setString(1, formattedModel.getModelId());
			psUpdateModelHeader.setString(2, formattedModel.getDescription());
			psUpdateModelHeader.setInt(3, formattedModel.getStandardQuantity());
			psUpdateModelHeader.setString(4, formattedModel.getModelType().getModelType());
			psUpdateModelHeader.setString(5, formattedModel.isDeleted() ? "T" : "F");
			psUpdateModelHeader.setDate(6, DateUtilities.convertUtilDateToSQLDate(formattedModel.getDeletedDate()));
			psUpdateModelHeader.setLong(7, formattedModel.getKey());
			int rowCount = psUpdateModelHeader.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Updated " + rowCount + " rows");
			}
			return getModelHeaderByKey(formattedModel.getKey());

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
	}

	@Override
	public ModelHeader addModelHeader(ModelHeader modelHeaderIn) throws DatabaseException {
		// Dupe Data Check

		if (modelHeaderIn == null) {
			throw new NullInputDataException("Model Header cannot be null");
		}
		ModelHeader formattedModel = ModelFormatter.format(modelHeaderIn);
		if (psAddModelHeaderDupeCheck == null) {
			try {
				psAddModelHeaderDupeCheck = conn.prepareStatement("Select * from ModelHeader where ModelID = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psAddModelHeaderDupeCheck.setString(1, formattedModel.getModelId());
			ResultSet rs = psAddModelHeaderDupeCheck.executeQuery();
			if (rs.next()) {
				throw new DuplicateDataException("Model ID is already in use");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
		if (psAddModelHeader == null) {
			try {
				psAddModelHeader = conn.prepareStatement(
						"Insert into ModelHeader (ModelId, Description, StandardQuantity, Type, IsDeleted, DeleteDate) values (?,?,?,?,?,?)",
						PreparedStatement.RETURN_GENERATED_KEYS);

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		String validateMessage = ModelValidator.validateAdd(formattedModel, conn);
		if (!"".equals(validateMessage)) {
			throw new DatabaseException(validateMessage);
		}
		try {
			psAddModelHeader.setString(1, formattedModel.getModelId());
			psAddModelHeader.setString(2, formattedModel.getDescription());
			psAddModelHeader.setInt(3, formattedModel.getStandardQuantity());
			psAddModelHeader.setString(4, formattedModel.getModelType().getModelType());
			psAddModelHeader.setString(5, formattedModel.isDeleted() ? "T" : "F");
			psAddModelHeader.setDate(6, DateUtilities.convertUtilDateToSQLDate(formattedModel.getDeletedDate()));
			int rowCount = psAddModelHeader.executeUpdate();

			if (rowCount != 1) {
				throw new DatabaseException("Error.  Inserted " + rowCount + " rows");
			}
			Employee newEmp = new Employee();
			try (ResultSet generatedKeys = psAddModelHeader.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					newEmp.setKey(generatedKeys.getLong(1));
					return getModelHeaderByKey(generatedKeys.getLong(1));
				} else {
					throw new SQLException("Creating user failed, no ID obtained.");
				}
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DuplicateDataException(e.getMessage(), e);
		}
	}

	@Override
	public void deleteModelByKey(long key) throws DatabaseException {
		// TODO Set isdeleted status to false instead?

		if (psDelModelByKey == null) {
			try {
				psDelModelByKey = conn.prepareStatement("Delete From Model where Model.Key = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psDelModelByKey.setLong(1, key);
			int rowCount = psDelModelByKey.executeUpdate();

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Model key " + key + " failed");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}

	}

	@Override
	public void deleteModel(String modelID) throws DatabaseException {
		if (modelID == null) {
			throw new NullInputDataException("empID cannot be null");
		}
		if (psDelModelByModelId == null) {
			try {
				psDelModelByModelId = conn.prepareStatement("Delete From Model where ModelID = ?");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DatabaseException(e.getMessage());
			}
		}
		try {
			psDelModelByModelId.setString(1, modelID);
			int rowCount = psDelModelByModelId.executeUpdate();

			if (rowCount == 0) {
				throw new DataNotFoundException("Error.  Delete Emp ID " + modelID + " failed");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public List<ModelDetail> getModelDetails(String modelId) throws DatabaseException {
		List<ModelDetail> details;
		if (psGetModelDetailByModelId == null) {
			try {
				psGetModelDetailByModelId = conn
						.prepareStatement("Select * from ModelDetail where ModelID = ? order by sequence");

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				throw new DataNotFoundException(e.getMessage());
			}
		}
		try {
			psGetModelDetailByModelId.setString(1, modelId);
			ResultSet rs = psGetModelDetailByModelId.executeQuery();
			details = convertModelDetailsFromResultSet(rs);
			if (details.isEmpty()) {
				throw new DataNotFoundException("Model ModelID " + modelId + " is not in DB");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new DataNotFoundException(e.getMessage(), e);
		}
		return details;
	}

	@Override
	public List<ModelDetail> updateModelDetails(ModelDetail modelDetail) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ModelDetail> addModelDetails(ModelDetail modelDetail) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model getModel(String modelId) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model updateModel(Model model) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model addModel(Model model) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	private ModelHeader convertModelHeaderFromResultSet(ResultSet rs) throws SQLException {
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

		return new ModelHeader(modelId, description, standardQuantity, modelType, key, deleted, deleteDate);
	}

	private List<ModelDetail> convertModelDetailsFromResultSet(ResultSet rs) throws SQLException {

		List<ModelDetail> details = new ArrayList<ModelDetail>();
		while (rs.next()) {
			String modelId = rs.getString("ModelID");
			String opCode = rs.getString("OpCode");
			int sequence = rs.getInt("Sequence");
			details.add(new ModelDetail(modelId, opCode, sequence));
		}
		return details;

	}
}
